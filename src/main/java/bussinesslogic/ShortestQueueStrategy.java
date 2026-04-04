package bussinesslogic;

import model.Server;
import model.Task;

import java.util.List;

public class ShortestQueueStrategy implements Strategy {
    @Override
    public void addTask(List<Server> servers, Task task) {
        if(servers == null || servers.isEmpty() || task == null) {
            return;
        }

        Server bestServer = servers.get(0);
        int minTasks = bestServer.getTasks().size();

        for(Server srv : servers) {
            if(srv.getTasks().size() < minTasks) {
                bestServer = srv;
                minTasks = srv.getTasks().size();
            }
        }
        bestServer.addTask(task);
    }
}
