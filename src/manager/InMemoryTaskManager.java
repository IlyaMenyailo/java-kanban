package manager;

import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private int counter;
    private HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        this.historyManager = historyManager;
        counter = 1;
    }

    private int nextId() {

        return counter++;
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
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
        Task taskForHistory = new Task(task.getId(), task.getName(), task.getDescription(), task.getStatus());
        historyManager.addToHistory(taskForHistory);
        return tasks.get(id);
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

        tasks = new HashMap<>();
    }

    @Override
    public ArrayList<Task> findAllTasks() {

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
        Epic createdEpic = new Epic(newId, newEpic.getName(), newEpic.getDescription());
        epics.put(createdEpic.getId(), createdEpic);
        newEpic.setId(newId);
    }

    @Override
    public Epic getEpic(Integer id) {
        Epic epic = epics.get(id);
        Epic epicForHistory = new Epic(epic.getId(), epic.getName(), epic.getDescription());
        historyManager.addToHistory(epicForHistory);
        return epics.get(id);
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
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    @Override
    public ArrayList<Epic> findAllEpics() {

        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic findEpicById(Integer id) {

        return epics.get(id);
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasksOfEpic = getSubtasksOfEpic(epic.getId());
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
    public ArrayList<Subtask> getSubtasksOfEpic(Integer epicId) {
        ArrayList<Subtask> result = new ArrayList<>();
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
        Subtask subtaskForHistory = new Subtask(subtask.getId(), subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getEpicId());
        historyManager.addToHistory(subtaskForHistory);
        return subtasks.get(id);
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
        subtasks = new HashMap<>();
        for (Epic epic : epics.values()) {
            epic.getSubtasksId().clear();
            updateEpicStatus(epic);
        }

    }

    @Override
    public ArrayList<Subtask> findAllSubtasks() {

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