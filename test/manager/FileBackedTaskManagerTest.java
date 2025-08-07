package manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    public void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void testSaveAndLoadEmptyFile() throws IOException {
        manager.save();

        FileBackedTaskManager loadedFile = FileBackedTaskManager.loadFromFile(tempFile);
        String content = Files.readString(tempFile.toPath());

        Assertions.assertTrue(loadedFile.findAllTasks().isEmpty(), "Задач не должно быть");
        Assertions.assertTrue(loadedFile.findAllEpics().isEmpty(), "Эпиков не должно быть");
        Assertions.assertTrue(loadedFile.findAllTasks().isEmpty(), "Подзадач не должно быть");
        Assertions.assertEquals("id,type,name,status,description,epic\n", content);
    }

    @Test
    void testSaveAndLoadWithTasks() {
        Task task = new Task(null, "Task", "Task description", Status.NEW);
        manager.createTask(task);
        Epic epic = new Epic(null, "Epic", "Epic description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(null, "Subtask", "Subtask description", Status.NEW, epic.getId());
        manager.createSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> tasks = loadedManager.findAllTasks();
        Assertions.assertEquals(1, tasks.size());
        Assertions.assertEquals("Task", tasks.get(0).getName());

        List<Epic> epics = loadedManager.findAllEpics();
        Assertions.assertEquals(1, epics.size());
        Assertions.assertEquals("Epic", epics.get(0).getName());

        List<Subtask> subtasks = loadedManager.findAllSubtasks();
        Assertions.assertEquals(1, subtasks.size());
        Assertions.assertEquals("Subtask", subtasks.get(0).getName());
        Assertions.assertEquals(epic.getId(), subtasks.get(0).getEpicId());
    }
}
