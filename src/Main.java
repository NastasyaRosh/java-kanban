import Manager.Manager;
import Tasks.*;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        Task task1 = new Task("Помыть пол", "Используй Mister Proper", "NEW");
        Task task2 = new Task("Сходить в магазин", "Купить капусту", "IN PROGRESS");
        Epic epic1 = new Epic("Книги", "На прочтение");
        Epic epic2 = new Epic("Счета", "На оплату");
        SubTask subTask1 = new SubTask("Карл Маркс \"Капитал\"", "Экономика", "NEW", 3);
        SubTask subTask2 = new SubTask("Карлос Кастанеда \"Путешествие в Икстлан\"", "Философия", "IN_PROGRESS", 3);
        SubTask subTask3 = new SubTask("Счет за газ", "557 рублей", "DONE", 4);

        manager.makeTask(task1);
        manager.makeTask(task2);
        manager.makeEpic(epic1);
        manager.makeEpic(epic2);
        manager.makeSubtask(subTask1);
        manager.makeSubtask(subTask2);
        manager.makeSubtask(subTask3);

        System.out.println("Список всех отдельных задач:");
        System.out.println(manager.getListTasks());
        System.out.println("Список всех эпиков:");
        System.out.println(manager.getListEpics());
        System.out.println("Список всех подзадач:");
        System.out.println(manager.getListSubtasks());
        System.out.println();

        Task task3 = new Task("Сходить в магазин", "Купить капусту", "DONE", 2);
        SubTask subTask4 = new SubTask("Джордж Оруэлл \"1984\"", "Художка", "NEW", 6, 3);

        manager.updateTask(task3);
        manager.updateSubtask(subTask4);
        System.out.println("Задача и подзадача обновлены.\n");

        System.out.println("Список всех отдельных задач:");
        System.out.println(manager.getListTasks());
        System.out.println("Список всех эпиков:");
        System.out.println(manager.getListEpics());
        System.out.println("Список всех подзадач:");
        System.out.println(manager.getListSubtasks());
        System.out.println();

        manager.deleteTaskById(2);
        manager.deleteEpicById(4);
        System.out.println("Задача, эпик и его подзадачи удалены.\n");

        System.out.println("Список всех отдельных задач:");
        System.out.println(manager.getListTasks());
        System.out.println("Список всех эпиков:");
        System.out.println(manager.getListEpics());
        System.out.println("Список всех подзадач:");
        System.out.println(manager.getListSubtasks());
    }
}
