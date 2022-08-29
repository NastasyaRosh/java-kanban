package manager;

import tasks.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static tasks.TasksType.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final String fileName;
    protected int id = 0;

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

    public FileBackedTasksManager() {
        fileName = null;
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(fileName)) {
            fileWriter.write("id,type,name,status,description,epic,duration,startTime\n");
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
        taskManager.maxId();
        return taskManager;
    }

    private void maxId() {
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
        id = maxId;
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
            taskToString = String.format("%d,%s,%s,%s,Description %s,%d,%d,%s%n", task.getId(), TasksType.SUBTASK
                    , task.getName(), task.getStatus(), task.getDescription(), ((SubTask) task).getIdEpic()
                    , task.getDuration(), task.getStartTimeFormat());
        } else if (task instanceof Epic) {
            taskToString = String.format("%d,%s,%s,%s,Description %s,%d,%s%n", task.getId(), TasksType.EPIC
                    , task.getName(), task.getStatus(), task.getDescription(), task.getDuration()
                    , task.getStartTimeFormat());
        } else {
            taskToString = String.format("%d,%s,%s,%s,Description %s,%d,%s%n", task.getId(), TasksType.TASK
                    , task.getName(), task.getStatus(), task.getDescription(), task.getDuration(), task.getStartTimeFormat());
        }
        return taskToString;
    }

    private Task taskFromString(String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm|dd.MM.yyyy");
        Task task = null;
        if (!value.isEmpty()) {
            String[] line = value.split(",");
            if (line[1].equals(TASK.toString())) {
                task = new Task(line[2], line[4].substring(12), Statuses.valueOf(line[3]), Integer.parseInt(line[0])
                        , line[5], line[6].trim());
            } else if (line[1].equals(SUBTASK.toString())) {
                task = new SubTask(line[2], line[4].substring(12), Statuses.valueOf(line[3]), Integer.parseInt(line[0])
                        , Integer.parseInt(line[5].trim()), line[6], line[7].trim());
            } else if (line[1].equals(EPIC.toString())) {
                task = new Epic(line[2], line[4].substring(12), Statuses.valueOf(line[3]), Integer.parseInt(line[0])
                        , line[5], line[6].trim());
            }
        }
        return task;
    }

    @Override
    public void makeTask(Task task) {
        task.setId(setCommonId());
        tasks.put(task.getId(), task);
        super.validatorTimeTasks(task);
        super.setPriorityForTasks(task);
        save();
    }

    @Override
    public void makeEpic(Epic epic) {
        epic.setId(setCommonId());
        epics.put(epic.getId(), epic);
        save();
    }

    @Override
    public void makeSubtask(SubTask subTask) {
        subTask.setId(setCommonId());
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getIdEpic());
        epic.getIdSubtasks().add(subTask.getId());
        super.setStatusForEpics();
        super.setTimeAndDurationForEpic();
        super.setPriorityForTasks(subTask);
        super.validatorTimeTasks(subTask);
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

    private int setCommonId() {
        return ++id;
    }

    private void saveTask(Task task) {
        tasks.put(task.getId(), task);
        setPriorityForTasks(task);
    }

    private void saveEpic(Task task) {
        Epic epic = (Epic) task;
        epic.setIdSubtasks(new ArrayList<>());
        epics.put(task.getId(), epic);
    }

    private void saveSubTask(Task task) {
        SubTask subTask = (SubTask) task;
        subTasks.put(task.getId(), subTask);
        Epic epic = epics.get(subTask.getIdEpic());
        ArrayList<Integer> idSubtasks = new ArrayList<>();
        idSubtasks.add(subTask.getId());
        epic.setIdSubtasks(idSubtasks);
        setPriorityForTasks(task);
    }

}
