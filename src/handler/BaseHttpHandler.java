package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected static final int OK = 200;
    protected static final int CREATED = 201;
    protected static final int NOT_FOUND = 404;
    protected static final int NOT_ACCEPTABLE = 406;
    protected static final int INTERNAL_SERVER_ERROR = 500;

    protected final Gson gson;
    protected final TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(response);
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String response = "Задача не найдена";
        sendText(exchange, response, NOT_FOUND);
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        String response = "Задача пересекается по времени с существующей";
        sendText(exchange, response, NOT_ACCEPTABLE);
    }

    protected void sendInternalError(HttpExchange exchange) throws IOException {
        String response = "Внутренняя ошибка сервера";
        sendText(exchange, response, INTERNAL_SERVER_ERROR);
    }

    protected <T> T readRequest(HttpExchange exchange, Class<T> classOfT) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("Received JSON: " + body);
            return gson.fromJson(body, classOfT);
        }
    }
}