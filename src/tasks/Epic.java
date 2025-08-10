package tasks;

import status.Status;
import status.TaskType;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class Epic extends Task {

    private final List<Integer> subtasksId;
    private LocalDateTime endTime;

    public Epic(Integer id, String name, String description) {
        super(id, name, description, Status.NEW);
        this.subtasksId = new ArrayList<>();
        super.setDuration(null);
        super.setStartTime(null);
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

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", endTime=" + endTime +
                ", subtasksId=" + subtasksId +
                '}';
    }
}