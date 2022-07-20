package tests;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Statuses;
import tasks.SubTask;

class EpicTest {

    private final TaskManager manager = Managers.getDefault();

    @Test
    public void setNewForNullList () {
        Epic epic = new Epic("Книги", "На прочтение");
        manager.makeEpic(epic);
        Assertions.assertEquals("NEW", epic.getStatus().toString());
    }

    @Test
    public void setNewForNewList () {
        Epic epic = new Epic("Книги", "На прочтение");
        SubTask subTask1 = new SubTask("Карл Маркс \"Капитал\"", "Экономика", Statuses.NEW, 1);
        manager.makeEpic(epic);
        manager.makeSubtask(subTask1);
        Assertions.assertEquals("NEW", epic.getStatus().toString());
    }

    @Test
    public void setDoneForDoneList () {
        Epic epic = new Epic("Книги", "На прочтение");
        SubTask subTask1 = new SubTask("Карл Маркс \"Капитал\"", "Экономика", Statuses.DONE, 1);
        manager.makeEpic(epic);
        manager.makeSubtask(subTask1);
        Assertions.assertEquals("DONE", epic.getStatus().toString());
    }

   @Test
    public void setInProgressForNewAndDoneList () {
        Epic epic = new Epic("Книги", "На прочтение");
        SubTask subTask1 = new SubTask("Карл Маркс \"Капитал\"", "Экономика", Statuses.NEW, 1);
        SubTask subTask2 = new SubTask("Карлос Кастанеда \"Путешествие в Икстлан\"", "Философия", Statuses.DONE, 1);
        manager.makeEpic(epic);
        manager.makeSubtask(subTask1);
        manager.makeSubtask(subTask2);
       Assertions.assertEquals("IN_PROGRESS", epic.getStatus().toString());
    }

    @Test
    public void setInProgressForInProgressList () {
        Epic epic = new Epic("Книги", "На прочтение");
        SubTask subTask1 = new SubTask("Карл Маркс \"Капитал\"", "Экономика", Statuses.IN_PROGRESS, 1);
        manager.makeEpic(epic);
        manager.makeSubtask(subTask1);
        Assertions.assertEquals("IN_PROGRESS", epic.getStatus().toString());
    }

}