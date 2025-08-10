package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            switch (method) {
                case "GET":
                    if (pathParts.length == 2) {
                        handleGetAllEpics(exchange);
                    } else if (pathParts.length == 3) {
                        handleGetEpicById(exchange, pathParts[2]);
                    } else if (pathParts.length == 4 && "subtasks".equals(pathParts[3])) {
                        handleGetEpicSubtasks(exchange, pathParts[2]);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "POST":
                    handlePostEpic(exchange);
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        handleDeleteEpic(exchange, pathParts[2]);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception exception) {
            sendInternalError(exchange);
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.findAllEpics());
        sendText(exchange, response, OK);
    }

    private void handleGetEpicById(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
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
            int id = Integer.parseInt(idStr);
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


    private void handlePostEpic(HttpExchange exchange) throws IOException {
        try {
            Epic epic = readRequest(exchange, Epic.class);
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

    private void handleDeleteEpic(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
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
}