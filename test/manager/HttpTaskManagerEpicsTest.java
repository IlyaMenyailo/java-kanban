package manager;

import com.google.gson.Gson;
import handler.HttpTaskServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

class HttpTaskManagerEpicsTest {
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
    void testCreateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(null, "Test Epic", "Description");

        String epicJson = gson.toJson(epic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Epic> epics = taskManager.findAllEpics();
        Assertions.assertEquals(1, epics.size());
        Assertions.assertEquals("Test Epic", epics.get(0).getName());
    }

    @Test
    void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic(null, "Test Epic", "Description");
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(response.body().contains("Test Epic"));
    }

    @Test
    void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic(null, "Epic", "Desc");
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("[]", response.body());
    }

    @Test
    void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(null, "To Delete", "Desc");
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertNull(taskManager.getEpic(epicId));
    }

    @Test
    void testUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(null, "Original", "Desc");
        taskManager.createEpic(epic);
        epic.setName("Updated");
        epic.setDescription("New Description");

        String epicJson = gson.toJson(epic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        Epic updatedEpic = taskManager.getEpic(epic.getId());
        Assertions.assertEquals("Updated", updatedEpic.getName());
        Assertions.assertEquals("New Description", updatedEpic.getDescription());
    }
}