package main;

import manager.Managers;
import manager.TaskManager;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!\n");

        try {
            File file = File.createTempFile("tasks", ".csv");

            TaskManager managerFileBacked = Managers.getDefaultFileBackedManager(file);

            // 1. Создание тестовых задач
            System.out.println("=== Создание задач ===");

            Task task1 = new Task(null, "Помыть посуду", "Помыть посуду после ужина", Status.NEW);
            task1.setStartTime(LocalDateTime.of(2023, 6, 15, 18, 0));
            task1.setDuration(Duration.ofMinutes(30));
            managerFileBacked.createTask(task1);
            System.out.println("Создана задача: " + task1);

            Task task2 = new Task(null, "Убраться дома", "Сделать уборку в квартире", Status.NEW);
            task2.setStartTime(LocalDateTime.of(2023, 6, 15, 19, 0));
            task2.setDuration(Duration.ofHours(2));
            managerFileBacked.createTask(task2);
            System.out.println("Создана задача: " + task2 + "\n");

            // 2. Создание Эпиков
            System.out.println("=== Создание эпиков ===");

            Epic epic1 = new Epic(null, "Ремонт в квартире", "Полный ремонт во всех комнатах");
            managerFileBacked.createEpic(epic1);
            System.out.println("Создан эпик: " + epic1);

            Subtask subtask1 = new Subtask(null, "Купить материалы", "Купить стройматериалы", Status.NEW, epic1.getId());
            subtask1.setStartTime(LocalDateTime.of(2023, 6, 16, 10, 0));
            subtask1.setDuration(Duration.ofHours(3));
            managerFileBacked.createSubtask(subtask1);
            System.out.println("Создана подзадача: " + subtask1);

            Subtask subtask2 = new Subtask(null, "Сделать дизайн", "Разработать дизайн-проект", Status.NEW, epic1.getId());
            subtask2.setStartTime(LocalDateTime.of(2023, 6, 16, 14, 0));
            subtask2.setDuration(Duration.ofHours(4));
            managerFileBacked.createSubtask(subtask2);
            System.out.println("Создана подзадача: " + subtask2 + "\n");

            // 3. Проверка статусов
            System.out.println("=== Проверка статусов ===");
            System.out.println("Статус эпика: " + epic1.getStatus());

            subtask1.setStatus(Status.IN_PROGRESS);
            managerFileBacked.updateSubtask(subtask1);
            System.out.println("После изменения статуса подзадачи: " + epic1.getStatus() + "\n");

            // 4. Приоритетные задачи
            System.out.println("=== Приоритетные задачи ===");
            managerFileBacked.getPrioritizedTasks().forEach(task ->
                    System.out.printf("%s: %s (%s - %s)\n",
                            task.getType(),
                            task.getName(),
                            task.getStartTime(),
                            task.getEndTime())
            );
            System.out.println();

             // 5. Проверка пересечений
            System.out.println("=== Проверка пересечений ===");
            Task overlappingTask = new Task(null, "Неправильная задача", "Должна пересекаться",
                    Status.NEW);
            overlappingTask.setStartTime(LocalDateTime.of(2023, 6, 15, 18, 30));
            overlappingTask.setDuration(Duration.ofHours(1));
            try {
                managerFileBacked.createTask(overlappingTask);
            } catch (Exception e) {
                System.out.println("Ошибка при создании: " + e.getMessage());
            }
            System.out.println();

            // 6. История просмотров
            System.out.println("=== История просмотров ===");
            managerFileBacked.getTask(task1.getId());
            managerFileBacked.getEpic(epic1.getId());
            managerFileBacked.getSubtask(subtask1.getId());

            System.out.println("История:");
            managerFileBacked.getHistory().forEach(task ->
                    System.out.println("- " + task.getName() + " (" + task.getType() + ")")
            );
            System.out.println();

            // 7. Удаление задач
            System.out.println("=== Удаление задачи ===");
            managerFileBacked.deleteTask(task1.getId());
            System.out.println("Задачи после удаления:");
            managerFileBacked.findAllTasks().forEach(t -> System.out.println("- " + t.getName()));
            System.out.println("История после удаления:");
            managerFileBacked.getHistory().forEach(task ->
                    System.out.println("- " + task.getName())
            );

            System.out.println();
            System.out.println("=== Информация о сохраненном файле ===");
            if (file.exists()) {
                System.out.println("Файл создан: " + file.getAbsolutePath());
                System.out.println("Размер файла: " + file.length() + " байт");
            } else {
                System.out.println("Файл не создан!");
            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}