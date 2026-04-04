package bussinesslogic;

import model.*;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private List<Server> servers;
    private int maxNoServers;
    private int maxTasksPerServer;
    private Strategy strategy;

    public Scheduler(int maxNoServers, int maxTasksPerServer) {
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerServer;
        this.servers = new ArrayList<>();

        for (int i = 0; i < maxNoServers; i++) {
            servers.add(new Server());
            Thread thread = new Thread(servers.get(i));
            thread.start();
        }
    }

    public void changeStrategy(SelectionPolicy selectionPolicy) {
        if(selectionPolicy == SelectionPolicy.SHORTEST_TIME) {
            this.strategy = new ShortestTimeStrategy();
        }else if(selectionPolicy == SelectionPolicy.SHORTEST_QUEUE) {
            this.strategy = new ShortestQueueStrategy();
        }
    }

    public int getMinWaitingTime() {
        int min = Integer.MAX_VALUE;
        for (Server s : servers) {
            if (s.getWaitingPeriod() < min) {
                min = s.getWaitingPeriod();
            }
        }
        return min;
    }

    public void dispatchTask(Task task) {
        strategy.addTask(servers, task);
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }

    public int getMaxNoServers() {
        return maxNoServers;
    }

    public void setMaxNoServer(int maxNoServers) {
        this.maxNoServers = maxNoServers;
    }

    public int getMaxTasksPerServer() {
        return maxTasksPerServer;
    }

    public void setMaxTasksPerServer(int maxTasksPerServer) {
        this.maxTasksPerServer = maxTasksPerServer;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

}
