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
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

class HttpTaskManagerHistoryAndPrioritizedTest {
    private HttpTaskServer server;
    private TaskManager taskManager;
    private Gson gson;
    private final HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        server = new HttpTaskServer(taskManager);
        gson = HttpTaskServer.getGson();
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void testGetHistory() throws IOException, InterruptedException {
        Task task = new Task(null, "Task", "Desc", Status.NEW);
        taskManager.createTask(task);
        taskManager.getTask(task.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(response.body().contains("Task"));
    }

    @Test
    void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task = new Task(null, "Task", "Desc", Status.NEW);
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(30));
        taskManager.createTask(task);

        Epic epic = new Epic(null, "Epic", "Desc");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(null, "Subtask", "Desc", Status.NEW, epic.getId());
        subtask.setStartTime(LocalDateTime.now().plusHours(1));
        subtask.setDuration(Duration.ofMinutes(15));
        taskManager.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(response.body().contains("Task"));
        Assertions.assertTrue(response.body().contains("Subtask"));
        Assertions.assertFalse(response.body().contains("Epic"));
    }
}