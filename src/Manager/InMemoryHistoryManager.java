package Manager;

import Tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node<Task>> history = new HashMap<>();
    private Node<Task> first;
    private Node<Task> last;
    private int size = 0;

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.containsKey(task.getId(task))) {
                removeNode(history.get(task.getId(task)));
            }
            linkLast(task);
            history.put(task.getId(task), last);
        }
    }

    @Override
    public void remove(int id) {
        if (history.containsKey(id)) {
            removeNode(history.get(id));
            history.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        final Node<Task> l = last;
        final Node<Task> newNode = new Node<>(l, task, null);
        last = newNode;
        if (l == null) {
            first = newNode;
        } else {
            l.nextLink = newNode;
        }
        size++;
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> historyFromNode = new ArrayList<>();
        Node<Task> link = null;
        if (first != null) {
            historyFromNode.add(first.data);
            link = first.nextLink;
        }
        while (true) {
            if (link != null) {
                historyFromNode.add(link.data);
                link = link.nextLink;
            } else {
                break;
            }
        }
        return historyFromNode;
    }

    private void removeNode(Node<Task> node) {
        final Node<Task> next = node.nextLink;
        final Node<Task> prev = node.prevLink;

        if (node != null) {
            if (prev == null) {
                first = next;
            } else {
                prev.nextLink = next;
                node.prevLink = null;
            }

            if (next == null) {
                last = prev;
            } else {
                next.prevLink = prev;
                node.nextLink = null;
            }

            node.data = null;
            size--;
        }
    }

}
