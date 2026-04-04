package bussinesslogic;

import model.*;

import java.util.List;

public class Scheduler {
    private List<Server> servers;
    private int maxNoServer;
    private int maxTasksPerServer;
    private Strategy strategy;

    public void changeStrategy(SelectionPolicy selectionPolicy) {
        if(selectionPolicy == SelectionPolicy.SHORTEST_TIME) {
            this.strategy = new ShortestTimeStrategy();
        }else if(selectionPolicy == SelectionPolicy.SHORTEST_QUEUE) {
            this.strategy = new ShortestQueueStrategy();
        }
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

    public int getMaxNoServer() {
        return maxNoServer;
    }

    public void setMaxNoServer(int maxNoServer) {
        this.maxNoServer = maxNoServer;
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
