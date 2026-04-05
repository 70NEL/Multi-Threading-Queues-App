package bussinesslogic;

import gui.SimulationFrame;
import model.*;

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

    private Scheduler scheduler;
    private SimulationFrame simulationFrame;
    private List<Task> generatedTasks;

    public SimulationManager(int numberOfClients, int numberOfQueues, int timeLimit, int maxProcessingTime, int minProcessingTime, int maxArrivalTime, int minArrivalTime, SelectionPolicy selectionPolicy) {
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

        this.simulationFrame = new SimulationFrame();
        generateNRandomTasks();
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
        int currentTime = 0;
        float totalWaitingTime = 0;
        int peakHour = 0;
        int maxClientsAtOnce = 0;
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new FileWriter("log.txt"));

            while(currentTime <= timeLimit) {
                Iterator<Task> iterator = generatedTasks.iterator();
                while(iterator.hasNext()) {
                    Task tsk = iterator.next();
                    if(currentTime == tsk.getArrivalTime()) {
                        int temp = scheduler.dispatchTask(tsk);
                        tsk.setWaitingTimeInQueue(temp);
                        iterator.remove();
                    }
                }

                int currentNrOfClients = 0;
                for(Server server : scheduler.getServers()) {
                    currentNrOfClients += server.getTasks().size() + 1;
                }

                if(currentNrOfClients > maxClientsAtOnce) {
                    maxClientsAtOnce = currentNrOfClients;
                    peakHour = currentTime;
                }

                writer.println("Time: " + currentTime);
                writer.println("Waiting Clients: " + generatedTasks.toString());
                int svNr = 1;
                for(Server sv : scheduler.getServers()) {
                    String queueStatus = sv.getTasks().isEmpty() ? "Closed" : sv.getTasks().toString();
                    writer.println("Queue " + svNr + " : " + queueStatus);
                    svNr++;
                }

                writer.println();
                writer.flush();

                if(simulationFrame != null) {
                    //simulationFrame.updateView(scheduler.getServers(), currentTime);
                }

                currentTime++;
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            int finalFinishedCnt = 0;
            for(Server server : scheduler.getServers()) {
                totalWaitingTime += server.getTotalWaitTimeReal();
                finalFinishedCnt += server.getTotalFinishedClients();
            }

            float averageWaitingTime;
            if(finalFinishedCnt == 0) {
                averageWaitingTime = 0;
            }else {
                averageWaitingTime = totalWaitingTime / finalFinishedCnt;
            }
            writer.println("Average Waiting Time: " + averageWaitingTime);
            writer.println("Peak Hour: " + peakHour + ", Max Clients: " + maxClientsAtOnce);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if(writer != null) {
                writer.close();
            }
            scheduler.stopServers();
        }
    }
}
