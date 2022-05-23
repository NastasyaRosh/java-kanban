package Manager;

import Tasks.Task;

import java.util.LinkedList;
import java.util.List;

class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.size() > 9) {
                history.removeFirst();
            }
            history.addLast(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
