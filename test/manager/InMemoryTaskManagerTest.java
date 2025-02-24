package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void init() {
        taskManager = Managers.getDefault();
    }

    @Test
    void getHistory() {
        Task firstTask = new Task(null, "Task 1 name", "Task 1 description", Status.NEW);
        Task secondTask = new Task(null, "Task 2 name", "Task 2 description", Status.NEW);

        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);
        Task createdFirstTask = taskManager.getTask(firstTask.getId());
        Task createdSecondTask = taskManager.getTask(secondTask.getId());
        List<Task> tasksInHistory = taskManager.getHistory();

        Assertions.assertEquals(createdFirstTask, tasksInHistory.get(0), "Задача 1 должна быть равна задачи" +
                " в истории");
        Assertions.assertEquals(createdSecondTask, tasksInHistory.get(1), "Задача 2 должна быть равна задачи" +
                " в истории");
    }

    @Test
    void createTask() {
        String name = "Task 1 name";
        String description = "Task 1 description";
        Task task = new Task(null, name, description, Status.NEW);

        taskManager.createTask(task);
        Task createdTask = taskManager.getTask(task.getId());

        Assertions.assertEquals(createdTask.getStatus(), Status.NEW, "Статус задачи должен быть NEW");
        Assertions.assertEquals(createdTask.getName(), name, "Имя задачи должно быть (Task 1 name)");
        Assertions.assertEquals(createdTask.getDescription(), description, "Описание задачи должно быть" +
                " (Task 1 description)");
    }

    @Test
    void getTask() {
        String name = "Task 1 name";
        String description = "Task 1 description";
        Task task = new Task(null, name, description, Status.NEW);

        taskManager.createTask(task);
        Task createdTask = taskManager.getTask(task.getId());

        Assertions.assertEquals(createdTask.getStatus(), Status.NEW, "Статус задачи должен быть NEW");
        Assertions.assertEquals(createdTask.getName(), name, "Имя задачи должно быть (Task 1 name)");
        Assertions.assertEquals(createdTask.getDescription(), description, "Описание задачи должно быть" +
                " (Task 1 description)");
    }

    @Test
    void updateTask() {
        String name = "Task 1 name";
        String description = "Task 1 description";
        Task task = new Task(null, name, description, Status.NEW);

        taskManager.createTask(task);
        Task createdTask = taskManager.getTask(task.getId());
        createdTask.setStatus(Status.DONE);
        taskManager.updateTask(createdTask);
        Task updatedTask = taskManager.getTask(createdTask.getId());

        Assertions.assertEquals(createdTask.getStatus(), Status.DONE, "Статус задачи должен быть DONE");
        Assertions.assertEquals(createdTask.getName(), name, "Имя задачи должно быть (Task 1 name)");
        Assertions.assertEquals(createdTask.getDescription(), description, "Описание задачи должно быть" +
                " (Task 1 description)");
    }

    /* Тут ругается на получение id когда сравниваем с null, не могу понять почему так не получается сделать
        и как тогда сделать тест для удаления задачи... Тоже самое с удалением Epic и Subtask */
    @Test
    void deleteTask() {
        Task task = new Task(null, "Task name", "Task description", Status.NEW);
        taskManager.createTask(task);
        int taskId = task.getId();

        Assertions.assertNotNull(taskManager.getTask(taskId), "Задача должна существовать до удаления");

        taskManager.deleteTask(taskId);

        Assertions.assertNull(taskManager.getTask(taskId), "Задача должна быть удалена"); // тут ошибка
    }


    @Test
    void deleteAllTasks() {
        Task firstTask = new Task(null, "Task 1 name", "Task 1 description", Status.NEW);
        Task secondTask = new Task(null, "Task 2 name", "Task 2 description", Status.NEW);

        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);

        Assertions.assertEquals(2, taskManager.findAllTasks().size(), "Должно быть 2 задачи" +
                " до удаления");

        taskManager.deleteAllTasks();

        Assertions.assertTrue(taskManager.findAllTasks().isEmpty(), "Все задачи должны быть удалены");
    }

    @Test
    void findAllTasks() {
        Task firstTask = new Task(null, "Task 1 name", "Task 1 description", Status.NEW);
        Task secondTask = new Task(null, "Task 2 name", "Task 2 description", Status.NEW);

        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);
        Task createdFirstTask = taskManager.getTask(firstTask.getId());
        Task createdSecondTask = taskManager.getTask(secondTask.getId());
        List<Task> allTasks = taskManager.findAllTasks();

        Assertions.assertEquals(createdFirstTask, allTasks.get(0), "Task 1 должен быть найден");
        Assertions.assertEquals(createdSecondTask, allTasks.get(1), "Task 2 должен быть найден");
    }

    @Test
    void findTaskById() {
        Task firstTask = new Task(null, "Task 1 name", "Task 1 description", Status.NEW);

        taskManager.createTask(firstTask);
        Task createdFirstTask = taskManager.getTask(firstTask.getId());
        Task taskFoundById = taskManager.findTaskById(createdFirstTask.getId());

        Assertions.assertEquals(taskFoundById, createdFirstTask, "Task должен быть найден");
    }

    @Test
    void createEpic() {
        String epicName = "Epic name";
        String epicDescription = "Epic description";
        Epic epic = new Epic(null, epicName, epicDescription);

        taskManager.createEpic(epic);
        Epic createdEpic = taskManager.getEpic(epic.getId());

        Assertions.assertEquals(createdEpic.getName(), epicName, "Имя Эпика должно быть (Epic name)");
        Assertions.assertEquals(createdEpic.getDescription(), epicDescription, "Описание Эпика должно быть" +
                " (Epic description)");
    }

    @Test
    void getEpic() {
        String epicName = "Epic name";
        String epicDescription = "Epic description";
        Epic epic = new Epic(null, epicName, epicDescription);

        taskManager.createEpic(epic);
        Epic createdEpic = taskManager.getEpic(epic.getId());

        Assertions.assertEquals(createdEpic.getName(), epicName, "Имя Эпика должно быть (Epic name)");
        Assertions.assertEquals(createdEpic.getDescription(), epicDescription, "Описание Эпика должно быть" +
                " (Epic description)");
    }

    @Test
    void updateEpic() {
        String epicName = "Epic name";
        String epicDescription = "Epic description";
        Epic epic = new Epic(null, epicName, epicDescription);

        taskManager.createEpic(epic);
        Epic createdEpic = taskManager.getEpic(epic.getId());
        createdEpic.setStatus(Status.DONE);
        taskManager.updateTask(createdEpic);
        Epic updatedEpic = taskManager.getEpic(createdEpic.getId());

        Assertions.assertEquals(updatedEpic.getStatus(), Status.DONE, "Статус Эпика должен быть DONE");
        Assertions.assertEquals(updatedEpic.getName(), epicName, "Имя Эпика должно быть (Epic name)");
        Assertions.assertEquals(updatedEpic.getDescription(), epicDescription, "Описание Эпика должно быть" +
                " (Epic description)");
    }

    //Запустил код сейчас, все работает! Странно...
    @Test
    void deleteEpic() {
        Epic epic = new Epic(null, "Epic name", "Epic description");
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask(null, "Subtask", "Description", Status.NEW, epicId);
        taskManager.createSubtask(subtask);

        Assertions.assertNotNull(taskManager.getEpic(epicId), "Эпик должен существовать до удаления");
        Assertions.assertNotNull(taskManager.getSubtask(subtask.getId()), "Подзадача должна существовать " +
                "до удаления");

        taskManager.deleteEpic(epicId);

        Assertions.assertNull(taskManager.getEpic(epicId), "Эпик должен быть удален");
        Assertions.assertNull(taskManager.getSubtask(subtask.getId()), "Подзадача должна быть удалена " +
                "вместе с эпиком");
    }

    @Test
    void deleteAllEpics() {
        Epic epic1 = new Epic(null, "Epic 1 name", "Epic 1 description");
        Epic epic2 = new Epic(null, "Epic 2 name", "Epic 2 description");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask(null, "Subtask 1 name", "Subtask 1 description", Status.NEW,
                epic1.getId());
        Subtask subtask2 = new Subtask(null, "Subtask 2 name", "Subtask 2 description", Status.NEW,
                epic2.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Assertions.assertEquals(2, taskManager.findAllEpics().size(), "Должно быть 2 эпика до " +
                "удаления");
        Assertions.assertEquals(2, taskManager.findAllSubtasks().size(), "Должно быть 2 подзадачи" +
                " до удаления");

        taskManager.deleteAllEpics();

        Assertions.assertTrue(taskManager.findAllEpics().isEmpty(), "Все эпики должны быть удалены");
        Assertions.assertTrue(taskManager.findAllSubtasks().isEmpty(), "Все подзадачи должны быть удалены");
    }

    @Test
    void findAllEpics() {
        Epic firstEpic = new Epic(null, "Epic 1 name", "Epic 1 description");
        Epic secondEpic = new Epic(null, "Epic 2 name", "Epic 2 description");

        taskManager.createEpic(firstEpic);
        taskManager.createEpic(secondEpic);
        Epic createdFirstEpic = taskManager.getEpic(firstEpic.getId());
        Epic createdSecondEpic = taskManager.getEpic(secondEpic.getId());

        List<Epic> allEpics = taskManager.findAllEpics();

        Assertions.assertEquals(createdFirstEpic, allEpics.get(0), "Epic 1 должен быть найден");
        Assertions.assertEquals(createdSecondEpic, allEpics.get(1), "Epic 2 должен быть найден");
    }

    @Test
    void findEpicById() {
        Epic firstEpic = new Epic(null, "Epic 1 name", "Epic 1 description");

        taskManager.createEpic(firstEpic);
        Epic createdFirstEpic = taskManager.getEpic(firstEpic.getId());
        Epic epicFoundById = taskManager.findEpicById(createdFirstEpic.getId());

        Assertions.assertEquals(epicFoundById, createdFirstEpic, "Epic 1 должен быть найден");
    }

    @Test
    void getSubtasksOfEpic() {
        Epic epic = new Epic(null, "Epic name", "Epic description");
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        Subtask subtask1 = new Subtask(null, "Subtask 1 name", "Subtask 1 description",
                Status.NEW, epicId);
        Subtask subtask2 = new Subtask(null, "Subtask 2 name", "Subtask 2 description",
                Status.IN_PROGRESS, epicId);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        List<Subtask> subtasksOfEpic = taskManager.getSubtasksOfEpic(epicId);

        Assertions.assertEquals(2, subtasksOfEpic.size(), "Должно быть 2 подзадачи у эпика");

        Assertions.assertTrue(subtasksOfEpic.contains(subtask1), "Subtask 1 должен быть в списке");
        Assertions.assertTrue(subtasksOfEpic.contains(subtask2), "Subtask 2 должен быть в списке");

        // Проверяем случай, когда у эпика нет подзадач
        Epic epicWithoutSubtasks = new Epic(null, "Epic without subtasks",
                "Epic without subtasks description");
        taskManager.createEpic(epicWithoutSubtasks);
        int epicWithoutSubtasksId = epicWithoutSubtasks.getId();

        List<Subtask> subtasksOfEpicWithoutSubtasks = taskManager.getSubtasksOfEpic(epicWithoutSubtasksId);

        Assertions.assertTrue(subtasksOfEpicWithoutSubtasks.isEmpty(), "Список подзадач должен быть пустым" +
                " для эпика без подзадач");
    }

    @Test
    void createSubtask() {
        String epicName = "Epic name";
        String epicDescription = "Epic description";
        Epic epic = new Epic(null, epicName, epicDescription);
        taskManager.createEpic(epic);
        String subtaskName = "Subtask name";
        String subtaskDescription = "Subtask description";
        Subtask subtask = new Subtask(null, subtaskName, subtaskDescription, Status.NEW, epic.getId());

        taskManager.createSubtask(subtask);
        Subtask createdSubtask = taskManager.getSubtask(subtask.getId());

        Assertions.assertEquals(createdSubtask.getName(), subtaskName, "Имя подзадачи должно быть" +
                " (Subtask name)");
        Assertions.assertEquals(createdSubtask.getDescription(), subtaskDescription, "Описание подзадачи " +
                "должно быть (Subtask description)");
        Assertions.assertEquals(createdSubtask.getStatus(), Status.NEW, "Статус подзадачи должен быть NEW");
        Assertions.assertEquals(createdSubtask.getEpicId(), epic.getId(), "Id Эпика Подзадачи " +
                "не должно быть null");
    }

    @Test
    void getSubtask() {
        String epicName = "Epic name";
        String epicDescription = "Epic description";
        Epic epic = new Epic(null, epicName, epicDescription);
        taskManager.createEpic(epic);
        String subtaskName = "Subtask name";
        String subtaskDescription = "Subtask description";
        Subtask subtask = new Subtask(null, subtaskName, subtaskDescription, Status.NEW, epic.getId());

        taskManager.createSubtask(subtask);
        Subtask createdSubtask = taskManager.getSubtask(subtask.getId());

        Assertions.assertEquals(createdSubtask.getName(), subtaskName, "Имя подзадачи должно быть" +
                " (Subtask name)");
        Assertions.assertEquals(createdSubtask.getDescription(), subtaskDescription, "Описание подзадачи " +
                "должно быть (Subtask description)");
        Assertions.assertEquals(createdSubtask.getStatus(), Status.NEW, "Статус подзадачи должен быть NEW");
        Assertions.assertEquals(createdSubtask.getEpicId(), epic.getId(), "Id Эпика Подзадачи " +
                "не должно быть null");
    }

    @Test
    void updateSubtask() {
        String epicName = "Epic name";
        String epicDescription = "Epic description";
        Epic epic = new Epic(null, epicName, epicDescription);
        taskManager.createEpic(epic);
        String subtaskName = "Subtask name";
        String subtaskDescription = "Subtask description";
        Subtask subtask = new Subtask(null, subtaskName, subtaskDescription, Status.NEW, epic.getId());

        taskManager.createSubtask(subtask);
        Subtask createdSubtask = taskManager.getSubtask(subtask.getId());
        createdSubtask.setStatus(Status.DONE);
        taskManager.updateSubtask(createdSubtask);
        Subtask updatedSubtask = taskManager.getSubtask(createdSubtask.getId());

        Assertions.assertEquals(updatedSubtask.getName(), subtaskName, "Имя подзадачи должно быть" +
                " (Subtask name)");
        Assertions.assertEquals(updatedSubtask.getDescription(), subtaskDescription, "Описание подзадачи" +
                " должно быть (Subtask description)");
        Assertions.assertEquals(updatedSubtask.getStatus(), Status.DONE, "Статус подзадачи должен быть DONE");
        Assertions.assertEquals(createdSubtask.getEpicId(), epic.getId(), "Id Эпика Подзадачи " +
                "не должно быть null");
    }

    @Test
    void deleteSubtask() {
        Epic epic = new Epic(null, "Epic", "Description");
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask(null, "Subtask", "Description", Status.NEW, epicId);
        taskManager.createSubtask(subtask);
        int subtaskId = subtask.getId();

        Assertions.assertNotNull(taskManager.getSubtask(subtaskId), "Подзадача должна существовать до удаления");

        taskManager.deleteSubtask(subtaskId);

        Assertions.assertNull(taskManager.getSubtask(subtaskId), "Подзадача должна быть удалена");
    }

    @Test
    void deleteAllSubtask() {
        Epic epic = new Epic(null, "Epic", "Description");
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        Subtask subtask1 = new Subtask(null, "Subtask 1 name", "Subtask 1 description",
                Status.NEW, epicId);
        Subtask subtask2 = new Subtask(null, "Subtask 2 name", "Subtask 1 description",
                Status.NEW, epicId);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Assertions.assertEquals(2, taskManager.findAllSubtasks().size(), "Должно быть 2 подзадачи" +
                " до удаления");

        taskManager.deleteAllSubtask();

        Assertions.assertTrue(taskManager.findAllSubtasks().isEmpty(), "Все подзадачи должны быть удалены");
    }

    @Test
    void findAllSubtasks() {
        Epic epic = new Epic(null, "Epic name", "Epic description");
        taskManager.createEpic(epic);
        Subtask firstSubtask = new Subtask(null, "Subtask 1 name", "Subtask 1 description",
                Status.NEW, epic.getId());
        Subtask secondSubtask = new Subtask(null, "Subtask 1 name", "Subtask 1 description",
                Status.NEW, epic.getId());

        taskManager.createSubtask(firstSubtask);
        taskManager.createSubtask(secondSubtask);
        Subtask createdFirstSubtask = taskManager.getSubtask(firstSubtask.getId());
        Subtask createdSecondSubtask = taskManager.getSubtask(secondSubtask.getId());

        List<Subtask> allSubtask = taskManager.findAllSubtasks();

        Assertions.assertEquals(createdFirstSubtask, allSubtask.get(0), "Subtask 1 должен быть найден");
        Assertions.assertEquals(createdSecondSubtask, allSubtask.get(1), "Subtask 2 должен быть найден");
    }

    @Test
    void findSubtaskById() {
        Epic epic = new Epic(null, "Epic name", "Epic description");
        taskManager.createEpic(epic);
        Subtask firstSubtask = new Subtask(null, "Subtask 1 name", "Subtask 1 description",
                Status.NEW, epic.getId());

        taskManager.createSubtask(firstSubtask);
        Subtask createdFirstSubtask = taskManager.getSubtask(firstSubtask.getId());
        Subtask subtaskFoundById = taskManager.findSubtaskById(createdFirstSubtask.getId());

        Assertions.assertEquals(createdFirstSubtask, subtaskFoundById, "Subtask 1 должен быть найден");
    }

    @Test
    void taskInHistoryListShouldNotBeUpdatedAfterTaskUpdate() {
        Task task = new Task(null, "Task name", "Task description", Status.NEW);

        taskManager.createTask(task);
        taskManager.getTask(task.getId());

        List<Task> history = taskManager.getHistory();
        Task taskInHistory = history.get(0);

        Status statusInHistoryBeforeUpdate = taskInHistory.getStatus();

        task.setStatus(Status.DONE);
        taskManager.updateTask(task);

        Task taskInHistoryAfterUpdate = taskManager.getHistory().get(0);

        Assertions.assertEquals(statusInHistoryBeforeUpdate, taskInHistoryAfterUpdate.getStatus(),
                "Статус в истории не должен меняться");
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

    @Test
    void historyManagerShouldKeepLast10ViewedTasks() {
        TaskManager taskManager = Managers.getDefault();

        for (int i = 1; i <= 12; i++) {
            Task task = new Task(i, "Task " + i, "Description " + i, Status.NEW);
            taskManager.createTask(task);
        }

        for (int i = 1; i <= 12; i++) {
            taskManager.getTask(i);
        }

        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(10, history.size(), "История должна содержать только 10 последних " +
                "задач");

        for (int i = 0; i < history.size(); i++) {
            Assertions.assertEquals(i + 3, history.get(i).getId(), "История должна содержать " +
                    "последние 10 задач с 3 до 12");
        }
    }
}