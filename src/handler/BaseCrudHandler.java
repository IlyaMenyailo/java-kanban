package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

public abstract class BaseCrudHandler<T> extends BaseHttpHandler implements HttpHandler {
    private final Class<T> taskClass;

    public BaseCrudHandler(TaskManager taskManager, Gson gson, Class<T> taskClass) {
        super(taskManager, gson);
        this.taskClass = taskClass;
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
                        handleGetAll(exchange);
                    } else if (pathParts.length == 3) {
                        handleGetById(exchange, pathParts[2]);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        handleDelete(exchange, pathParts[2]);
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

    protected abstract void handleGetAll(HttpExchange exchange) throws IOException;
    protected abstract void handleGetById(HttpExchange exchange, String idStr) throws IOException;
    protected abstract void handlePost(HttpExchange exchange) throws IOException;
    protected abstract void handleDelete(HttpExchange exchange, String idStr) throws IOException;

    protected T parseTask(HttpExchange exchange) throws IOException {
        return readRequest(exchange, taskClass);
    }

    protected int parseId(String idStr) throws NumberFormatException {
        return Integer.parseInt(idStr);
    }
}
