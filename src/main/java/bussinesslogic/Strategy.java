package bussinesslogic;

import model.*;

import java.util.*;

public interface Strategy {
    public void addTask(List<Server> servers, Task tasks);
}
