package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exeption.ManagerSaveException;
import manager.TaskManager;
import tasks.Subtask;

import java.io.IOException;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
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
                        handleGetAllSubtasks(exchange);
                    } else if (pathParts.length == 3) {
                        handleGetSubtaskById(exchange, pathParts[2]);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "POST":
                    handlePostSubtask(exchange);
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        handleDeleteSubtask(exchange, pathParts[2]);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.findAllSubtasks());
        sendText(exchange, response, OK);
    }

    private void handleGetSubtaskById(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
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

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        try {
            Subtask subtask = readRequest(exchange, Subtask.class);
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

    private void handleDeleteSubtask(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
            taskManager.deleteSubtask(id);
            sendText(exchange, "Подзадача успешно удалена", OK);
        } catch (NumberFormatException exception) {
            sendNotFound(exchange);
        }
    }
}