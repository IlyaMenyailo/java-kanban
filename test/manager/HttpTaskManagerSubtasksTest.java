package manager;

import com.google.gson.Gson;
import handler.HttpTaskServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class HttpTaskManagerSubtasksTest {
    private HttpTaskServer server;
    private TaskManager taskManager;
    private Gson gson;
    private final HttpClient client = HttpClient.newHttpClient();
    private Epic epic;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        server = new HttpTaskServer(taskManager);
        gson = HttpTaskServer.getGson();
        server.start();

        epic = new Epic(null, "Test Epic", "Description");
        taskManager.createEpic(epic);
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void testCreateSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(null, "Test Subtask", "Description", Status.NEW, epic.getId());
        subtask.setStartTime(LocalDateTime.now());
        subtask.setDuration(Duration.ofMinutes(30));

        String subtaskJson = gson.toJson(subtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Subtask> subtasks = taskManager.findAllSubtasks();
        Assertions.assertEquals(1, subtasks.size());
        Assertions.assertEquals("Test Subtask", subtasks.get(0).getName());
    }

    @Test
    void testGetSubtaskById() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(null, "Test Subtask", "Description", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask);
        int subtaskId = subtask.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtaskId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(response.body().contains("Test Subtask"));
    }

    @Test
    void testUpdateSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(null, "Original", "Desc", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask);
        subtask.setName("Updated");
        subtask.setStatus(Status.DONE);

        String subtaskJson = gson.toJson(subtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        Subtask updatedSubtask = taskManager.getSubtask(subtask.getId());
        Assertions.assertEquals("Updated", updatedSubtask.getName());
        Assertions.assertEquals(Status.DONE, updatedSubtask.getStatus());
    }

    @Test
    void testDeleteSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(null, "To Delete", "Desc", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask);
        int subtaskId = subtask.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtaskId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertNull(taskManager.getSubtask(subtaskId));
    }
}