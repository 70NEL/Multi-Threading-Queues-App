package bussinesslogic;

import model.Server;
import model.Task;

import java.util.List;

public class ShortestTimeStrategy implements Strategy {
    public void addTask(List<Server> servers, Task tasks) {
        Server bestServer = servers.get(0);
        int bestTime = bestServer.getWaitingPeriod();

        for (Server srv : servers) {
            if(srv.getWaitingPeriod() < bestTime) {
                bestTime = srv.getWaitingPeriod();
                bestServer = srv;
            }
        }

        bestServer.addTask(tasks);
    }
}
