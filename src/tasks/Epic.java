package tasks;

import status.Status;
import status.TaskType;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtasksId;

    public Epic(Integer id, String name, String description) {
        super(id, name, description, Status.NEW);
        this.subtasksId = new ArrayList<>();
    }

    public List<Integer> getSubtasksId() {

        return subtasksId;
    }

    public void addSubtask(Integer subtaskId) {

        subtasksId.add(subtaskId);
    }

    public void removeSubtask(Integer subtaskId) {

        subtasksId.remove(subtaskId);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "\n" + "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtasksId=" + subtasksId +
                '}';
    }
}