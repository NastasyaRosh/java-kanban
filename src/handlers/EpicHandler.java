package handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.HTTPTaskManager;
import manager.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class EpicHandler implements HttpHandler {

    private static final Gson gson = new Gson();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
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
                if (path.endsWith("/tasks/epic")) {
                    exchange.sendResponseHeaders(200, 0);
                    response = gson.toJson(taskManager.getListEpics());
                } else if (path.contains("/tasks/epic?id=")) {
                    exchange.sendResponseHeaders(200, 0);
                    int id = Integer.parseInt(path.substring(path.indexOf("?id=") + 4));
                    response = gson.toJson(taskManager.getEpicById(id));
                }
                break;
            case "POST":
                if (path.equals("/tasks/epic")) {
                    try (InputStream inputStream = exchange.getRequestBody()) {
                        Epic epic = null;
                        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                        JsonElement jsonElement = JsonParser.parseString(body);
                        if (!jsonElement.isJsonObject()) {
                            System.out.println("Тело запроса не соответствует ожидаемому.");
                            return;
                        }
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        epic = taskManager.epicFromJSON(jsonObject);

                        int i = -1;
                        for (Epic listEpic : taskManager.getListEpics()) {
                            if (listEpic.getId() == epic.getId()) {
                                i = 1;
                                break;
                            }
                        }

                        if (i > 0) {
                            exchange.sendResponseHeaders(201, 0);
                            taskManager.updateEpic(epic);
                            response = "Эпик обновлен.";
                        } else {
                            exchange.sendResponseHeaders(201, 0);
                            ((HTTPTaskManager) taskManager).makeEpicWithId(epic);
                            response = "Эпик создан.";
                        }
                    }
                }
                break;
            case "DELETE":
                if (path.endsWith("/tasks/epic")) {
                    exchange.sendResponseHeaders(200, 0);
                    taskManager.deleteAllEpicsAndSubTasks();
                    response = "Все эпики и подзадачи удалены.";
                } else if (path.contains("/tasks/epic?id=")) {
                    exchange.sendResponseHeaders(200, 0);
                    int id = Integer.parseInt(path.substring(path.indexOf("?id=") + 4));
                    taskManager.deleteEpicById(id);
                    response = "Эпик с ID = " + id + " удален.";
                }
                break;
        }

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(DEFAULT_CHARSET));
        }
    }
}
