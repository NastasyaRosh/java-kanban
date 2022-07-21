package tests;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Statuses;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {

    HistoryManager historyManager;
    Task task;
    Task task2;
    Task task3;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        task = new Task("Помыть пол", "Используй Mister Proper", Statuses.NEW, 1);
        task2 = new Task("Сходить в магазин", "Купить капусту", Statuses.IN_PROGRESS, 2);
        task3 = new Task("Сдать спринт", "Дописать тесты", Statuses.IN_PROGRESS, 3);
    }


    @Test
    public void add() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "История пустая.");
    }

    @Test
    public void remove() {
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task);
        assertEquals(3, historyManager.getHistory().size(), "Задачи дублируются.");

        historyManager.remove(1);
        List<Task> array = new ArrayList<>();
        array.add(task2);
        array.add(task3);
        assertArrayEquals(array.toArray(), historyManager.getHistory().toArray(), "Ошибка при удалении из начала.");

        historyManager.add(task);
        historyManager.remove(2);
        array.clear();
        array.add(task3);
        array.add(task);
        assertArrayEquals(array.toArray(), historyManager.getHistory().toArray(), "Ошибка при удалении из середины.");

        historyManager.add(task2);
        historyManager.remove(3);
        array.clear();
        array.add(task);
        array.add(task2);
        assertArrayEquals(array.toArray(), historyManager.getHistory().toArray(), "Ошибка при удалении конечного элемента.");
    }

    @Test
    public void getHistoryTest() {

        assertArrayEquals(new ArrayList<>().toArray(), historyManager.getHistory().toArray(), "Пустая история не возвращается.");

        historyManager.add(task);
        List<Task> list = new ArrayList<>();
        list.add(task);
        assertArrayEquals(list.toArray(), historyManager.getHistory().toArray(), "Заполненная история не возвращается.");
    }
}
