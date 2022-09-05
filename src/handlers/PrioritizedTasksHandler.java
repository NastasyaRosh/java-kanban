package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class PrioritizedTasksHandler implements HttpHandler {

    private static final Gson gson = new Gson();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;

    public PrioritizedTasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

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
