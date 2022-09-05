package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.HTTPTaskManager;
import manager.TaskManager;
import tasks.SubTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler implements HttpHandler {

    private static final Gson gson = new Gson();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
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
                if (path.endsWith("/tasks/subtask")) {
                    exchange.sendResponseHeaders(200, 0);
                    response = gson.toJson(taskManager.getListSubtasks());
                } else if (path.contains("/tasks/subtask?id=")) {
                    exchange.sendResponseHeaders(200, 0);
                    int id = Integer.parseInt(path.substring(path.indexOf("?id=") + 4));
                    response = gson.toJson(taskManager.getSubtaskById(id));
                } else if (path.contains("/tasks/subtask/epic?id=")) {
                    exchange.sendResponseHeaders(200, 0);
                    int id = Integer.parseInt(path.substring(path.indexOf("?id=") + 4));
                    response = gson.toJson(taskManager.getListSubtasksByEpic(id));
                }
                break;
            case "POST":
                if (path.equals("/tasks/subtask")) {
                    try (InputStream inputStream = exchange.getRequestBody()) {
                        SubTask subTask = null;
                        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                        JsonElement jsonElement = JsonParser.parseString(body);
                        if (!jsonElement.isJsonObject()) {
                            System.out.println("Тело запроса не соответствует ожидаемому.");
                            return;
                        }
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        subTask = (SubTask) taskManager.taskFromJSON(jsonObject);

                        int i = -1;
                        for (SubTask listSubtask : taskManager.getListSubtasks()) {
                            if (listSubtask.getId() == subTask.getId()) {
                                i = 1;
                                break;
                            }
                        }

                        if (i > 0) {
                            exchange.sendResponseHeaders(201, 0);
                            taskManager.updateSubtask(subTask);
                            response = "Подзадача обновлена.";
                        } else {
                            exchange.sendResponseHeaders(201, 0);
                            ((HTTPTaskManager) taskManager).makeSubtaskWithId(subTask);
                            response = "Подзадача создана.";
                        }
                    }
                }
                break;
            case "DELETE":
                if (path.endsWith("/tasks/subtask")) {
                    exchange.sendResponseHeaders(200, 0);
                    taskManager.deleteAllSubTask();
                    response = "Все подзадачи удалены.";
                } else if (path.contains("/tasks/subtask?id=")) {
                    exchange.sendResponseHeaders(200, 0);
                    int id = Integer.parseInt(path.substring(path.indexOf("?id=") + 4));
                    taskManager.deleteSubtaskById(id);
                    response = "Подзадача с ID = " + id + " удалена.";
                }
                break;
        }

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(DEFAULT_CHARSET));
        }
    }
}

