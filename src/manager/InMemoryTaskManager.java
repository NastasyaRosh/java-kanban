package manager;

import java.time.LocalDateTime;
import java.util.*;

import tasks.*;

public class InMemoryTaskManager implements TaskManager {
    Comparator<Task> comparator = (t1, t2) -> {
        int res;
        if (t1.getStartTime().isBefore(t2.getStartTime())) {
            res = -1;
        } else if (t1.getStartTime().equals(t2.getStartTime())) {
            res = 0;
        } else {
            res = 1;
        }
        return res;
    };

    protected int id = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    public final HistoryManager historyManager = Managers.getDefaultHistory();
    protected Set<Task> priorityTasks = new TreeSet<>(comparator);
    protected Set<Task> notPriorityTasks = new HashSet<>();

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
        for (Task task : tasks.values()) {
            deleteFromPriorityList(task);
        }
        tasks.clear();
    }

    //Удаление всех подзадач
    @Override
    public void deleteAllSubTask() {
        for (SubTask subTask : subTasks.values()) {
            deleteFromPriorityList(subTask);
        }
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getIdSubtasks().clear();
            epic.setDuration(0);
            epic.setStartTime(null);
            epic.setEndTime(null);
        }
        setStatusForEpics();
    }

    //Удаление всех эпиков и подзадач (т.к. подзадача не может существовать без эпика)
    @Override
    public void deleteAllEpicsAndSubTasks() {
        for (SubTask subTask : subTasks.values()) {
            deleteFromPriorityList(subTask);
        }
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
        tasks.put(task.getId(), task);
        setPriorityForTasks(task);
        validatorTimeTasks(task);
    }

    //Создать эпик
    @Override
    public void makeEpic(Epic epic) {
        epic.setId(setCommonId());
        epics.put(epic.getId(), epic);
    }

    //Создать подзадачу
    @Override
    public void makeSubtask(SubTask subTask) {
        subTask.setId(setCommonId());
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getIdEpic());
        epic.getIdSubtasks().add(subTask.getId());
        setStatusForEpics();
        setTimeAndDurationForEpic();
        setPriorityForTasks(subTask);
        validatorTimeTasks(subTask);
    }

    //Обновить задачу
    @Override
    public void updateTask(Task task) {
        deleteFromPriorityList(tasks.get(task.getId()));
        tasks.put(task.getId(), task);
        validatorTimeTasks(task);
        setPriorityForTasks(task);
    }

    //Обновить эпик
    @Override
    public void updateEpic(Epic epic) {
        epic.setIdSubtasks(epics.get(epic.getId()).getIdSubtasks());
        epics.put(epic.getId(), epic);
        setStatusForEpics();
        setTimeAndDurationForEpic();
    }

    //Обновить подзадачу
    @Override
    public void updateSubtask(SubTask subTask) {
        deleteFromPriorityList(subTasks.get(subTask.getId()));
        subTasks.put(subTask.getId(), subTask);
        setStatusForEpics();
        setTimeAndDurationForEpic();
        setPriorityForTasks(subTask);
        validatorTimeTasks(subTask);
    }

    //Удалить задачу по ИД
    @Override
    public void deleteTaskById(int id) {
        deleteFromPriorityList(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    //Удалить подзадачу по ИД
    @Override
    public void deleteSubtaskById(int id) {
        deleteFromPriorityList(subTasks.get(id));
        Object delId = id;
        epics.get(subTasks.get(id).getIdEpic()).getIdSubtasks().remove(delId);
        subTasks.remove(id);
        historyManager.remove(id);
        setStatusForEpics();
        setTimeAndDurationForEpic();
    }

    //Удалить эпик и все его подзадачи по ИД
    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        for (int i : epic.getIdSubtasks()) {
            deleteFromPriorityList(subTasks.get(i));
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
    void setStatusForEpics() {
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

    //Расчет продолжительности, времени начала и окончания эпика
    public void setTimeAndDurationForEpic() {
        int durationEpic = 0;
        LocalDateTime earlyStartSubtask = LocalDateTime.of(2100, 1, 1, 0, 0);
        LocalDateTime lastEndSubtask = LocalDateTime.of(1900, 1, 1, 0, 0);
        for (Epic epic : epics.values()) {
            if (epic.getIdSubtasks().size() != 0) {
                for (Integer i : epic.getIdSubtasks()) {
                    if (subTasks.get(i).getDuration() != null) {
                        durationEpic += subTasks.get(i).getDuration();
                    }
                    if ((subTasks.get(i).getStartTime() != null) && (subTasks.get(i).getStartTime().isBefore(earlyStartSubtask))) {
                        earlyStartSubtask = subTasks.get(i).getStartTime();
                    }
                    if ((subTasks.get(i).getStartTime() != null) && (subTasks.get(i).getEndTime().isAfter(lastEndSubtask))) {
                        lastEndSubtask = subTasks.get(i).getEndTime();
                    }
                }
            }
            if (durationEpic != 0) {
                epic.setDuration(durationEpic);
            } else epic.setDuration(null);
            if (earlyStartSubtask.equals(LocalDateTime.of(2100, 1, 1, 0, 0))) {
                epic.setStartTime(null);
            } else {
                epic.setStartTime(earlyStartSubtask);
                epic.setEndTime(lastEndSubtask);
            }
        }
    }

    private int setCommonId() {
        return ++this.id;
    }

    public HistoryManager getHManager() {
        return historyManager;
    }

    protected void setPriorityForTasks(Task task) {
        if (task.getStartTime() != null) {
            priorityTasks.add(task);
        } else {
            notPriorityTasks.add(task);
        }
    }

    private void deleteFromPriorityList(Task task) {
        if (task.getStartTime() != null) {
            priorityTasks.remove(task);
        } else {
            notPriorityTasks.remove(task);
        }
    }

    public List<Task> getPrioritizedTasks() {
        List<Task> prioritisedList = new ArrayList<>(priorityTasks);
        prioritisedList.addAll(notPriorityTasks);
        return prioritisedList;
    }

    protected void validatorTimeTasks(Task task) {
        for (Task priorityTask : priorityTasks) {
            if (task.getStartTime() != null) {
                if (task.getId() != priorityTask.getId()) {
                    if (!task.getStartTime().isBefore(priorityTask.getStartTime()) && !task.getStartTime().isAfter(priorityTask.getEndTime()) ||
                            !priorityTask.getStartTime().isBefore(task.getStartTime()) && !priorityTask.getStartTime().isAfter(task.getEndTime())) {
                        throw new IllegalArgumentException("Даты пересекаются у задач с номерами: " + priorityTask.getId() + " и " + task.getId());
                    }
                }
            }
        }
    }

}
