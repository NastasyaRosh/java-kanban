import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        Scanner scanner = new Scanner(System.in);
        Task task1 = new Task("Помыть пол", "Используй Mister Proper", 1, "NEW");
        Task task2 = new Task("Сходить в магазин", "Купить капусту", 2, "IN PROGRESS");
        Task task3 = new Task("Сходить в магазин", "Купить 3 качана капусты", 2, "DONE");
        Epic epic1 = new Epic("Книги", "На прочтение", 1);
        Epic epic2 = new Epic("Счета", "На оплату", 2);
        SubTask subTask1 = new SubTask("Карл Маркс \"Капитал\"", "Экономика", 1, "NEW", 1);
        SubTask subTask2 = new SubTask("Карлос Кастанеда \"Путешествие в Икстлан\"", "Философия", 2, "IN_PROGRESS", 1);
        SubTask subTask3 = new SubTask("Джордж Оруэлл \"1984\"", "Художка", 2, "NEW", 1);
        SubTask subTask4 = new SubTask("Счет за газ", "557 рублей", 3, "DONE", 2);

        while (true) {
            System.out.println("\nМеню: \n" + "1. Получить список задач \n" + "2. Удалить все задачи \n" +
                    "3. Получить задачу по ID \n" + "4. Создать задачу \n" + "5. Обновить задачу \n" +
                    "6. Удалить задачу по ID \n" + "7. Получить список всех подзадач эпика \n" + "0. Выход \n");
            System.out.println("Введите номер команды:");
            int command = scanner.nextInt();
            switch (command) {
                case 0:
                    System.out.println("Выход.");
                    System.exit(0);
                case 1:
                    System.out.println("Список всех отдельных задач:");
                    System.out.println(manager.getListTasks());
                    System.out.println("Список всех эпиков':");
                    System.out.println(manager.getListEpics());
                    System.out.println("Список всех подзадач:");
                    System.out.println(manager.getListSubtasks());
                    break;
                case 2:
                    manager.deleteAllTasks();
                    System.out.println("Все задачи удалены.");
                    //Закомментированный ниже код создан для случая удаления всех подзадач без удаления эпиков
                    /*manager.deleteAllSubTask();
                    manager.setStatusForEpics();
                    System.out.println("Все подзадачи удалены.");*/
                    manager.deleteAllEpicsAndSubTasks();
                    System.out.println("Все эпики и подзадачи удалены.");
                    break;
                case 3:
                    System.out.println("Введите ID задачи: ");
                    int myIdGetTask = scanner.nextInt();
                    System.out.println(manager.getTaskById(myIdGetTask));
                    System.out.println("Введите ID эпика': ");
                    int myIdGetEpic = scanner.nextInt();
                    System.out.println(manager.getEpicById(myIdGetEpic));
                    System.out.println("Введите ID подзадачи: ");
                    int myIdGetSubtask = scanner.nextInt();
                    System.out.println(manager.getSubtaskById(myIdGetSubtask));
                    break;
                case 4:
                    manager.makeTask(task1);
                    manager.makeTask(task2);
                    manager.makeTask(epic1);
                    manager.makeTask(epic2);
                    manager.makeTask(subTask1);
                    manager.makeTask(subTask2);
                    manager.makeTask(subTask4);
                    manager.setStatusForEpics();
                    System.out.println("Задачи, эпики и подзадачи записаны.");
                    break;
                case 5:
                    manager.updateTask(task3);
                    manager.updateTask(subTask3);
                    manager.setStatusForEpics();
                    System.out.println("Задача и подзадача обновлены.");
                    break;
                case 6:
                    System.out.println("Введите ID для удаления задачи: ");
                    int myIdDeleteTask = scanner.nextInt();
                    manager.deleteTaskById(myIdDeleteTask);
                    System.out.println("Задача удалена.");
                    System.out.println("Введите ID для удаления подзадачи: ");
                    int myIdDeleteSubtask = scanner.nextInt();
                    manager.deleteSubtaskById(myIdDeleteSubtask);
                    manager.setStatusForEpics();
                    System.out.println("Задача удалена.");
                    System.out.println("Введите ID для удаления эпика: ");
                    int myIdDeleteEpic = scanner.nextInt();
                    manager.deleteEpicById(myIdDeleteEpic);
                    System.out.println("Эпик и его подадачи удалены.");
                    break;
                case 7:
                    System.out.println("Введите ID эпика': ");
                    int idEpic = scanner.nextInt();
                    System.out.println(manager.getListSubtasksByEpic(idEpic));
                    break;
                default:
                    System.out.println("Такой команды нет.");
                    break;
            }
        }
    }
}
