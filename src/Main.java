import manager.*;
import tasks.*;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        FileBackedTasksManager fileTasksManager = Managers.getDefaultFile("task.csv");
        Task task1 = new Task("Помыть пол", "Используй Mister Proper", Statuses.NEW, 15
                , LocalDateTime.of(2022, 7, 20, 12, 15));
        Epic epic1 = new Epic("Счета", "На оплату");
        SubTask subTask1 = new SubTask("Счет за воду", "2222 рубля", Statuses.NEW, 2, 30
                , LocalDateTime.of(2022, 4, 25, 10, 0));
        SubTask subTask2 = new SubTask("Счет за газ", "557 рублей", Statuses.IN_PROGRESS, 2, 120
                , LocalDateTime.of(2022, 10, 5, 10, 30));
        Task task2 = new Task("Сходить в магазин", "Купить капусту", Statuses.IN_PROGRESS);

        fileTasksManager.makeTask(task1);
        fileTasksManager.makeEpic(epic1);
        fileTasksManager.makeSubtask(subTask1);

        fileTasksManager.getTaskById(1);
        fileTasksManager.getEpicById(2);

        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile("task.csv");
        fileBackedTasksManager.makeSubtask(subTask2);
        fileBackedTasksManager.makeTask(task2);
        System.out.println(fileBackedTasksManager.getPrioritizedTasks());

    }
}
