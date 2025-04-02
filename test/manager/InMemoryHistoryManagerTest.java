package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Task;

import java.util.List;

public class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void initial() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task(1, "Task 1", "Description 1", Status.NEW);
        task2 = new Task(2, "Task 2", "Description 2", Status.IN_PROGRESS);
        task3 = new Task(3, "Task 3", "Description 3", Status.DONE);
    }

    @Test
    void addToHistory() {
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);

        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(task1, history.get(0));
        Assertions.assertEquals(task2, history.get(1));
    }

    @Test
    void removeFromHistory() {
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);

        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(task1, history.get(0));
        Assertions.assertEquals(task3, history.get(1));
    }

    @Test
    void removeFromHistoryNonExistingTask() {
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);

        historyManager.remove(4);
        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(task1, history.get(0));
        Assertions.assertEquals(task2, history.get(1));
    }

    @Test
    void shouldReturnEmptyHistoryIfNoTasksInHistory() {

        List<Task> history = historyManager.getHistory();

        Assertions.assertTrue(history.isEmpty());
    }
}
