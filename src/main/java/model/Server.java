package model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    BlockingQueue<Task> tasks;
    AtomicInteger waitingPeriod;
    AtomicInteger totalFinishedClients;
    AtomicInteger totalWaitTimeReal;

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
        while (true) {
            try {
                Task nextTask = tasks.take();

                for(int i = 0; i < nextTask.getServiceTime(); i++) {
                    Thread.sleep(1000);
                    waitingPeriod.decrementAndGet();
                }

                totalWaitTimeReal.addAndGet(nextTask.getWaitingTimeInQueue());
                totalFinishedClients.incrementAndGet();
                nextTask = null;

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
