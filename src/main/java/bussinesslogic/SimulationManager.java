package bussinesslogic;

import gui.SimulationFrame;
import model.*;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class SimulationManager implements Runnable {
    public int numberOfClients;
    public int numberOfQueues;
    public int timeLimit;
    public int maxProcessingTime;
    public int minProcessingTime;
    public int maxArrivalTime;
    public int minArrivalTime;
    public SelectionPolicy selectionPolicy;
    public float averageWaitingTime;
    public int peakHour;
    public int maxClientsAtOnce;
    public int currentTime = 0;

    private Scheduler scheduler;
    private SimulationFrame simulationFrame;
    private List<Task> generatedTasks;

    public SimulationManager(SimulationFrame simulationFrame, int numberOfClients, int numberOfQueues, int timeLimit, int maxProcessingTime, int minProcessingTime, int maxArrivalTime, int minArrivalTime, SelectionPolicy selectionPolicy) {
        this.simulationFrame = simulationFrame;
        this.numberOfClients = numberOfClients;
        this.numberOfQueues = numberOfQueues;
        this.timeLimit = timeLimit;
        this.maxProcessingTime = maxProcessingTime;
        this.minProcessingTime = minProcessingTime;
        this.maxArrivalTime = maxArrivalTime;
        this.minArrivalTime = minArrivalTime;
        this.selectionPolicy = selectionPolicy;

        scheduler = new Scheduler(numberOfQueues, numberOfClients/numberOfQueues + 1);
        scheduler.changeStrategy(selectionPolicy);
        generateNRandomTasks();
    }

    public SimulationManager() {

    }

    private void generateNRandomTasks() {
        this.generatedTasks = new ArrayList<>();
        Random rand = new Random();
        for (int i = 1; i <= numberOfClients; i++) {
            int arrival = rand.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
            int serviceTime = rand.nextInt(maxProcessingTime -  minProcessingTime + 1) + minProcessingTime;
            generatedTasks.add(new Task(i, arrival, serviceTime));
        }

        generatedTasks.sort(Comparator.comparingInt(Task::getArrivalTime));
    }

    @Override
    public void run() {
        float totalWaitingTime = 0;
        peakHour = 0;
        maxClientsAtOnce = 0;

        try (PrintWriter writer = new PrintWriter(new FileWriter("log.txt"))) {
            while (currentTime <= timeLimit) {
                Iterator<Task> iterator = generatedTasks.iterator();
                while (iterator.hasNext()) {
                    Task tsk = iterator.next();
                    if (currentTime == tsk.getArrivalTime()) {
                        int temp = scheduler.dispatchTask(tsk);
                        tsk.setWaitingTimeInQueue(temp);
                        iterator.remove();
                    }
                }

                final List<Server> currentServers = scheduler.getServers();
                final List<Task> currentWaiting = new ArrayList<>(generatedTasks);
                final int displayTime = currentTime;

                SwingUtilities.invokeLater(() -> {
                    if (simulationFrame != null && simulationFrame.getVisualPanel() != null) {
                        simulationFrame.getVisualPanel().updateData(currentServers, currentWaiting, displayTime);
                    }
                });

                int currentNrOfClients = 0;
                for (Server server : currentServers) {
                    currentNrOfClients += server.getTasks().size();
                }

                if (currentNrOfClients > maxClientsAtOnce) {
                    maxClientsAtOnce = currentNrOfClients;
                    peakHour = currentTime;
                }

                writer.println("Time: " + currentTime);
                writer.println("Waiting Clients: " + generatedTasks.toString());
                int svNr = 1;
                for (Server sv : currentServers) {
                    String queueStatus = sv.getTasks().isEmpty() ? "Closed" : sv.getTasks().toString();
                    writer.println("Queue " + svNr + " : " + queueStatus);
                    svNr++;
                }
                writer.println();
                writer.flush();

                Thread.sleep(1000);
                currentTime++;
            }

            int finalFinishedCnt = 0;
            for (Server server : scheduler.getServers()) {
                totalWaitingTime += server.getTotalWaitTimeReal();
                finalFinishedCnt += server.getTotalFinishedClients();
            }

            averageWaitingTime = (finalFinishedCnt == 0) ? 0 : totalWaitingTime / finalFinishedCnt;
            writer.println("Average Waiting Time: " + averageWaitingTime);
            writer.println("Peak Hour: " + peakHour + ", Max Clients: " + maxClientsAtOnce);
            writer.flush();

            simulationFrame.onSimulationFinished(averageWaitingTime, peakHour, maxClientsAtOnce);

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            scheduler.stopServers();
        }
    }
}
