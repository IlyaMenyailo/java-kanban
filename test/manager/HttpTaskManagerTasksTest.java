package manager;

import com.google.gson.Gson;
import handler.HttpTaskServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class HttpTaskManagerTasksTest {
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
    void testCreateTask() throws IOException, InterruptedException {
        Task task = new Task(null, "Test Task", "Description", Status.NEW);
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(30));

        String taskJson = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Task> tasks = taskManager.findAllTasks();
        Assertions.assertEquals(1, tasks.size());
        Assertions.assertEquals("Test Task", tasks.get(0).getName());
    }

    @Test
    void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task(null, "Test Task", "Description", Status.NEW);
        taskManager.createTask(task);
        int taskId = task.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(response.body().contains("Test Task"));
    }

    @Test
    void testGetAllTasks() throws IOException, InterruptedException {
        taskManager.createTask(new Task(null, "Task 1", "Desc 1", Status.NEW));
        taskManager.createTask(new Task(null, "Task 2", "Desc 2", Status.IN_PROGRESS));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(response.body().contains("Task 1"));
        Assertions.assertTrue(response.body().contains("Task 2"));
    }

    @Test
    void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task(null, "Original", "Desc", Status.NEW);
        taskManager.createTask(task);
        task.setName("Updated");
        task.setStatus(Status.DONE);

        String taskJson = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        Task updatedTask = taskManager.getTask(task.getId());
        Assertions.assertEquals("Updated", updatedTask.getName());
        Assertions.assertEquals(Status.DONE, updatedTask.getStatus());
    }

    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task(null, "To Delete", "Desc", Status.NEW);
        taskManager.createTask(task);
        int taskId = task.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertNull(taskManager.getTask(taskId));
    }

    @Test
    void testTaskTimeOverlap() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task(null, "Task 1", "Desc", Status.NEW);
        task1.setStartTime(now);
        task1.setDuration(Duration.ofMinutes(30));
        taskManager.createTask(task1);

        Task task2 = new Task(null, "Task 2", "Desc", Status.NEW);
        task2.setStartTime(now.plusMinutes(15));
        task2.setDuration(Duration.ofMinutes(30));

        String taskJson = gson.toJson(task2);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response.statusCode());
    }
}