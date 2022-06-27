package manager;

import tasks.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static tasks.TasksType.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final String fileName;

    public FileBackedTasksManager(String fileName) {
        this.fileName = fileName;
        if (!Files.exists(Paths.get(fileName))) {
            try {
                Files.createFile(Paths.get(fileName));
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка сохранения данных." + e.getMessage());
            }
        }
    }

    private void save() {
        try (Writer fileWriter = new FileWriter(fileName)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task : getListTasks()) {
                fileWriter.write(toString(task));
            }
            for (Epic epic : getListEpics()) {
                fileWriter.write(toString(epic));
            }
            for (SubTask subTask : getListSubtasks()) {
                fileWriter.write(toString(subTask));
            }
            fileWriter.write("\n");
            fileWriter.write(toStringHistory(getHManager()));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных." + e.getMessage());
        }
    }

    public static FileBackedTasksManager loadFromFile(String fileName) {
        FileBackedTasksManager taskManager;
        try {
            String read = Files.readString(Path.of(fileName));
            String[] lines = read.split("\n");
            taskManager = new FileBackedTasksManager(fileName);
            for (int i = 1; i < lines.length; i++) {
                if (!lines[i].isBlank() && lines[i].contains(SUBTASK.toString())) {
                    Task task = taskManager.taskFromString(lines[i]);
                    taskManager.saveSubTask(task);
                } else if (!lines[i].isBlank() && lines[i].contains(TASK.toString())) {
                    Task task = taskManager.taskFromString(lines[i]);
                    taskManager.saveTask(task);
                } else if (!lines[i].isBlank() && lines[i].contains(EPIC.toString())) {
                    Task task = taskManager.taskFromString(lines[i]);
                    taskManager.saveEpic(task);
                } else if (!lines[i].isBlank()) {
                    for (int id : fromStringHistory(lines[i])) {
                        if ((taskManager.tasks.containsKey(id))) {
                            taskManager.historyManager.add(taskManager.tasks.get(id));
                        } else if (taskManager.epics.containsKey(id)) {
                            taskManager.historyManager.add(taskManager.epics.get(id));
                        } else if (taskManager.subTasks.containsKey(id)) {
                            taskManager.historyManager.add(taskManager.subTasks.get(id));
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных." + e.getMessage());
        }
        if (taskManager != null) {
            id = taskManager.maxId();
        }
        return taskManager;
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

    private static String toStringHistory(HistoryManager manager) {
        StringBuilder history = new StringBuilder();
        if (!manager.getHistory().isEmpty()) {
            for (Task task : manager.getHistory()) {
                history.append(task.getId());
                history.append(",");
            }
            history.deleteCharAt(history.lastIndexOf(","));
        }
        return history.toString();
    }

    private static List<Integer> fromStringHistory(String value) {
        List<Integer> historyFromFile = new ArrayList<>();
        if (!value.isEmpty()) {
            String[] history = value.split(",");
            for (String line : history) {
                historyFromFile.add(Integer.parseInt(line));
            }
        }
        return historyFromFile;
    }

    private String toString(Task task) {
        String taskToString;
        if (task instanceof SubTask) {
            taskToString = String.format("%d,%s,%s,%s,Description %s,%d%n", task.getId(), TasksType.SUBTASK, task.getName(), task.getStatus(), task.getDescription(), ((SubTask) task).getIdEpic());
        } else if (task instanceof Epic) {
            taskToString = String.format("%d,%s,%s,%s,Description %s,%n", task.getId(), TasksType.EPIC, task.getName(), task.getStatus(), task.getDescription());
        } else {
            taskToString = String.format("%d,%s,%s,%s,Description %s,%n", task.getId(), TasksType.TASK, task.getName(), task.getStatus(), task.getDescription());
        }
        return taskToString;
    }

    private Task taskFromString(String value) {
        Task task = null;
        if (!value.isEmpty()) {
            String[] line = value.split(",");
            if (line[1].equals(TASK.toString())) {
                task = new Task(line[2], line[4].substring(12), Statuses.valueOf(line[3]), Integer.parseInt(line[0]));
            } else if (line[1].equals(SUBTASK.toString())) {
                task = new SubTask(line[2], line[4].substring(12), Statuses.valueOf(line[3]), Integer.parseInt(line[0]), Integer.parseInt(line[5].trim()));
            } else if (line[1].equals(EPIC.toString())) {
                task = new Epic(line[2], line[4].substring(12), Statuses.valueOf(line[3]), Integer.parseInt(line[0]));
            }
        }
        return task;
    }

    @Override
    public void makeTask(Task task) {
        super.makeTask(task);
        save();
    }

    @Override
    public void makeEpic(Epic epic) {
        super.makeEpic(epic);
        save();
    }

    @Override
    public void makeSubtask(SubTask subTask) {
        super.makeSubtask(subTask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubTask() {
        super.deleteAllSubTask();
        save();
    }

    @Override
    public void deleteAllEpicsAndSubTasks() {
        super.deleteAllEpicsAndSubTasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        super.getTaskById(id);
        save();
        return super.getTaskById(id);
    }

    @Override
    public SubTask getSubtaskById(int id) {
        super.getSubtaskById(id);
        save();
        return super.getSubtaskById(id);
    }

    @Override
    public Epic getEpicById(int id) {
        super.getEpicById(id);
        save();
        return super.getEpicById(id);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(SubTask subTask) {
        super.updateSubtask(subTask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    private void saveTask(Task task) {
        tasks.put(task.getId(), task);
    }

    private void saveEpic(Task task) {
        epics.put(task.getId(), (Epic) task);
    }

    private void saveSubTask(Task task) {
        SubTask subTask = (SubTask) task;
        subTasks.put(task.getId(), subTask);
        Epic epic = epics.get(subTask.getIdEpic());
        ArrayList<Integer> idSubtasks = new ArrayList<>();
        idSubtasks.add(subTask.getId());
        epic.setIdSubtasks(idSubtasks);
    }

    public static void main(String[] args) {
        FileBackedTasksManager fileTasksManager = Managers.getDefaultFile("task.csv");
        Task task1 = new Task("Помыть пол", "Используй Mister Proper", Statuses.NEW);
        Epic epic1 = new Epic("Счета", "На оплату");
        SubTask subTask1 = new SubTask("Счет за воду", "2222 рубля", Statuses.NEW, 2);
        SubTask subTask2 = new SubTask("Счет за газ", "557 рублей", Statuses.IN_PROGRESS, 2);

        fileTasksManager.makeTask(task1);
        fileTasksManager.makeEpic(epic1);
        fileTasksManager.makeSubtask(subTask1);

        fileTasksManager.getTaskById(1);
        fileTasksManager.getEpicById(2);

        FileBackedTasksManager fileBackedTasksManager = loadFromFile("task.csv");
        fileBackedTasksManager.makeSubtask(subTask2);
        System.out.println(fileBackedTasksManager.getTaskById(1));
        System.out.println(fileBackedTasksManager.historyManager.getHistory());
        System.out.println(fileBackedTasksManager.getEpicById(2).getIdSubtasks());
    }

}