import Server.HttpTaskServer;
import Server.KVServer;
import Server.KVTaskClient;
import com.google.gson.Gson;
import manager.*;
import tasks.*;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException {

        new KVServer().start();
        TaskManager httpManager = Managers.getDefault();

        Task task1 = new Task("Помыть пол", "Используй Mister Proper", Statuses.NEW, 15
                , LocalDateTime.of(2022, 7, 20, 12, 15));
        Task task2 = new Task("Сходить в магазин", "Купить капусту", Statuses.IN_PROGRESS);
        Epic epic1 = new Epic("Счета", "На оплату");
        SubTask subTask1 = new SubTask("Счет за воду", "2222 рубля", Statuses.NEW, 3, 30
                , LocalDateTime.of(2022, 4, 25, 10, 0));
        SubTask subTask2 = new SubTask("Счет за газ", "557 рублей", Statuses.IN_PROGRESS, 3, 120
                , LocalDateTime.of(2022, 10, 5, 10, 30));

        httpManager.makeTask(task1);
        httpManager.makeTask(task2);
        httpManager.makeEpic(epic1);
        httpManager.makeSubtask(subTask1);
        httpManager.makeSubtask(subTask2);

        httpManager.getTaskById(1);
        httpManager.getEpicById(3);

        System.out.println(httpManager.getPrioritizedTasks());

        HttpTaskServer server = new HttpTaskServer();

    }
}
