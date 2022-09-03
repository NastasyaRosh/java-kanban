package tests;

import Server.HttpTaskServer;
import Server.KVServer;
import com.google.gson.Gson;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Statuses;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    private KVServer kvServer;
    private HttpTaskServer server;
    private TaskManager httpManager;
    private HttpClient client;
    private String answerForNewTask;
    private String answerForNewEpic;
    private String answerForNewSubtask;
    Gson gson = new Gson();

    @BeforeEach
    public void beforeEach() {
        try {
            kvServer = new KVServer();
            kvServer.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить KV - сервер. Стек ошибки:");
            e.printStackTrace();
        }
        httpManager = Managers.getDefault();
        server = new HttpTaskServer("http://localhost:8078");
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void stopServers() {
        kvServer.stop();
        server.stop(0);
    }

    private void createTask() {
        Task task = new Task("Помыть пол", "Используй Mister Proper", Statuses.NEW, 15
                , LocalDateTime.of(2022, 7, 20, 12, 15));
        HttpRequest requestPost = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/task")).
                POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task))).build();
        try {
            HttpResponse<String> response = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
            answerForNewTask = response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createEpicAndSubtask() {
        Epic epic = new Epic("Счета", "На оплату", 1);
        SubTask subTask = new SubTask("Счет за воду", "2222 рубля", Statuses.NEW, 1, 30
                , LocalDateTime.of(2022, 4, 25, 10, 0));
        HttpRequest requestPostEpic = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/epic")).
                POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();
        HttpRequest requestPostSubtask = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/subtask")).
                POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask))).build();
        try {
            HttpResponse<String> responsePostEpic = client.send(requestPostEpic, HttpResponse.BodyHandlers.ofString());
            answerForNewEpic = responsePostEpic.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        HttpResponse<String> responsePostSubtask = null;
        try {
            responsePostSubtask = client.send(requestPostSubtask, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        answerForNewSubtask = responsePostSubtask.body();
    }

    @Test
    public void taskTest() throws IOException, InterruptedException {
        createTask();
        assertEquals("Задача создана.", answerForNewTask, "Ошибка при создании базовой задачи.");

        //Проверка обновления задачи
        Task updateBaseTask = new Task("Сходить в магазин", "Купить капусту", Statuses.IN_PROGRESS, 1);
        HttpRequest requestPostUpdate = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/task")).
                POST(HttpRequest.BodyPublishers.ofString(gson.toJson(updateBaseTask))).build();
        HttpResponse<String> responsePostUpdate = client.send(requestPostUpdate, HttpResponse.BodyHandlers.ofString());
        HttpRequest requestGetUpdate = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/task?id=1")).
                GET().build();
        HttpResponse<String> responseGetUpdate = client.send(requestGetUpdate, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responsePostUpdate.statusCode());
        assertEquals("Задача обновлена.", responsePostUpdate.body(), "Неверное тело ответа при обновлении задачи.");
        assertEquals(200, responseGetUpdate.statusCode(), "Получение задачи по ID не работает.");
        assertEquals(gson.toJson(updateBaseTask), responseGetUpdate.body(), "Задача обновляется неверно.");

        //Проверка создания новой задачи
        Task newTask = new Task("Сходить в магазин", "Купить капусту", Statuses.IN_PROGRESS, 2);
        HttpRequest requestPostNew = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/task")).
                POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newTask))).build();
        HttpResponse<String> responsePostNew = client.send(requestPostNew, HttpResponse.BodyHandlers.ofString());
        HttpRequest requestGetNew = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/task?id=2")).
                GET().build();
        HttpResponse<String> responseGetNew = client.send(requestGetNew, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responsePostNew.statusCode());
        assertEquals("Задача создана.", responsePostNew.body(), "Неверное тело ответа при обновлении задачи.");
        assertEquals(200, responseGetNew.statusCode(), "Получение задачи по ID не работает.");
        assertEquals(gson.toJson(newTask), responseGetNew.body(), "Задача создается неверно.");

        //Проверка получения списка задач
        HttpRequest requestGetAll = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/task")).
                GET().build();
        HttpResponse<String> responseGetAll = client.send(requestGetAll, HttpResponse.BodyHandlers.ofString());

        List<Task> getTask = new ArrayList<>(Arrays.asList(updateBaseTask, newTask));
        assertEquals(200, responseGetAll.statusCode());
        assertEquals(gson.toJson(getTask), responseGetAll.body(), "Список задач не приходит.");

        //Проверка удаления одной задачи
        HttpRequest requestDeleteOne = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/task?id=2")).
                DELETE().build();
        HttpResponse<String> responseDeleteOne = client.send(requestDeleteOne, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseGetAllAfterDelete = client.send(requestGetAll, HttpResponse.BodyHandlers.ofString());

        getTask.remove(1);
        assertEquals(200, responseDeleteOne.statusCode());
        assertEquals(gson.toJson(getTask), responseGetAllAfterDelete.body(), "Задача не удаляется по ID.");

        //Проверка удаления всех задач
        HttpRequest requestDeleteAll = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/task")).
                DELETE().build();
        HttpResponse<String> responseDeleteAll = client.send(requestDeleteAll, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseGetAllAfterDeleteAll = client.send(requestGetAll, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseDeleteAll.statusCode());
        assertEquals("Все задачи удалены.", responseDeleteAll.body());
        assertEquals("[]", responseGetAllAfterDeleteAll.body(), "Не работает удаление сразу всех задач.");
    }

    @Test
    public void subtaskTest() throws IOException, InterruptedException {
        createEpicAndSubtask();
        assertEquals("Эпик создан.", answerForNewEpic, "Ошибка при создании базового эпика.");
        assertEquals("Подзадача создана.", answerForNewSubtask, "Ошибка при создании базовой подзадачи.");

        //Проверка обновления подзадачи
        SubTask updateBaseTask = new SubTask("Сходить в магазин", "Купить капусту", Statuses.IN_PROGRESS, 2, 1);
        HttpRequest requestPostUpdate = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/subtask")).
                POST(HttpRequest.BodyPublishers.ofString(gson.toJson(updateBaseTask))).build();
        HttpResponse<String> responsePostUpdate = client.send(requestPostUpdate, HttpResponse.BodyHandlers.ofString());
        HttpRequest requestGetUpdate = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/subtask?id=2")).
                GET().build();
        HttpResponse<String> responseGetUpdate = client.send(requestGetUpdate, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responsePostUpdate.statusCode());
        assertEquals("Подзадача обновлена.", responsePostUpdate.body(), "Неверное тело ответа при обновлении подзадачи.");
        assertEquals(200, responseGetUpdate.statusCode(), "Получение подзадачи по ID не работает.");
        assertEquals(gson.toJson(updateBaseTask), responseGetUpdate.body(), "Подзадача обновляется неверно.");

        //Проверка создания новой подзадачи
        SubTask newTask = new SubTask("Выгулять собаку", "На поводке", Statuses.IN_PROGRESS, 3, 1);
        HttpRequest requestPostNew = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/subtask")).
                POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newTask))).build();
        HttpResponse<String> responsePostNew = client.send(requestPostNew, HttpResponse.BodyHandlers.ofString());
        HttpRequest requestGetNew = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/subtask?id=3")).
                GET().build();
        HttpResponse<String> responseGetNew = client.send(requestGetNew, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responsePostNew.statusCode());
        assertEquals("Подзадача создана.", responsePostNew.body(), "Неверное тело ответа при обновлении подзадачи.");
        assertEquals(200, responseGetNew.statusCode(), "Получение подзадачи по ID не работает.");
        assertEquals(gson.toJson(newTask), responseGetNew.body(), "Подзадача создается неверно.");

        //Проверка получения списка подзадач
        HttpRequest requestGetAll = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/subtask")).
                GET().build();
        HttpResponse<String> responseGetAll = client.send(requestGetAll, HttpResponse.BodyHandlers.ofString());

        List<SubTask> getTask = new ArrayList<>(Arrays.asList(updateBaseTask, newTask));
        assertEquals(200, responseGetAll.statusCode());
        assertEquals(gson.toJson(getTask), responseGetAll.body(), "Список подзадач не приходит.");

        //Проверка получения списка подзадач по номеру эпика
        HttpRequest requestGetSubtasksByEpic = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/subtask/epic?id=1")).
                GET().build();
        HttpResponse<String> responseGetSubtasksByEpic = client.send(requestGetSubtasksByEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGetSubtasksByEpic.statusCode());
        assertEquals(gson.toJson(getTask), responseGetSubtasksByEpic.body(), "Список подзадач по номеру эпика не приходит.");

        //Проверка удаления одной подзадачи
        HttpRequest requestDeleteOne = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/subtask?id=3")).
                DELETE().build();
        HttpResponse<String> responseDeleteOne = client.send(requestDeleteOne, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseGetAllAfterDelete = client.send(requestGetAll, HttpResponse.BodyHandlers.ofString());

        getTask.remove(1);
        assertEquals(200, responseDeleteOne.statusCode());
        assertEquals(gson.toJson(getTask), responseGetAllAfterDelete.body(), "Подзадача не удаляется по ID.");

        //Проверка удаления всех подзадач
        HttpRequest requestDeleteAll = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/subtask")).
                DELETE().build();
        HttpResponse<String> responseDeleteAll = client.send(requestDeleteAll, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseGetAllAfterDeleteAll = client.send(requestGetAll, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseDeleteAll.statusCode());
        assertEquals("Все подзадачи удалены.", responseDeleteAll.body());
        assertEquals("[]", responseGetAllAfterDeleteAll.body(), "Не работает удаление сразу всех подзадач.");
    }

    @Test
    public void epicTest() throws IOException, InterruptedException {
        createEpicAndSubtask();
        assertEquals("Эпик создан.", answerForNewEpic, "Ошибка при создании базового эпика.");
        assertEquals("Подзадача создана.", answerForNewSubtask, "Ошибка при создании базовой подзадачи.");

        //Проверка обновления эпика
        Epic updateBaseTask = new Epic("Новый", "эпик", 1);
        HttpRequest requestPostUpdate = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/epic")).
                POST(HttpRequest.BodyPublishers.ofString(gson.toJson(updateBaseTask))).build();
        HttpResponse<String> responsePostUpdate = client.send(requestPostUpdate, HttpResponse.BodyHandlers.ofString());
        HttpRequest requestGetUpdate = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/epic?id=1")).
                GET().build();
        HttpResponse<String> responseGetUpdate = client.send(requestGetUpdate, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responsePostUpdate.statusCode());
        assertEquals("Эпик обновлен.", responsePostUpdate.body(), "Неверное тело ответа при обновлении эпика.");
        assertEquals(200, responseGetUpdate.statusCode(), "Получение эпика по ID не работает.");
        String jsonUpdateEpic = "{\"idSubtasks\":[2],\"endTime\":{\"date\":{\"year\":2022,\"month\":4,\"" +
                "day\":25},\"time\":{\"hour\":10,\"minute\":30,\"second\":0,\"nano\":0}},\"name\":\"Новый\",\"description\":\"эпик\"," +
                "\"id\":1,\"status\":\"NEW\",\"duration\":30,\"startTime\":{\"date\":{\"year\":2022,\"month\":4,\"day\":25}," +
                "\"time\":{\"hour\":10,\"minute\":0,\"second\":0,\"nano\":0}}}";
        assertEquals(jsonUpdateEpic, responseGetUpdate.body(), "Эпик обновляется неверно.");

        //Проверка создания нового эпика
        Epic newTask = new Epic("Новый", "эпик", 3);
        HttpRequest requestPostNew = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/epic")).
                POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newTask))).build();
        HttpResponse<String> responsePostNew = client.send(requestPostNew, HttpResponse.BodyHandlers.ofString());
        HttpRequest requestGetNew = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/epic?id=3")).
                GET().build();
        HttpResponse<String> responseGetNew = client.send(requestGetNew, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responsePostNew.statusCode());
        assertEquals("Эпик создан.", responsePostNew.body(), "Неверное тело ответа при обновлении эпика.");
        assertEquals(200, responseGetNew.statusCode(), "Получение эпика по ID не работает.");
        String jsonNewEpic = "{\"idSubtasks\":[],\"name\":\"Новый\",\"description\":\"эпик\",\"id\":3}";
        assertEquals(jsonNewEpic, responseGetNew.body(), "Эпик создается неверно.");

        //Проверка получения списка эпиков
        HttpRequest requestGetAll = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/epic")).
                GET().build();
        HttpResponse<String> responseGetAll = client.send(requestGetAll, HttpResponse.BodyHandlers.ofString());

        List<Epic> getTask = new ArrayList<>(Arrays.asList(updateBaseTask, newTask));
        assertEquals(200, responseGetAll.statusCode());
        String jsonListEpic = "[{\"idSubtasks\":[2],\"endTime\":{\"date\":{\"year\":2022,\"month\":4,\"day\":25},\"time\":{\"hour\":10,\"minute\":30,\"second\":0,\"nano\":0}}," +
                "\"name\":\"Новый\",\"description\":\"эпик\",\"id\":1,\"status\":\"NEW\",\"duration\":30," +
                "\"startTime\":{\"date\":{\"year\":2022,\"month\":4,\"day\":25},\"time\":{\"hour\":10,\"minute\":0,\"second\":0,\"nano\":0}}}," +
                "{\"idSubtasks\":[],\"name\":\"Новый\",\"description\":\"эпик\",\"id\":3}]";
        assertEquals(jsonListEpic, responseGetAll.body(), "Список эпиков не приходит.");

        //Проверка удаления однго эпика
        HttpRequest requestDeleteOne = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/epic?id=3")).
                DELETE().build();
        HttpResponse<String> responseDeleteOne = client.send(requestDeleteOne, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseGetAllAfterDelete = client.send(requestGetAll, HttpResponse.BodyHandlers.ofString());

        getTask.remove(1);
        assertEquals(200, responseDeleteOne.statusCode());
        assertEquals("[" + jsonUpdateEpic + "]", responseGetAllAfterDelete.body(), "Эпик не удаляется по ID.");

        //Проверка удаления всех эпиков
        HttpRequest requestDeleteAll = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/epic")).
                DELETE().build();
        HttpResponse<String> responseDeleteAll = client.send(requestDeleteAll, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseGetAllAfterDeleteAll = client.send(requestGetAll, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseDeleteAll.statusCode());
        assertEquals("Все эпики и подзадачи удалены.", responseDeleteAll.body());
        assertEquals("[]", responseGetAllAfterDeleteAll.body(), "Не работает удаление сразу всех эпиков.");
    }

    @Test
    public void tasksTest() throws IOException, InterruptedException {
        Task task1 = new Task("Для", "списка", Statuses.NEW, 1, 15
                , LocalDateTime.of(2022, 7, 20, 12, 15));
        Task task2 = new Task("Для", "списка", Statuses.NEW, 2, 15
                , LocalDateTime.of(2023, 7, 20, 12, 15));
        HttpRequest requestPost1 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/task")).
                POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1))).build();
        HttpRequest requestPost2 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/task")).
                POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2))).build();
        client.send(requestPost1, HttpResponse.BodyHandlers.ofString());
        client.send(requestPost2, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = new ArrayList<>(Arrays.asList(task1, task2));

        HttpRequest requestTasks = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/")).
                GET().build();
        HttpResponse<String> responseTasks = client.send(requestTasks, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseTasks.statusCode());
        assertEquals(gson.toJson(tasks), responseTasks.body(), "Список приоритетных задач неверен.");
    }
}
