package manager;

import exeption.ManagerSaveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Test
    void testTaskTimeOverlap() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task(null, "Task 1", "Description", Status.NEW,
                Duration.ofMinutes(30), now);
        Task task2 = new Task(null, "Task 2", "Description", Status.NEW,
                Duration.ofMinutes(30), now.plusMinutes(15));

        taskManager.createTask(task1);
        Assertions.assertThrows(ManagerSaveException.class, () -> taskManager.createTask(task2));
    }

    @Test
    void updateEpicTime() {
        Epic epic = new Epic(null, "Test epic", "Test description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(null, "Test subtask", "Test description", Status.NEW, epic.getId());
        subtask.setStartTime(LocalDateTime.of(2023, 1, 1, 10, 0));
        subtask.setDuration(Duration.ofHours(2));
        taskManager.createSubtask(subtask);

        Epic updatedEpic = taskManager.getEpic(epic.getId());
        Assertions.assertEquals(subtask.getStartTime(), updatedEpic.getStartTime(),
                "Неверное время начала эпика");
        Assertions.assertEquals(subtask.getEndTime(), updatedEpic.getEndTime(),
                "Неверное время окончания эпика");
    }

    @Test
    void taskEqualsTaskById() {
        Task task1 = new Task(1, "Task 1 name", "Task 1 description", Status.NEW);
        Task task2 = new Task(1, "Task 2 name", "Task 1 description", Status.DONE);

        Assertions.assertEquals(task1, task2, "Задачи должны быть равны по id");
    }

    @Test
    void inheritorsOfTheTaskEqualsEachOtherById() {
        Epic epic1 = new Epic(1, "Epic 1 name", "Epic 1 description");
        Epic epic2 = new Epic(1, "Epic 2 name", "Epic 1 description");

        Subtask subtask1 = new Subtask(1, "Subtask 1 name", "Subtask 1 description",
                Status.NEW, 1);
        Subtask subtask2 = new Subtask(1, "Subtask 2 name", "Subtask 1 description",
                Status.DONE, 1);

        Assertions.assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны");
        Assertions.assertEquals(subtask1, subtask2, "Подзадачи с одинаковым id должны быть равны");
    }

    @Test
    void managersUtilityClassReturnsInitializedManagers() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        Assertions.assertNotNull(taskManager, "TaskManager должен быть проинициализирован");
        Assertions.assertNotNull(historyManager, "HistoryManager должен быть проинициализирован");
    }

    @Test
    void inMemoryTaskManagerAddsAndFindsTasksById() {
        Task task = new Task(null, "Task name", "Task description", Status.NEW);
        Epic epic = new Epic(null, "Epic name", "Epic description");

        taskManager.createTask(task);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(null, "Subtask name", "Subtask description",
                Status.NEW, epic.getId());

        taskManager.createSubtask(subtask);

        Task foundTask = taskManager.getTask(task.getId());
        Epic foundEpic = taskManager.getEpic(epic.getId());
        Subtask foundSubtask = taskManager.getSubtask(subtask.getId());

        Assertions.assertNotNull(foundTask, "Task должен быть найден по id");
        Assertions.assertNotNull(foundEpic, "Epic должен быть найден по id");
        Assertions.assertNotNull(foundSubtask, "Subtask должен быть найден по id");
    }

    @Test
    void tasksWithAssignedAndGeneratedIdsDoNotConflict() {
        Task taskWithAssignedId = new Task(1, "Task 1 name", "Task 1 description 1", Status.NEW);
        Task taskWithGeneratedId = new Task(null, "Task 2 name", "Task 2 description 2", Status.NEW);

        taskManager.createTask(taskWithAssignedId);
        taskManager.createTask(taskWithGeneratedId);

        Task foundTaskWithAssignedId = taskManager.getTask(1);
        Task foundTaskWithGeneratedId = taskManager.getTask(taskWithGeneratedId.getId());

        Assertions.assertNotNull(foundTaskWithAssignedId, "Задача с заданным id должна быть получена");
        Assertions.assertNotNull(foundTaskWithGeneratedId, "Задача с сгенерированным id должна быть получена");
    }

    @Test
    void taskRemainsUnchangedWhenAddedToManager() {
        Task task = new Task(null, "Task name", "Task description", Status.NEW);
        taskManager.createTask(task);

        Task createdTask = taskManager.getTask(task.getId());

        Assertions.assertEquals(task.getName(), createdTask.getName(), "Имя задачи не должно изменяться");
        Assertions.assertEquals(task.getDescription(), createdTask.getDescription(), "Описание задачи не " +
                "должно изменяться");
        Assertions.assertEquals(task.getStatus(), createdTask.getStatus(), "Статус задачи не должен " +
                "изменяться");
    }
}