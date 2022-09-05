package tests;

import manager.HTTPTaskManager;
import org.junit.jupiter.api.BeforeEach;
import server.HttpTaskServer;
import server.KVServer;
import com.google.gson.Gson;
import manager.Managers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tasks.Statuses;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager> {

    Gson gson = new Gson();
    private static KVServer kvServer;
    private static HttpClient client;

    @BeforeAll
    public static void beforeAll() {
        try {
            kvServer = new KVServer();
            kvServer.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить KV - сервер. Стек ошибки:");
            e.printStackTrace();
        }
        client = HttpClient.newHttpClient();
    }

    @BeforeEach
    public void beforeEach() {
        taskManager = (HTTPTaskManager) Managers.getDefault();
    }

    @Test
    public void saveAndLoad() throws IOException, InterruptedException {
        Task task = new Task("Сходить в магазин", "Купить капусту", Statuses.IN_PROGRESS, 1);
        taskManager.makeTask(task);
        HttpTaskServer server = new HttpTaskServer();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/")).
                GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка выполнения запроса.");
        assertEquals("[" + gson.toJson(task) + "]", response.body(), "Задачи не восстановлены.");
    }
}