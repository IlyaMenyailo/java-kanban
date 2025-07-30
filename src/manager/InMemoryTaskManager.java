package manager;

import exeption.ManagerSaveException;
import status.Status;
import status.TaskType;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected int counter;
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

    private final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(
                    Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder())
            ));

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return new TreeSet<>(prioritizedTasks);
    }

    private boolean hasTimeOverlap(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            return false;
        }

        return prioritizedTasks.stream()
                .filter(task -> task.getId() != newTask.getId())
                .anyMatch(existingTask -> isTimeOverlapping(newTask, existingTask));
    }

    private boolean isTimeOverlapping(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    //TASKS//
    @Override
    public void createTask(Task newTask) {
        if (newTask == null) return;

        int newId = nextId();
        newTask.setId(newId);

        if (hasTimeOverlap(newTask)) {
            throw new ManagerSaveException("Задача пересекается по времени с существующей",
                    new IllegalArgumentException("Время выполнения задачи пересекается с другой задачей"));
        }

        tasks.put(newId, newTask);
        if (newTask.getStartTime() != null) {
            prioritizedTasks.add(newTask);
        }
    }

    @Override
    public Task getTask(Integer id) {
        Task task = tasks.get(id);
        historyManager.addToHistory(task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) {
            return null;
        }

        if (hasTimeOverlap(task)) {
            throw new ManagerSaveException("Обновленная задача пересекается по времени",
                    new IllegalArgumentException("Невозможно обновить - пересечение времени выполнения"));
        }

        prioritizedTasks.remove(tasks.get(task.getId()));

        if (tasks.containsKey(task.getId())) {
            Task existingTask = tasks.get(task.getId());
            existingTask.setName(task.getName());
            existingTask.setDescription(task.getDescription());
            existingTask.setStatus(task.getStatus());
        }

        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        return task;
    }

    @Override
    public Task deleteTask(Integer id) {
        Task removedTask = tasks.remove(id);
        if (removedTask != null) {
            prioritizedTasks.remove(removedTask);
            historyManager.remove(id);
        }
        return removedTask;
    }

    @Override
    public void deleteAllTasks() {
        tasks.keySet().forEach(historyManager::remove);
        prioritizedTasks.removeIf(task -> task.getType() == TaskType.TASK);
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
        updateEpicStatus(newEpic);
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
                historyManager.remove(subtaskId);
                subtasks.remove(subtaskId);
                prioritizedTasks.removeIf(task -> task.getId() == subtaskId);
            }
            historyManager.remove(id);
        }
        return epics.remove(id);
    }

    @Override
    public void deleteAllEpics() {
        epics.keySet().forEach(historyManager::remove);
        subtasks.keySet().forEach(id -> {
            historyManager.remove(id);
            prioritizedTasks.removeIf(task -> task.getId() == id);
        });

        prioritizedTasks.removeIf(task -> task.getType() == TaskType.EPIC);
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

        if (subtasksOfEpic.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = subtasksOfEpic.stream()
                .allMatch(subtask -> subtask.getStatus() == Status.NEW);
        boolean allDone = subtasksOfEpic.stream()
                .allMatch(subtask -> subtask.getStatus() == Status.DONE);

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
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return List.of();
        }
        return epic.getSubtasksId().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .toList();
    }

    private void updateEpicTime(Epic epic) {
        List<Subtask> subtasksList = getSubtasksOfEpic(epic.getId());

        if (subtasksList.isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(null);
            epic.setEndTime(null);
            return;
        }

        LocalDateTime start = subtasksList.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime end = subtasksList.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        Duration duration = subtasksList.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setStartTime(start);
        epic.setDuration(duration);
        epic.setEndTime(end);
    }

    //SUBTASKS//
    @Override
    public void createSubtask(Subtask newSubtask) {
        if (newSubtask == null || !epics.containsKey(newSubtask.getEpicId())) {
            return;
        }

        Epic epic = epics.get(newSubtask.getEpicId());
        if (epic != null) {
            int newId = nextId();
            newSubtask.setId(newId);
            subtasks.put(newId, newSubtask);
            epic.addSubtask(newSubtask.getId());
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }

        if (hasTimeOverlap(newSubtask)) {
            throw new ManagerSaveException("Подзадача пересекается по времени с существующей",
                    new IllegalArgumentException("Подзадача не может быть создана - пересечение времени"));
        }

        if (newSubtask.getStartTime() != null) {
            prioritizedTasks.add(newSubtask);
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
        if (subtask == null || !subtasks.containsKey(subtask.getId())) {
            return null;
        }

        if (!epics.containsKey(subtask.getEpicId())) {
            return null;
        }

        if (hasTimeOverlap(subtask)) {
            throw new ManagerSaveException("Обновленная подзадача пересекается по времени",
                    new IllegalArgumentException("Обновление невозможно - пересечение времени выполнения"));
        }

        Subtask existingSubtask = subtasks.get(subtask.getId());
        prioritizedTasks.remove(existingSubtask);

        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());

        updateEpicStatus(epic);
        updateEpicTime(epic);

        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }

        return subtask;
    }

    @Override
    public void deleteSubtask(Integer id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            prioritizedTasks.remove(subtask);
            historyManager.remove(id);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
                updateEpicStatus(epic);
                updateEpicTime(epic);
            }
        }
    }

    @Override
    public void deleteAllSubtask() {
        subtasks.keySet().forEach(id -> {
            historyManager.remove(id);
            prioritizedTasks.removeIf(task -> task.getId() == id);
        });

        subtasks.clear();

        epics.values().forEach(epic -> {
            epic.getSubtasksId().clear();
            updateEpicStatus(epic);
            updateEpicTime(epic);

        });
    }

    @Override
    public List<Subtask> findAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask findSubtaskById(Integer id) {
        return subtasks.getOrDefault(id, null);
    }
}