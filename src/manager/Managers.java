package manager;

import java.net.URI;

public final class Managers {

    public static TaskManager getDefault() {
        URI url = URI.create("http://localhost:8078");
        return new HTTPTaskManager(url);
    }

/*    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }*/

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getDefaultFile(String fileName) {
        return new FileBackedTasksManager(fileName);
    }
}
