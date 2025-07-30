package manager;

import exeption.ManagerSaveException;
import status.Status;
import status.TaskType;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class FileBackedTaskManager extends InMemoryTaskManager {

    public final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\r?\n|\r");

            for (int i = 1; i < lines.length; i++) {
                if (lines[i].isEmpty()) {
                    continue;
                }

                Task task = fromString(lines[i]);
                if (task != null) {
                    switch (task.getType()) {
                        case TASK:
                            manager.tasks.put(task.getId(), task);
                            break;
                        case EPIC:
                            manager.epics.put(task.getId(), (Epic) task);
                            break;
                        case SUBTASK:
                            manager.subtasks.put(task.getId(), (Subtask) task);
                            break;
                    }
                    if (task.getId() > manager.counter) {
                        manager.counter = task.getId();
                    }
                }
            }
            for (Subtask subtask : manager.subtasks.values()) {
                Epic epic = manager.epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.addSubtask(subtask.getId());
                }
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при загрузке из файла", exception);
        }
        return manager;
    }

    protected void save() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("id,type,name,status,description,epic\n");

            for (Task task : findAllTasks()) {
                builder.append(toString(task)).append("\n");
            }
            for (Epic epic : findAllEpics()) {
                builder.append(toString(epic)).append("\n");
            }
            for (Subtask subtask : findAllSubtasks()) {
                builder.append(toString(subtask)).append("\n");
            }

            Files.writeString(file.toPath(), builder.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", e);
        }
    }

    protected static String toString(Task task) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String startTimeStr = task.getStartTime() != null ? task.getStartTime().format(formatter) : "";
        String durationStr = task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "";

        String result = String.format("%d,%s,%s,%s,%s,%s,%s,",
                task.getId(),
                task.getType(),
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                durationStr,
                startTimeStr);

        if (task.getType() == TaskType.SUBTASK) {
            result = result + ((Subtask) task).getEpicId();
        }
        return result;
    }

    protected static Task fromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 5) return null;

        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        Duration duration = parts.length > 5 && !parts[5].isEmpty() ?
                Duration.ofMinutes(Long.parseLong(parts[5])) : null;
        LocalDateTime startTime = parts.length > 6 && !parts[6].isEmpty() ?
                LocalDateTime.parse(parts[6]) : null;

        switch (type) {
            case TASK:
                return new Task(id, name, description, status, duration, startTime);
            case EPIC:
                Epic epic = new Epic(id, name, description);
                epic.setStatus(status);
                epic.setDuration(duration);
                epic.setStartTime(startTime);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(parts[7]);
                return new Subtask(id, name, description, status, epicId, duration, startTime);
            default:
                return null;
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    @Override
    public Task deleteTask(Integer id) {
        Task deletedTask = super.deleteTask(id);
        save();
        return deletedTask;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
    }

    @Override
    public Epic deleteEpic(Integer id) {
        Epic deletedEpic = super.deleteEpic(id);
        save();
        return deletedEpic;
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        save();
        return updatedSubtask;
    }

    @Override
    public void deleteSubtask(Integer id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }


}
