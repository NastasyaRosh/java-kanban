package tests;

import manager.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import tasks.Epic;
import tasks.Statuses;
import tasks.SubTask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;

    @Test
    public void addGetListAndDeleteTask() {
        Task task = new Task("Помыть пол", "Используй Mister Proper", Statuses.NEW);
        taskManager.makeTask(task);
        final int taskId = task.getId();

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        List<Task> tasks = taskManager.getListTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

        taskManager.deleteAllTasks();
        tasks = taskManager.getListTasks();

        assertArrayEquals(new ArrayList<>().toArray(), tasks.toArray(), "Задачи не удаляются");
    }

    @Test
    public void addGetListAndDeleteSubTask() {
        Epic epic = new Epic("Книги", "На прочтение");
        SubTask subTask = new SubTask("Карл Маркс \"Капитал\"", "Экономика", Statuses.NEW, 1);
        taskManager.makeEpic(epic);
        taskManager.makeSubtask(subTask);
        final int subTaskId = subTask.getId();

        final SubTask savedSubTask = taskManager.getSubtaskById(subTaskId);

        assertNotNull(savedSubTask, "Подзадача не найдена.");
        assertEquals(subTask, savedSubTask, "Подзадачи не совпадают.");

        assertEquals(epic, taskManager.getEpicById(subTask.getIdEpic()), "Подзадача создана для несуществующего эпика.");

        List<SubTask> subTasks = taskManager.getListSubtasks();

        assertNotNull(subTasks, "Подзадачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество подзадач.");
        assertEquals(subTask, subTasks.get(0), "Подзадачи не совпадают.");

        taskManager.deleteAllSubTask();
        subTasks = taskManager.getListSubtasks();

        assertArrayEquals(new ArrayList<>().toArray(), subTasks.toArray(), "Подзадачи не удаляются");
    }

    @Test
    public void addGetListAndDeleteEpic() {
        Epic epic = new Epic("Книги", "На прочтение");
        SubTask subTask = new SubTask("Карл Маркс \"Капитал\"", "Экономика", Statuses.NEW, 1);
        SubTask subTask2 = new SubTask("Карлос Кастанеда \"Путешествие в Икстлан\"", "Философия", Statuses.IN_PROGRESS, 1);
        taskManager.makeEpic(epic);
        taskManager.makeSubtask(subTask);
        taskManager.makeSubtask(subTask2);
        final int epicId = epic.getId();

        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        List<Epic> epics = taskManager.getListEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");

        List<SubTask> subTaskByEpicId = taskManager.getListSubtasksByEpic(epicId);
        List<SubTask> subTasks = new ArrayList<>();
        subTasks.add(subTask);
        subTasks.add(subTask2);

        assertNotNull(subTaskByEpicId, "Подзадачи эпика не возврщаются.");
        assertEquals(2, subTaskByEpicId.size(), "Возвращается неверное количество подзадач эпика.");
        assertArrayEquals(subTasks.toArray(), subTaskByEpicId.toArray(), "Возвращаются неверные подзадачи эпика.");

        taskManager.deleteAllEpicsAndSubTasks();
        epics = taskManager.getListEpics();
        subTasks = taskManager.getListSubtasks();

        assertArrayEquals(new ArrayList<>().toArray(), epics.toArray(), "Эпики не удаляются");
        assertArrayEquals(new ArrayList<>().toArray(), subTasks.toArray(), "Подзадачи эпика не удаляются");
    }

    @Test
    public void updateTaskAndDeleteForID() {
        Task task = new Task("Помыть пол", "Используй Mister Proper", Statuses.NEW);
        taskManager.makeTask(task);
        final int taskId = task.getId();
        Task task2 = new Task("Сходить в магазин", "Купить капусту", Statuses.IN_PROGRESS, taskId);

        taskManager.updateTask(task2);
        Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task2, savedTask, "Задача не обновлена.");

        taskManager.deleteTaskById(taskId);

        assertArrayEquals(new ArrayList<>().toArray(), taskManager.getListTasks().toArray(), "Задачи не удаляются по ИД");
    }

    @Test
    public void updateSubTaskAndDeleteForID() {
        Epic epic = new Epic("Книги", "На прочтение");
        SubTask subTask = new SubTask("Карл Маркс \"Капитал\"", "Экономика", Statuses.NEW, 1);
        taskManager.makeEpic(epic);
        taskManager.makeSubtask(subTask);
        final int subTaskId = subTask.getId();
        SubTask subTask2 = new SubTask("Карлос Кастанеда \"Путешествие в Икстлан\"", "Философия", Statuses.IN_PROGRESS, subTaskId, 1);

        taskManager.updateSubtask(subTask2);
        SubTask savedSubTask = taskManager.getSubtaskById(subTaskId);

        assertNotNull(savedSubTask, "Подзадача не найдена.");
        assertEquals(subTask2, savedSubTask, "Подзадача не обновлена.");
        assertEquals("IN_PROGRESS", epic.getStatus().toString(), "Статус эпика не обновлен.");

        taskManager.deleteSubtaskById(subTaskId);

        assertArrayEquals(new ArrayList<>().toArray(), taskManager.getListSubtasks().toArray(), "Подзадачи не удаляются по ИД");
    }

    @Test
    public void updateEpicAndDeleteForID() {
        Epic epic = new Epic("Книги", "На прочтение");
        SubTask subTask = new SubTask("Карл Маркс \"Капитал\"", "Экономика", Statuses.IN_PROGRESS, 1);
        taskManager.makeEpic(epic);
        taskManager.makeSubtask(subTask);
        final int epicId = epic.getId();
        Epic epic2 = new Epic("Счета", "На оплату", epicId);

        taskManager.updateEpic(epic2);
        Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic2, savedEpic, "Эпик не обновлен.");
        assertEquals("IN_PROGRESS", epic.getStatus().toString(), "Статус эпика не верный.");

        taskManager.deleteEpicById(epicId);

        assertArrayEquals(new ArrayList<>().toArray(), taskManager.getListEpics().toArray(), "Эпики не удаляются по ИД");
        assertArrayEquals(new ArrayList<>().toArray(), taskManager.getListSubtasksByEpic(epicId).toArray(), "Подзадачи эпика не удаляются.");
    }

    @Test
    public void startAndEndTimeAndDurationEpic() {
        Epic epic = new Epic("Счета", "На оплату");
        SubTask subTask1 = new SubTask("Счет за воду", "2222 рубля", Statuses.NEW, 1);
        SubTask subTask2 = new SubTask("Счет за газ", "557 рублей", Statuses.IN_PROGRESS, 1
                , 120, LocalDateTime.of(2022, 10, 5, 10, 30));
        SubTask subTask3 = new SubTask("Счет за воду", "2222 рубля", Statuses.NEW, 1
                , 30, LocalDateTime.of(2022, 4, 25, 10, 0));

        taskManager.makeEpic(epic);
        taskManager.makeSubtask(subTask1);
        taskManager.makeSubtask(subTask2);

        LocalDateTime startTime = LocalDateTime.of(2022, 10, 5, 10, 30);
        LocalDateTime endTime = LocalDateTime.of(2022, 10, 5, 12, 30);
        assertEquals(startTime, epic.getStartTime(), "Ложное время начала эпика при одной подзадаче с указанным временем.");
        assertEquals(endTime, epic.getEndTime(), "Ложное время окончания эпика при одной подзадаче с указанным временем.");
        assertEquals(120, epic.getDuration());

        taskManager.makeSubtask(subTask3);
        startTime = LocalDateTime.of(2022, 4, 25, 10, 0);
        endTime = LocalDateTime.of(2022, 10, 5, 12, 30);
        assertEquals(startTime, epic.getStartTime(), "Ложное время начала эпика при двух подзадачах с указанным временем.");
        assertEquals(endTime, epic.getEndTime(), "Ложное время окончания эпика при двух подзадачах с указанным временем.");
        assertEquals(150, epic.getDuration());
    }

    @Test
    public void getPrioritizedTasksTestForMakeAndDeleteById() {
        Epic epic = new Epic("Счета", "На оплату");
        SubTask subTask1 = new SubTask("Счет за воду", "2222 рубля", Statuses.NEW, 1);
        SubTask subTask2 = new SubTask("Счет за газ", "557 рублей", Statuses.IN_PROGRESS, 1
                , 120, LocalDateTime.of(2022, 10, 5, 10, 30));
        SubTask subTask3 = new SubTask("Счет за воду", "2222 рубля", Statuses.NEW, 1
                , 30, LocalDateTime.of(2022, 4, 25, 10, 0));
        Task task = new Task("Сходить в магазин", "Купить капусту", Statuses.IN_PROGRESS);

        taskManager.makeEpic(epic);
        taskManager.makeSubtask(subTask1);
        taskManager.makeSubtask(subTask2);
        taskManager.makeSubtask(subTask3);

        List<Task> list = new ArrayList<>();
        list.add(subTask3);
        list.add(subTask2);
        list.add(subTask1);
        assertArrayEquals(list.toArray(), taskManager.getPrioritizedTasks().toArray());

        taskManager.deleteSubtaskById(2);
        list.remove(2);
        assertArrayEquals(list.toArray(), taskManager.getPrioritizedTasks().toArray());

        taskManager.makeTask(task);
        taskManager.deleteEpicById(1);
        list.clear();
        list.add(task);
        assertArrayEquals(list.toArray(), taskManager.getPrioritizedTasks().toArray());

        taskManager.deleteTaskById(5);
        list.clear();
        assertArrayEquals(list.toArray(), taskManager.getPrioritizedTasks().toArray());

    }

    @Test
    public void getPrioritizedTasksTestForDeleteAll() {
        Epic epic = new Epic("Счета", "На оплату");
        SubTask subTask1 = new SubTask("Счет за воду", "2222 рубля", Statuses.NEW, 1);
        SubTask subTask2 = new SubTask("Счет за газ", "557 рублей", Statuses.IN_PROGRESS, 1
                , 120, LocalDateTime.of(2022, 10, 5, 10, 30));
        SubTask subTask3 = new SubTask("Счет за воду", "2222 рубля", Statuses.NEW, 1
                , 30, LocalDateTime.of(2022, 4, 25, 10, 0));
        Task task = new Task("Сходить в магазин", "Купить капусту", Statuses.IN_PROGRESS);

        taskManager.makeEpic(epic);
        taskManager.makeSubtask(subTask1);
        taskManager.makeSubtask(subTask2);
        taskManager.makeTask(task);
        List<Task> list = new ArrayList<>();
        list.add(subTask2);
        list.add(subTask1);

        taskManager.deleteAllTasks();
        assertArrayEquals(list.toArray(), taskManager.getPrioritizedTasks().toArray());

        taskManager.deleteAllSubTask();
        list.clear();
        assertArrayEquals(list.toArray(), taskManager.getPrioritizedTasks().toArray());

        taskManager.makeSubtask(subTask3);
        taskManager.deleteAllEpicsAndSubTasks();
        assertArrayEquals(list.toArray(), taskManager.getPrioritizedTasks().toArray());
    }

    @Test
    public void getPrioritizedTasksTestForUpdateTask() {
        Task task = new Task("Сходить в магазин", "Купить капусту", Statuses.IN_PROGRESS,
                30, LocalDateTime.of(2022, 4, 25, 10, 0));
        Task task2 = new Task("Обновление", "Новый вариант без времени", Statuses.DONE, 1);

        taskManager.makeTask(task);
        taskManager.updateTask(task2);
        List<Task> list = new ArrayList<>();
        list.add(task2);
        assertArrayEquals(list.toArray(), taskManager.getPrioritizedTasks().toArray());
    }

    @Test
    public void getPrioritizedTasksTestForUpdateSubtask() {
        Epic epic = new Epic("Счета", "На оплату");
        SubTask subTask1 = new SubTask("Счет за воду", "2222 рубля", Statuses.NEW, 1
                , 120, LocalDateTime.of(2022, 10, 5, 10, 30));
        SubTask subTask2 = new SubTask("Счет за газ", "557 рублей", Statuses.IN_PROGRESS, 2, 1);

        taskManager.makeEpic(epic);
        taskManager.makeSubtask(subTask1);
        taskManager.updateSubtask(subTask2);
        List<Task> list = new ArrayList<>();
        list.add(subTask2);
        assertArrayEquals(list.toArray(), taskManager.getPrioritizedTasks().toArray());
    }

    @Test
    public void validatorTimeTasksTest() {
        Epic epic = new Epic("Счета", "На оплату");
        SubTask subTask1 = new SubTask("База", "0", Statuses.NEW, 1
                , 60, LocalDateTime.of(2022, 1, 1, 12, 0));
        SubTask subTask2 = new SubTask("Наслойка сзади", "1", Statuses.NEW, 1
                , 60, LocalDateTime.of(2022, 1, 1, 12, 30));
        SubTask subTask3 = new SubTask("Наслойка внутри", "2", Statuses.NEW, 1
                , 10, LocalDateTime.of(2022, 1, 1, 12, 30));
        SubTask subTask4 = new SubTask("Наслойка спереди", "3", Statuses.NEW, 1
                , 60, LocalDateTime.of(2022, 1, 1, 11, 30));
        SubTask subTask5 = new SubTask("Наслойка снаружи1", "4", Statuses.NEW, 1
                , 10, LocalDateTime.of(2022, 2, 1, 12, 10));
        SubTask subTask6 = new SubTask("Наслойка снаружи2", "4", Statuses.NEW, 1
                , 60, LocalDateTime.of(2022, 2, 1, 12, 0));

        taskManager.makeEpic(epic);
        taskManager.makeSubtask(subTask1);

        final IllegalArgumentException exception1 = assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.makeSubtask(subTask2));
        assertEquals("Даты пересекаются у задач с номерами: " + subTask1.getId() + " и " + subTask2.getId(), exception1.getMessage());

        final IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.makeSubtask(subTask3));
        assertEquals("Даты пересекаются у задач с номерами: " + subTask1.getId() + " и " + subTask3.getId(), exception2.getMessage());

        final IllegalArgumentException exception3 = assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.makeSubtask(subTask4));
        assertEquals("Даты пересекаются у задач с номерами: " + subTask1.getId() + " и " + subTask4.getId(), exception3.getMessage());

        taskManager.makeSubtask(subTask5);
        final IllegalArgumentException exception4 = assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.makeSubtask(subTask6));
        assertEquals("Даты пересекаются у задач с номерами: " + subTask5.getId() + " и " + subTask6.getId(), exception4.getMessage());
    }
}