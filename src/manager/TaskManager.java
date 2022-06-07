package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    //Получение списка задач, подзадач и эпика
    List<Task> getListTasks();

    List<SubTask> getListSubtasks();

    List<Epic> getListEpics();

    //Удаление всех задач / подзадач / эпиков и подзадач
    void deleteAllTasks();

    void deleteAllSubTask();

    void deleteAllEpicsAndSubTasks();

    //Получить задачу / подзадачу / эпик по ИД
    Task getTaskById(int id);

    SubTask getSubtaskById(int id);

    Epic getEpicById(int id);

    //Создать задачу / эпик / подзадачу
    void makeTask(Task task);

    void makeEpic(Epic epic);

    void makeSubtask(SubTask subTask);

    //Обновить задачу / эпик / подзадачу
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(SubTask subTask);

    //Удалить задачу / подзадачу / эпик и его подзадачи по ИД
    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    //Получить список всех подзадач эпика по его ИД
    List<SubTask> getListSubtasksByEpic(int id);

    //Получение истории
    List<Task> getHistoryManager();

}