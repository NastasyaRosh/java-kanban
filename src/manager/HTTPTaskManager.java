package manager;

import Server.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HTTPTaskManager extends FileBackedTasksManager {
    KVTaskClient client;
    String key;
    Gson gson = new Gson();

    public HTTPTaskManager(URI url) {
        this.client = new KVTaskClient(url);
        this.key = "DEBUG";
    }

    @Override
    public void save() {
        client.put("task", gson.toJson(this.tasks));
        client.put("epic", gson.toJson(this.epics));
        client.put("subTask", gson.toJson(this.subTasks));
        client.put("history", gson.toJson(this.getHistoryManager()));
        client.put("priority", gson.toJson(this.priorityTasks));
    }

    //Этот лоад не работает
    public static HTTPTaskManager load() {
        Gson gson = new Gson();
        HTTPTaskManager httpTaskManager = new HTTPTaskManager(URI.create("http://localhost:8078"));
        httpTaskManager.tasks = gson.fromJson(httpTaskManager.client.load("task"), new TypeToken<HashMap<Integer, Task>>(){}.getType());
        httpTaskManager.epics = gson.fromJson(httpTaskManager.client.load("epic"), new TypeToken<HashMap<Integer, Epic>>(){}.getType());
        httpTaskManager.subTasks = gson.fromJson(httpTaskManager.client.load("subTask"), new TypeToken<HashMap<Integer, SubTask>>(){}.getType());
        httpTaskManager.historyManager = gson.fromJson(httpTaskManager.client.load("history"), new TypeToken<Map<Integer, Node<Task>>>(){}.getType());
        httpTaskManager.priorityTasks = gson.fromJson(httpTaskManager.client.load("priority"), new TypeToken<Set<Task>>(){}.getType());
        return httpTaskManager;
    }
}
