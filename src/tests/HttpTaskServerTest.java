package tests;

import Server.HttpTaskServer;
import Server.KVServer;
import com.google.gson.Gson;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Statuses;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    Gson gson = new Gson();

    @BeforeEach
    public static void beforeEach(){
        try {
            new KVServer().start();
            HttpTaskServer server = new HttpTaskServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/task")).DELETE().build();
        HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/epic")).DELETE().build();
    }

    @Test
    public void getTasks() throws IOException, InterruptedException {
        TaskManager httpManager = Managers.getDefault();
        Task task1 = new Task("Помыть пол", "Используй Mister Proper", Statuses.NEW, 15
                , LocalDateTime.of(2022, 7, 20, 12, 15));
        httpManager.makeTask(task1);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestPost = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/task?id=1")).
                POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1))).build();
        HttpRequest requestGet = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/task?id=1")).GET().build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(task1), response.body());
    }
}
