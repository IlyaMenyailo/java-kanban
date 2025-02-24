package manager;

import tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_SIZE = 10;
    public LinkedList<Task> history = new LinkedList<>();

    @Override
    public void addToHistory(Task task) {
        if (task == null) {
            return;
        }
        if (history.size() >= MAX_SIZE) {
            history.removeFirst();
        }
        history.addLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(history);
    }
}
