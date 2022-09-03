package manager;

import Server.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.net.URI;
import java.util.*;

public class HTTPTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private String key;
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
        client.put("nonPriority", gson.toJson(this.notPriorityTasks));
    }

    public static HTTPTaskManager load() {
        Gson gson = new Gson();
        HTTPTaskManager httpTaskManager = new HTTPTaskManager(URI.create("http://localhost:8078"));
        httpTaskManager.tasks = gson.fromJson(httpTaskManager.client.load("task"), new TypeToken<HashMap<Integer, Task>>() {
        }.getType());
        httpTaskManager.epics = gson.fromJson(httpTaskManager.client.load("epic"), new TypeToken<HashMap<Integer, Epic>>() {
        }.getType());
        httpTaskManager.subTasks = gson.fromJson(httpTaskManager.client.load("subTask"), new TypeToken<HashMap<Integer, SubTask>>() {
        }.getType());
        httpTaskManager.priorityTasks = gson.fromJson(httpTaskManager.client.load("priority"), new TypeToken<Set<Task>>() {
        }.getType());
        httpTaskManager.notPriorityTasks = gson.fromJson(httpTaskManager.client.load("nonPriority"), new TypeToken<Set<Task>>() {
        }.getType());

        String jsonHistory = httpTaskManager.client.load("history");
        if (httpTaskManager.priorityTasks != null) {
            if (!jsonHistory.isEmpty()) {
                ArrayList<Task> history = gson.fromJson(jsonHistory, new TypeToken<ArrayList<Task>>() {
                }.getType());
                for (Task task : history) {
                    httpTaskManager.historyManager.add(task);
                }
            }
        }
        return httpTaskManager;
    }

    private int maxId() {
        int maxId;
        int maxTaskId = 0;
        int maxSubtaskId = 0;
        int maxEpicId = 0;
        for (int i : tasks.keySet()) {
            if (maxTaskId < i) {
                maxTaskId = i;
            }
        }
        for (int i : subTasks.keySet()) {
            if (maxSubtaskId < i) {
                maxSubtaskId = i;
            }
        }
        for (int i : epics.keySet()) {
            if (maxEpicId < i) {
                maxEpicId = i;
            }
        }
        maxId = Math.max(maxTaskId, maxSubtaskId);
        maxId = Math.max(maxId, maxEpicId);
        return maxId;
    }

    public void makeTaskWithId(Task task) {
        task.setId(maxId() + 1);
        tasks.put(task.getId(), task);
        super.validatorTimeTasks(task);
        super.setPriorityForTasks(task);
        save();
    }

    public void makeEpicWithId(Epic epic) {
        epic.setId(maxId() + 1);
        epic.setIdSubtasks(new ArrayList<>());
        epics.put(epic.getId(), epic);
        save();
    }

    public void makeSubtaskWithId(SubTask subTask) {
        subTask.setId(maxId() + 1);
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getIdEpic());
        epic.getIdSubtasks().add(subTask.getId());
        super.setStatusForEpics();
        super.setTimeAndDurationForEpic();
        super.setPriorityForTasks(subTask);
        super.validatorTimeTasks(subTask);
        save();
    }
}
