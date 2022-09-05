package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.HTTPTaskManager;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TaskHandler implements HttpHandler {

    private static final Gson gson = new Gson();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = String.valueOf(exchange.getRequestURI());
        String response = null;

        System.out.println("Обрабатывается запрос " + path + " с методом " + method);

        switch (method) {
            case "GET":
                if (path.endsWith("/tasks/task")) {
                    exchange.sendResponseHeaders(200, 0);
                    response = gson.toJson(taskManager.getListTasks());
                } else if (path.contains("/tasks/task?id=")) {
                    exchange.sendResponseHeaders(200, 0);
                    int id = Integer.parseInt(path.substring(path.indexOf("?id=") + 4));
                    response = gson.toJson(taskManager.getTaskById(id));
                }
                break;
            case "POST":
                if (path.equals("/tasks/task")) {
                    try (InputStream inputStream = exchange.getRequestBody()) {
                        Task task = null;
                        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                        JsonElement jsonElement = JsonParser.parseString(body);
                        if (!jsonElement.isJsonObject()) {
                            System.out.println("Тело запроса не соответствует ожидаемому.");
                            return;
                        }
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        task = taskManager.taskFromJSON(jsonObject);

                        int i = -1;
                        for (Task listTask : taskManager.getPrioritizedTasks()) {
                            if (listTask.getId() == task.getId()) {
                                i = 1;
                                break;
                            }
                        }

                        if (i > 0) {
                            exchange.sendResponseHeaders(201, 0);
                            taskManager.updateTask(task);
                            response = "Задача обновлена.";
                        } else {
                            exchange.sendResponseHeaders(201, 0);
                            ((HTTPTaskManager) taskManager).makeTaskWithId(task);
                            response = "Задача создана.";
                        }
                    }
                }
                break;
            case "DELETE":
                if (path.endsWith("/tasks/task")) {
                    exchange.sendResponseHeaders(200, 0);
                    taskManager.deleteAllTasks();
                    response = "Все задачи удалены.";
                } else if (path.contains("/tasks/task?id=")) {
                    exchange.sendResponseHeaders(200, 0);
                    int id = Integer.parseInt(path.substring(path.indexOf("?id=") + 4));
                    taskManager.deleteTaskById(id);
                    response = "Задача с ID = " + id + " удалена.";
                }
                break;
        }

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(DEFAULT_CHARSET));
        }
    }
}
