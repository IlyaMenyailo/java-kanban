package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exeption.ManagerSaveException;
import manager.TaskManager;
import tasks.Subtask;

import java.io.IOException;

public class SubtasksHandler extends BaseCrudHandler<Subtask> {

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson, Subtask.class);
    }

    @Override
    protected void handleGetAll(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.findAllSubtasks());
        sendText(exchange, response, OK);
    }

    @Override
    protected void handleGetById(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = parseId(idStr);
            Subtask subtask = taskManager.getSubtask(id);
            if (subtask == null) {
                sendNotFound(exchange);
            } else {
                String response = gson.toJson(subtask);
                sendText(exchange, response, OK);
            }
        } catch (NumberFormatException exception) {
            sendNotFound(exchange);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        try {
            Subtask subtask = parseTask(exchange);
            if (subtask.getId() == null) {
                taskManager.createSubtask(subtask);
            } else {
                taskManager.updateSubtask(subtask);
            }
            sendText(exchange, "Подзадача успешно добавлена/обновлена", CREATED);
        } catch (JsonSyntaxException exception) {
            sendNotFound(exchange);
        } catch (ManagerSaveException exception) {
            sendHasInteractions(exchange);
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = parseId(idStr);
            taskManager.deleteSubtask(id);
            sendText(exchange, "Подзадача успешно удалена", OK);
        } catch (NumberFormatException exception) {
            sendNotFound(exchange);
        }
    }
}