package tests;

import manager.FileBackedTasksManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Statuses;
import tasks.SubTask;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    String fileName = "taskTest.csv";

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTasksManager(fileName);
    }

    @Test
    public void saveAndLoad() {
        taskManager.deleteAllTasks();
        FileBackedTasksManager loadTaskManager = taskManager.loadFromFile(fileName);
        assertArrayEquals(new ArrayList<>().toArray(), loadTaskManager.getListTasks().toArray(), "Файл не пустой.");
        assertArrayEquals(new ArrayList<>().toArray(), loadTaskManager.getListSubtasks().toArray(), "Файл не пустой.");
        assertArrayEquals(new ArrayList<>().toArray(), loadTaskManager.getListEpics().toArray(), "Файл не пустой.");

        Epic epic = new Epic("Книги", "На прочтение");
        SubTask subTask = new SubTask("Карл Маркс \"Капитал\"", "Экономика", Statuses.IN_PROGRESS, 1);
        taskManager.makeEpic(epic);
        taskManager.makeSubtask(subTask);
        loadTaskManager = taskManager.loadFromFile(fileName);
        assertArrayEquals(taskManager.getListEpics().toArray(), loadTaskManager.getListEpics().toArray(), "Список эпиков записан неверно.");
        assertArrayEquals(taskManager.getListSubtasks().toArray(), loadTaskManager.getListSubtasks().toArray(), "Список подзадач записан неверно.");
        assertArrayEquals(new ArrayList<>().toArray(), loadTaskManager.getHistoryManager().toArray(), "История просмотров не пустая.");

        taskManager.getEpicById(1);
        loadTaskManager = taskManager.loadFromFile(fileName);
        assertArrayEquals(taskManager.getHistoryManager().toArray(), loadTaskManager.getHistoryManager().toArray(), "История просмотров записывается неверно.");
    }
}
