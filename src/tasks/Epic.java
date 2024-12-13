package tasks;

import status.Status;
import java.util.ArrayList;

public class Epic extends Task {

    public ArrayList<Integer> subtasksId;

    public Epic(Integer id, String name, String description, Status status) {
        super(id, name, description, status);
        this.subtasksId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void addSubtask(Integer subtaskId) {
        subtasksId.add(subtaskId);
    }

    public void removeSubtask(Integer subtaskId) {
        subtasksId.remove(subtaskId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                super.toString() +
                '}';
    }
}