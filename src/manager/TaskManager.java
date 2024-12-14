package manager;

import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private int counter;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        counter = 1;
    }

    private int nextId() {
        return counter++;
    }

    //TASKS//
    public Task createTask(Task newTask) {
        int newId = nextId();
//        Нам на QA вебинаре посоветовали так сделать, использовать разные ссылки на задачи для мапы и те,
//        которые возвращаем, но действительно, так проще)
//        Task createdTask = new Task(newId, newTask.getName(), newTask.getDescription(), newTask.getStatus());
//        tasks.put(createdTask.getId(), createdTask);
        newTask.setId(newId);
        tasks.put(newId, newTask);
        return newTask;
    }

    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task existingTask = tasks.get(task.getId());
            existingTask.setName(task.getName());
            existingTask.setDescription(task.getDescription());
            existingTask.setStatus(task.getStatus());
        }
        return task;
    }

    public Task deleteTask(Integer id) {
        return tasks.remove(id);
    }

    public void deleteAllTasks() {
        tasks = new HashMap<>();
    }

    public ArrayList<Task> findAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task findTaskById(Integer id) {
        return tasks.get(id);
    }

    //EPICS//
    public void createEpic(Epic newEpic) {
        int newId = nextId();
        Epic createdEpic = new Epic(newId, newEpic.getName(), newEpic.getDescription());
        epics.put(createdEpic.getId(), createdEpic);
        newEpic.setId(newId);
    }

    public Epic getEpic(Integer id) {
        return epics.get(id);
    }

    public Epic updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic existingEpic = epics.get(epic.getId());
            existingEpic.setName(epic.getName());
            existingEpic.setDescription(epic.getDescription());
        }
        return epic;
    }

    public Epic deleteEpic(Integer id) {
        Epic existingEpic = epics.get(id);
        if (existingEpic != null) {
            for (Integer subtaskId : existingEpic.getSubtasksId()) {
                subtasks.remove(subtaskId);
            }
        }
        return epics.remove(id);
    }

    public void deleteAllEpics() {
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public ArrayList<Epic> findAllEpics() {
        return new ArrayList<>(epics.values());
    }

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
    public Subtask createSubtask(Subtask newSubtask) {
        Epic epic = epics.get(newSubtask.getEpicId());
        if (epic != null) {
            int newId = nextId();
            subtasks.put(newId, newSubtask);
            newSubtask.setId(newId);
            epic.addSubtask(newSubtask.getId());
            updateEpicStatus(epic);
        }
        return newSubtask;
    }

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

    public void deleteSubtask(Integer id) {
        Subtask subtask = subtasks.get(id);
        subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
                updateEpicStatus(epic);
            }
        }
    }

// Немного не понял тут комментарий: "И удалить подзадачи у эпиков, а также обновить статусы всех эпиков"
// Если я стираю все Сабтаски, то мне выводит null при поиске Сабтасков в определенном Эпике,
// проверял в Мэйне, тест оставил. Добавил обновление статуса в Эпиках
    public void deleteAllSubtask() {
        subtasks = new HashMap<>();
        for (Epic epic : epics.values()) {
            epic.getSubtasksId().clear();
            updateEpicStatus(epic);
        }

    }

    public ArrayList<Subtask> findAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Subtask findSubtaskById(Integer id) {
        if (subtasks.containsKey(id)) {
            Subtask existingSubtask = subtasks.get(id);
            return existingSubtask;
        } else {
            return null;
        }
    }
}