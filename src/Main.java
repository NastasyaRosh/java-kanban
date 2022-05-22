import Manager.*;
import Tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Помыть пол", "Используй Mister Proper", Statuses.NEW);
        Task task2 = new Task("Сходить в магазин", "Купить капусту", Statuses.IN_PROGRESS);
        Epic epic1 = new Epic("Книги", "На прочтение");
        Epic epic2 = new Epic("Счета", "На оплату");
        SubTask subTask1 = new SubTask("Карл Маркс \"Капитал\"", "Экономика", Statuses.NEW, 3);
        SubTask subTask2 = new SubTask("Карлос Кастанеда \"Путешествие в Икстлан\"", "Философия", Statuses.IN_PROGRESS, 3);
        SubTask subTask3 = new SubTask("Счет за газ", "557 рублей", Statuses.DONE, 4);

        manager.makeTask(task1);
        manager.makeTask(task2);
        manager.makeEpic(epic1);
        manager.makeEpic(epic2);
        manager.makeSubtask(subTask1);
        manager.makeSubtask(subTask2);
        manager.makeSubtask(subTask3);

        System.out.println("Задачи записаны. История вызовов:");
        System.out.println(manager.getHistoryManager().getHistory());

        Task task3 = new Task("Сходить в магазин", "Купить капусту", Statuses.DONE, 2);
        SubTask subTask4 = new SubTask("Джордж Оруэлл \"1984\"", "Художка", Statuses.NEW, 6, 3);

        manager.updateTask(task3);
        manager.updateSubtask(subTask4);
        System.out.println("Задача и подзадача обновлены. История вызовов:");
        System.out.println(manager.getHistoryManager().getHistory());

        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getEpicById(3);
        manager.getEpicById(4);
        manager.getSubtaskById(5);
        manager.getSubtaskById(6);
        manager.getSubtaskById(7);
        manager.getTaskById(2);
        manager.getEpicById(4);
        manager.getSubtaskById(5);
        manager.getSubtaskById(7);
        System.out.println("История после 11 вызовов:");
        System.out.println(manager.getHistoryManager().getHistory());

    }
}
