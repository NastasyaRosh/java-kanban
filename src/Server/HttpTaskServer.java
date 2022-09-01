package Server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.*;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {

    private final HttpServer httpServer;
    private static TaskManager taskManager;
    private final int port = 8080;
    private static final Gson gson = new Gson();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public HttpTaskServer() throws IOException {
        this.taskManager = HTTPTaskManager.load();
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(port), 0);

        httpServer.createContext("/tasks/", new PrioritizedTasksHandler());
        httpServer.createContext("/tasks/task", new TaskHandler());
        httpServer.createContext("/tasks/task?id={id}", new TaskHandler());
        httpServer.createContext("/tasks/subtask", new SubtaskHandler());
        httpServer.createContext("/tasks/subtask?id={id}", new SubtaskHandler());
        httpServer.createContext("/tasks/epic", new EpicHandler());
        httpServer.createContext("/tasks/epic?id={id}", new EpicHandler());
        httpServer.createContext("/tasks/subtask/epic?id={id}", new SubtaskHandler());
        httpServer.createContext("/tasks/history", new HistoryHandler());

        httpServer.start();
        System.out.println("Сервер запущен.");
    }

    static class PrioritizedTasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String response = null;
            String path = String.valueOf(exchange.getRequestURI());

            System.out.println("Обрабатывается запрос " + path + " с методом " + method);

            if (path.equals("/tasks/") && method.equals("GET")) {
                exchange.sendResponseHeaders(200, 0);
                response = gson.toJson(taskManager.getPrioritizedTasks());
            }

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(DEFAULT_CHARSET));
            }
        }
    }

    static class TaskHandler implements HttpHandler {

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

    static class SubtaskHandler implements HttpHandler {

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

    static class EpicHandler implements HttpHandler {

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

    static class HistoryHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String response = null;
            String path = String.valueOf(exchange.getRequestURI());

            System.out.println("Обрабатывается запрос " + path + " с методом " + method);

            if (path.equals("/tasks/history") && method.equals("GET")) {
                exchange.sendResponseHeaders(200, 0);
                response = gson.toJson(taskManager.getHistoryManager());
            }

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(DEFAULT_CHARSET));
            }
        }
    }

}
