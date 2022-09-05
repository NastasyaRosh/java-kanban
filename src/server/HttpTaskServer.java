package server;

import com.sun.net.httpserver.HttpServer;
import server.handlers.*;
import manager.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;

public class HttpTaskServer {

    private HttpServer httpServer;
    private static TaskManager taskManager;
    private final int port = 8080;

    public HttpTaskServer(String url) {
        this.taskManager = new HTTPTaskManager(URI.create(url));
        start();
    }

    public HttpTaskServer() {
        this.taskManager = HTTPTaskManager.load();
        start();
    }

    private void start() {
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        httpServer.createContext("/tasks/", new PrioritizedTasksHandler(taskManager));
        httpServer.createContext("/tasks/task", new TaskHandler(taskManager));
        httpServer.createContext("/tasks/task?id={id}", new TaskHandler(taskManager));
        httpServer.createContext("/tasks/subtask", new SubtaskHandler(taskManager));
        httpServer.createContext("/tasks/subtask?id={id}", new SubtaskHandler(taskManager));
        httpServer.createContext("/tasks/epic", new EpicHandler(taskManager));
        httpServer.createContext("/tasks/epic?id={id}", new EpicHandler(taskManager));
        httpServer.createContext("/tasks/subtask/epic?id={id}", new SubtaskHandler(taskManager));
        httpServer.createContext("/tasks/history", new HistoryHandler(taskManager));

        httpServer.start();
        System.out.println("Сервер запущен.");
    }

    public void stop(int i) {
        httpServer.stop(i);
    }
}
