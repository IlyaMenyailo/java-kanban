package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    //TASKS//
    void createTask(Task newTask);

    Task getTask(Integer id);

    Task updateTask(Task task);

    Task deleteTask(Integer id);

    void deleteAllTasks();

    ArrayList<Task> findAllTasks();

    Task findTaskById(Integer id);

    //EPICS//
    void createEpic(Epic newEpic);

    Epic getEpic(Integer id);

    Epic updateEpic(Epic epic);

    Epic deleteEpic(Integer id);

    void deleteAllEpics();

    ArrayList<Epic> findAllEpics();

    Epic findEpicById(Integer id);

    ArrayList<Subtask> getSubtasksOfEpic(Integer epicId);

    //SUBTASKS//
    void createSubtask(Subtask newSubtask);

    Subtask getSubtask(Integer id);

    Subtask updateSubtask(Subtask subtask);

    void deleteSubtask(Integer id);

    void deleteAllSubtask();

    ArrayList<Subtask> findAllSubtasks();

    Subtask findSubtaskById(Integer id);
}
