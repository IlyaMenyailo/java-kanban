package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();

    //TASKS//
    void createTask(Task newTask);

    Task getTask(Integer id);

    Task updateTask(Task task);

    Task deleteTask(Integer id);

    void deleteAllTasks();

    List<Task> findAllTasks();

    Task findTaskById(Integer id);

    //EPICS//
    Epic createEpic(Epic newEpic);

    Epic getEpic(Integer id);

    Epic updateEpic(Epic epic);

    Epic deleteEpic(Integer id);

    void deleteAllEpics();

    List<Epic> findAllEpics();

    Epic findEpicById(Integer id);

    List<Subtask> getSubtasksOfEpic(Integer epicId);

    //SUBTASKS//
    void createSubtask(Subtask newSubtask);

    Subtask getSubtask(Integer id);

    Subtask updateSubtask(Subtask subtask);

    void deleteSubtask(Integer id);

    void deleteAllSubtask();

    List<Subtask> findAllSubtasks();

    Subtask findSubtaskById(Integer id);
}
