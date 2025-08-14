package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseCrudHandler<Epic> {

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson, Epic.class);
    }

    @Override
    protected void handleGetAll(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.findAllEpics());
        sendText(exchange, response, OK);
    }

    @Override
    protected void handleGetById(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = parseId(idStr);
            Epic epic = taskManager.getEpic(id);
            if (epic == null) {
                sendNotFound(exchange);
            } else {
                String response = gson.toJson(epic);
                sendText(exchange, response, OK);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = parseId(idStr);
            Epic epic = taskManager.getEpic(id);
            if (epic == null) {
                sendNotFound(exchange);
            } else {
                List<Subtask> subtasks = taskManager.getSubtasksOfEpic(id);
                String response = gson.toJson(subtasks);
                sendText(exchange, response, OK);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        try {
            Epic epic = parseTask(exchange);
            if (epic == null) {
                sendNotFound(exchange);
                return;
            }
            Epic createdEpic = taskManager.createEpic(epic);
            String response = gson.toJson(createdEpic);
            sendText(exchange, response, CREATED);
        } catch (JsonSyntaxException exception) {
            sendNotFound(exchange);
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = parseId(idStr);
            Epic epic = taskManager.deleteEpic(id);
            if (epic == null) {
                sendNotFound(exchange);
            } else {
                sendText(exchange, "Эпик успешно удален", OK);
            }
        } catch (NumberFormatException exception) {
            sendNotFound(exchange);
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            if (pathParts.length == 4 && "subtasks".equals(pathParts[3]) && "GET".equals(method)) {
                handleGetEpicSubtasks(exchange, pathParts[2]);
                return;
            }

            super.handle(exchange);
        } catch (Exception exception) {
            sendInternalError(exchange);
        }
    }
}