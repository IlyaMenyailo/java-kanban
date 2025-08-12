package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseListHandler {

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getHistory());
        sendText(exchange, response, OK);
    }
}