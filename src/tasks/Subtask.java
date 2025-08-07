package tasks;

import status.Status;
import status.TaskType;

import java.time.LocalDateTime;
import java.time.Duration;

public class Subtask extends Task {

    private Integer epicId;

    public Subtask(Integer id, String name, String description, Status status, Integer epicId) {
        this(id, name, description, status, epicId, null, null);
    }

    public Subtask(Integer id, String name, String description, Status status, Integer epicId, Duration duration,
                   LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public Integer getEpicId() {

        return epicId;
    }

    public void setEpicId(Integer epicId) {

        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", duration=" + (getDuration() != null ? getDuration().toMinutes() + "m" : "null") +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                ", epicId=" + epicId +
                '}';
    }
}