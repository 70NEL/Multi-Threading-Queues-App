package model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;
    private AtomicInteger totalFinishedClients;
    private AtomicInteger totalWaitTimeReal;

    public Server() {
        this.tasks = new LinkedBlockingDeque<>();
        this.waitingPeriod = new AtomicInteger(0);
        this.totalFinishedClients = new AtomicInteger(0);
        this.totalWaitTimeReal = new AtomicInteger(0);
    }

    public void addTask(Task newtask) {
        tasks.add(newtask);
        waitingPeriod.addAndGet(newtask.getServiceTime());
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Task nextTask = tasks.peek();

                if (nextTask != null) {
                    int duration = nextTask.getServiceTime();

                    for (int i = 0; i < duration; i++) {
                        Thread.sleep(1000);
                        nextTask.setServiceTime(nextTask.getServiceTime() - 1);
                        waitingPeriod.decrementAndGet();
                    }

                    totalWaitTimeReal.addAndGet(nextTask.getWaitingTimeInQueue());
                    totalFinishedClients.incrementAndGet();

                    tasks.poll();
                } else {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public BlockingQueue<Task> getTasks() {
        return tasks;
    }

    public int getWaitingPeriod() {
        return waitingPeriod.get();
    }

    public int getTotalFinishedClients() {
        return totalFinishedClients.get();
    }

    public int getTotalWaitTimeReal() {
        return totalWaitTimeReal.get();
    }
}