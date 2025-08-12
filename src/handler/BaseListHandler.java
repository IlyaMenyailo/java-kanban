package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

public abstract class BaseListHandler extends BaseHttpHandler implements HttpHandler {

    public BaseListHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                handleGet(exchange);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception exception) {
            sendInternalError(exchange);
        }
    }

    protected abstract void handleGet(HttpExchange exchange) throws IOException;
}