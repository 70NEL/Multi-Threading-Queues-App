package bussinesslogic;

import model.*;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private List<Server> servers;
    private int maxNoServers;
    private int maxTasksPerServer;
    private Strategy strategy;
    private List<Thread> serverThreads;

    public Scheduler(int maxNoServers, int maxTasksPerServer) {
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerServer;
        this.servers = new ArrayList<>();

        for (int i = 0; i < maxNoServers; i++) {
            Server srv = new  Server();
            servers.add(srv);
            Thread t = new Thread(srv);
            serverThreads.add(t);
            t.start();
        }
    }

    public void stopServers() {
        for (Thread t : serverThreads) {
            t.interrupt();
        }
    }

    public void changeStrategy(SelectionPolicy selectionPolicy) {
        if(selectionPolicy == SelectionPolicy.SHORTEST_TIME) {
            this.strategy = new ShortestTimeStrategy();
        }else if(selectionPolicy == SelectionPolicy.SHORTEST_QUEUE) {
            this.strategy = new ShortestQueueStrategy();
        }
    }

    public int dispatchTask(Task task) {
        return strategy.addTask(servers, task);
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
