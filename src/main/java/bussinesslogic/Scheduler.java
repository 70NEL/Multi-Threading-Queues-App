package bussinesslogic;

import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scheduler {
    private List<Server> servers;
    private int maxNoServers;
    private int maxTasksPerServer;
    private Strategy strategy;
    private ExecutorService pool;

    public Scheduler(int maxNoServers, int maxTasksPerServer) {
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerServer;
        this.servers = new ArrayList<>();
        pool = Executors.newFixedThreadPool(maxNoServers);

        for (int i = 0; i < maxNoServers; i++) {
            Server srv = new  Server();
            servers.add(srv);
            pool.execute(srv);
        }
    }

    public void stopServers() {
        pool.shutdown();
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
