package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exeption.ManagerSaveException;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;

public class TasksHandler extends BaseCrudHandler<Task> {

    public TasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson, Task.class);
    }

    @Override
    protected void handleGetAll(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.findAllTasks());
        sendText(exchange, response, OK);
    }

    @Override
    protected void handleGetById(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = parseId(idStr);
            Task task = taskManager.getTask(id);
            if (task == null) {
                sendNotFound(exchange);
            } else {
                String response = gson.toJson(task);
                sendText(exchange, response, OK);
            }
        } catch (NumberFormatException exception) {
            sendNotFound(exchange);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        try {
            Task task = parseTask(exchange);
            if (task.getId() == null) {
                taskManager.createTask(task);
            } else {
                taskManager.updateTask(task);
            }
            sendText(exchange, "Задача успешно добавлена/обновлена", CREATED);
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
            Task task = taskManager.deleteTask(id);
            if (task == null) {
                sendNotFound(exchange);
            } else {
                sendText(exchange, "Задача успешно удалена", OK);
            }
        } catch (NumberFormatException exception) {
            sendNotFound(exchange);
        }
    }
}