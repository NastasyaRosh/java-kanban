package Manager;

import Tasks.Epic;
import Tasks.SubTask;
import Tasks.Task;
import java.util.ArrayList;

public interface TaskManager {

    //Получение списка задач, подзадач и эпика
    ArrayList getListTasks();

    ArrayList getListSubtasks();

    ArrayList getListEpics();

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
    ArrayList getListSubtasksByEpic(int id);

    //Назначение статуса эпика
    void setStatusForEpics();

    //Получение истории
    HistoryManager getHistoryManager();

}