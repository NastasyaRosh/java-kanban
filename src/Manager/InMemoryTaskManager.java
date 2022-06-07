package Manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Tasks.*;

public class InMemoryTaskManager implements TaskManager {
    private int id = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    //Получение списка всех отдельных задач
    @Override
    public List<Task> getListTasks() {
        return new ArrayList<>(tasks.values());
    }

    //Получение списка всех подзадач
    @Override
    public List<SubTask> getListSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    //Получение списка всех эпиков
    @Override
    public List<Epic> getListEpics() {
        return new ArrayList<>(epics.values());
    }

    //Удаление всех задач
    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    //Удаление всех подзадач
    @Override
    public void deleteAllSubTask() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getIdSubtasks().clear();
        }
        setStatusForEpics();
    }

    //Удаление всех эпиков и подзадач (т.к. подзадача не может существовать без эпика)
    @Override
    public void deleteAllEpicsAndSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getIdSubtasks().clear();
        }
        epics.clear();
    }

    //Получить задачу по ИД
    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    //Получить подзадачу по ИД
    @Override
    public SubTask getSubtaskById(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    //Получить эпик по ИД
    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    //Создать задачу
    @Override
    public void makeTask(Task task) {
        task.setId(setCommonId());
        tasks.put(task.getId(task), task);
    }

    //Создать эпик
    @Override
    public void makeEpic(Epic epic) {
        epic.setId(setCommonId());
        epics.put(epic.getId(epic), epic);
    }

    //Создать подзадачу
    @Override
    public void makeSubtask(SubTask subTask) {
        subTask.setId(setCommonId());
        subTasks.put(subTask.getId(subTask), subTask);
        Epic epic = epics.get(subTask.getIdEpic());
        epic.getIdSubtasks().add(subTask.getId(subTask));
        setStatusForEpics();
    }

    //Обновить задачу
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(task), task);
    }

    //Обновить эпик
    @Override
    public void updateEpic(Epic epic) {
        epic.setIdSubtasks(epics.get(epic.getId(epic)).getIdSubtasks());
        epics.put(epic.getId(epic), epic);
        setStatusForEpics();
    }

    //Обновить подзадачу
    @Override
    public void updateSubtask(SubTask subTask) {
        subTasks.put(subTask.getId(subTask), subTask);
        setStatusForEpics();
    }

    //Удалить задачу по ИД
    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    //Удалить подзадачу по ИД
    @Override
    public void deleteSubtaskById(int id) {
        Object delId = id;
        epics.get(subTasks.get(id).getIdEpic()).getIdSubtasks().remove(delId);
        subTasks.remove(id);
        historyManager.remove(id);
        setStatusForEpics();
    }

    //Удалить эпик и все его подзадачи по ИД
    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        for (int i : epic.getIdSubtasks()) {
            subTasks.remove(i);
            historyManager.remove(i);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    //Получить список всех подзадач эпика по его ИД
    @Override
    public List<SubTask> getListSubtasksByEpic(int id) {
        ArrayList<SubTask> getSubtaskByEpic = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            if (subTask.getIdEpic() == id) {
                getSubtaskByEpic.add(subTask);
            }
        }
        return getSubtaskByEpic;
    }

    @Override
    public List<Task> getHistoryManager() {
        return historyManager.getHistory();
    }

    //Назначение статуса эпика
    private void setStatusForEpics() {
        for (Epic epic : epics.values()) {
            int counterNew = 0;
            int counterDone = 0;
            Statuses subStatus;
            if (!epic.getIdSubtasks().isEmpty()) {
                for (int i = 0; i < epic.getIdSubtasks().size(); i++) {
                    subStatus = subTasks.get(epic.getIdSubtasks().get(i)).getStatus();
                    if (Statuses.NEW.equals(subStatus)) {
                        counterNew++;
                    } else if (Statuses.DONE.equals(subStatus)) {
                        counterDone++;
                    }
                }
                if (counterNew == epic.getIdSubtasks().size()) {
                    epic.setStatus(Statuses.NEW);
                } else if (counterDone == epic.getIdSubtasks().size()) {
                    epic.setStatus(Statuses.DONE);
                } else {
                    epic.setStatus(Statuses.IN_PROGRESS);
                }
            } else {
                epic.setStatus(Statuses.NEW);
            }
        }
    }

    private int setCommonId() {
        return ++this.id;
    }

}