package bussinesslogic;

import model.*;

import java.util.*;

public interface Strategy {
    public int addTask(List<Server> servers, Task tasks);
}
