package Manager;

import java.util.ArrayList;
import java.util.HashMap;

import Tasks.*;

public class Manager {
    private int ID = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    //Получение списка всех отдельных задач
    public ArrayList getListTasks() {
        return new ArrayList<>(tasks.values());
    }

    //Получение списка всех подзадач
    public ArrayList getListSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    //Получение списка всех эпиков
    public ArrayList getListEpics() {
        return new ArrayList<>(epics.values());
    }

    //Удаление всех задач
    public void deleteAllTasks() {
        tasks.clear();
    }

    //Удаление всех подзадач
    public void deleteAllSubTask() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getIdSubtasks().clear();
        }
        setStatusForEpics();
    }

    //Удаление всех эпиков и подзадач (т.к. подзадача не может существовать без эпика)
    public void deleteAllEpicsAndSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getIdSubtasks().clear();
        }
        epics.clear();
    }

    //Получить задачу по ИД
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    //Получить подзадачу по ИД
    public SubTask getSubtaskById(int id) {
        return subTasks.get(id);
    }

    //Получить эпик по ИД
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    //Создать задачу, эпик или подзадачу
    public void makeTask(Object obj) {
        if (obj instanceof Epic) {
            Epic epic = (Epic) obj;
            epic.setId(++ID);
            epics.put(epic.getId(epic), epic);
        } else if (obj instanceof SubTask) {
            SubTask subTask = (SubTask) obj;
            subTask.setId(++ID);
            subTasks.put(subTask.getId(subTask), subTask);
            Epic epic = epics.get(subTask.getIdEpic());
            epic.getIdSubtasks().add(subTask.getId(subTask));
        } else if (obj instanceof Task) {
            Task task = (Task) obj;
            task.setId(++ID);
            tasks.put(task.getId(task), task);
        }
        setStatusForEpics();
    }

    //Обновить задачу, эпик или подзадачу
    public void updateTask(Object obj) {
        if (obj instanceof Epic) {
            Epic epic = (Epic) obj;
            epics.put(epic.getId(epic), epic);
        } else if (obj instanceof SubTask) {
            SubTask subTask = (SubTask) obj;
            subTasks.put(subTask.getId(subTask), subTask);
        } else if (obj instanceof Task) {
            Task task = (Task) obj;
            tasks.put(task.getId(task), task);
        }
        setStatusForEpics();
    }

    //Удалить задачу по ИД
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    //Удалить подзадачу по ИД
    public void deleteSubtaskById(int id) {
        Object delId = id;
        epics.get(subTasks.get(id).getIdEpic()).getIdSubtasks().remove(delId);
        subTasks.remove(id);
        setStatusForEpics();
    }

    //Удалить эпик и все его подзадачи по ИД
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        for (int i : epic.getIdSubtasks()) {
            subTasks.remove(i);
        }
        epics.remove(id);
    }

    //Получить список всех подзадач эпика по его ИД
    public ArrayList getListSubtasksByEpic(int id) {
        ArrayList<SubTask> getSubtaskByEpic = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            if (subTask.getIdEpic() == id) {
                getSubtaskByEpic.add(subTask);
            }
        }
        return getSubtaskByEpic;
    }

    //Назначение статуса эпика
    public void setStatusForEpics() {
        for (Epic epic : epics.values()) {
            int counterNew = 0;
            int counterDone = 0;
            String subStatus;
            if (!epic.getIdSubtasks().isEmpty()) {
                for (int i = 0; i < epic.getIdSubtasks().size(); i++) {
                    subStatus = subTasks.get(epic.getIdSubtasks().get(i)).getStatus();
                    if (subStatus.equals("NEW")) {
                        counterNew++;
                    } else if (subStatus.equals("DONE")) {
                        counterDone++;
                    }
                }
                if (counterNew == epic.getIdSubtasks().size()) {
                    epic.setStatus("NEW");
                } else if (counterDone == epic.getIdSubtasks().size()) {
                    epic.setStatus("DONE");
                } else {
                    epic.setStatus("IN_PROGRESS");
                }
            } else {
                epic.setStatus("NEW");
            }
        }
    }

}