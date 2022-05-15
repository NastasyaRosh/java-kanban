import java.util.ArrayList;
import java.util.HashMap;

/*
Уточнения по ТЗ смотри в README.md !!!
 */

public class Manager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, SubTask> subTasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();

    //Получение списка всех отдельных задач
    public ArrayList getListTasks() {
        ArrayList<Task> addListTask = new ArrayList<>();
        for (Task task : tasks.values()) {
            addListTask.add(task);
        }
        return addListTask;
    }

    //Получение списка всех подзадач
    public ArrayList getListSubtasks() {
        ArrayList<SubTask> addListSubtask = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            addListSubtask.add(subTask);
        }
        return addListSubtask;
    }

    //Получение списка всех эпиков
    public ArrayList getListEpics() {
        ArrayList<Epic> addListEpic = new ArrayList<>();
        for (Epic epic : epics.values()) {
            addListEpic.add(epic);
        }
        return addListEpic;
    }

    //Удаление всех задач
    public void deleteAllTasks() {
        tasks.clear();
    }

    //Удаление всех подзадач
    public void deleteAllSubTask() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.idSubtasks.clear();
        }
    }

    //Удаление всех эпиков и подзадач (т.к. подзадача не может существовать без эпика)
    public void deleteAllEpicsAndSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.idSubtasks.clear();
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
        if (obj.getClass() == Task.class) {
            Task task = (Task) obj;
            tasks.put(task.getId(task), task);
        } else if (obj.getClass() == Epic.class) {
            Epic epic = (Epic) obj;
            epics.put(epic.getId(epic), epic);
        } else if (obj.getClass() == SubTask.class) {
            SubTask subTask = (SubTask) obj;
            subTasks.put(subTask.getId(subTask), subTask);
            Epic epic = epics.get(subTask.idEpic);
            epic.idSubtasks.add(subTask.getId(subTask));
        }
    }

    //Обновить задачу, эпик или подзадачу
    public void updateTask(Object obj) {
        if (obj.getClass() == Task.class) {
            Task task = (Task) obj;
            tasks.put(task.getId(task), task);
        } else if (obj.getClass() == Epic.class) {
            Epic epic = (Epic) obj;
            epics.put(epic.getId(epic), epic);
        } else if (obj.getClass() == SubTask.class) {
            SubTask subTask = (SubTask) obj;
            subTasks.put(subTask.getId(subTask), subTask);
        }
    }

    //Удалить задачу по ИД
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    //Удалить подзадачу по ИД
    public void deleteSubtaskById(int id) {
        Object delId = id;
        epics.get(subTasks.get(id).idEpic).idSubtasks.remove(delId);
        subTasks.remove(id);
    }

    //Удалить эпик и все его подзадачи по ИД
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        for (int i : epic.idSubtasks) {
            subTasks.remove(i);
        }
        epics.remove(id);
    }

    //Получить список всех подзадач эпика по его ИД
    public ArrayList getListSubtasksByEpic(int id) {
        ArrayList<SubTask> getSubtaskByEpic = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            if (subTask.idEpic == id) {
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
            if (!epic.idSubtasks.isEmpty()) {
                for (int i = 0; i < epic.idSubtasks.size(); i++) {
                    subStatus = subTasks.get(epic.idSubtasks.get(i)).status;
                    if (subStatus.equals("NEW")) {
                        counterNew++;
                    } else if (subStatus.equals("DONE")) {
                        counterDone++;
                    }
                }
                if (counterNew == epic.idSubtasks.size()) {
                    epic.status = "NEW";
                } else if (counterDone == epic.idSubtasks.size()) {
                    epic.status = "DONE";
                } else {
                    epic.status = "IN_PROGRESS";
                }
            } else {
                epic.status = "NEW";
            }
        }
    }

}

