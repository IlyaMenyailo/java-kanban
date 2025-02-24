package manager;

import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Epic> epics;
    private Map<Integer, Subtask> subtasks;
    private int counter;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        counter = 1;
    }

    private int nextId() {

        return counter++;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //TASKS//
    @Override
    public void createTask(Task newTask) {
        int newId = nextId();
        newTask.setId(newId);
        tasks.put(newId, newTask);
    }

    @Override
    public Task getTask(Integer id) {
        Task task = tasks.get(id);
        historyManager.addToHistory(task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task existingTask = tasks.get(task.getId());
            existingTask.setName(task.getName());
            existingTask.setDescription(task.getDescription());
            existingTask.setStatus(task.getStatus());
        }
        return task;
    }

    @Override
    public Task deleteTask(Integer id) {

        return tasks.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public List<Task> findAllTasks() {

        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task findTaskById(Integer id) {

        return tasks.get(id);
    }

    //EPICS//
    @Override
    public void createEpic(Epic newEpic) {
        int newId = nextId();
        newEpic.setId(newId);
        epics.put(newEpic.getId(), newEpic);
    }

    @Override
    public Epic getEpic(Integer id) {
        Epic epic = epics.get(id);
        historyManager.addToHistory(epic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic existingEpic = epics.get(epic.getId());
            existingEpic.setName(epic.getName());
            existingEpic.setDescription(epic.getDescription());
        }
        return epic;
    }

    @Override
    public Epic deleteEpic(Integer id) {
        Epic existingEpic = epics.get(id);
        if (existingEpic != null) {
            for (Integer subtaskId : existingEpic.getSubtasksId()) {
                subtasks.remove(subtaskId);
            }
        }
        return epics.remove(id);
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public List<Epic> findAllEpics() {

        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic findEpicById(Integer id) {

        return epics.get(id);
    }

    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtasksOfEpic = getSubtasksOfEpic(epic.getId());
        boolean allNew = true;
        boolean allDone = true;

        if (subtasksOfEpic.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        for (Subtask subtask : subtasksOfEpic) {
            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(Integer epicId) {
        List<Subtask> result = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtasksId()) {
                result.add(subtasks.get(subtaskId));
            }
        }
        return result;
    }

    //SUBTASKS//
    @Override
    public void createSubtask(Subtask newSubtask) {
        Epic epic = epics.get(newSubtask.getEpicId());
        if (epic != null) {
            int newId = nextId();
            subtasks.put(newId, newSubtask);
            newSubtask.setId(newId);
            epic.addSubtask(newSubtask.getId());
            updateEpicStatus(epic);
        }
    }

    @Override
    public Subtask getSubtask(Integer id) {
        Subtask subtask = subtasks.get(id);
        historyManager.addToHistory(subtask);
        return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Epic epic = epics.get(subtask.getEpicId());
            Subtask existingSubtask = subtasks.get(subtask.getId());
            existingSubtask.setName(subtask.getName());
            existingSubtask.setDescription(subtask.getDescription());
            existingSubtask.setStatus(subtask.getStatus());
            existingSubtask.setEpicId(subtask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            }
        }
        return subtask;
    }

    @Override
    public void deleteSubtask(Integer id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
                updateEpicStatus(epic);
            }
        }
    }

    @Override
    public void deleteAllSubtask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasksId().clear();
            updateEpicStatus(epic);
        }

    }

    @Override
    public List<Subtask> findAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask findSubtaskById(Integer id) {
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        } else {
            return null;
        }
    }
}