package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exeption.ManagerSaveException;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    public TasksHandler(TaskManager taskManager, Gson gson) {
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
                        handleGetAllTasks(exchange);
                    } else if (pathParts.length == 3) {
                        handleGetTaskById(exchange, pathParts[2]);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "POST":
                    handlePostTask(exchange);
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        handleDeleteTask(exchange, pathParts[2]);
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

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.findAllTasks());
        sendText(exchange, response, OK);
    }

    private void handleGetTaskById(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
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

    private void handlePostTask(HttpExchange exchange) throws IOException {
        try {
            Task task = readRequest(exchange, Task.class);
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

    private void handleDeleteTask(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
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