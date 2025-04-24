package main;

import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!\n");

        TaskManager manager = Managers.getDefault();

        //ЗАДАЧИ
        Task task1 = new Task(null, "Task 1", "Description of task 1", Status.NEW);
        manager.createTask(task1);
        Task task2 = new Task(null, "Task 2", "Description of task 2", Status.NEW);
        manager.createTask(task2);
        Task task3 = new Task(null, "Task 3", "Description of task 3", Status.NEW);
        manager.createTask(task3);
        Task task4 = new Task(null, "Task 4", "Description of task 4", Status.NEW);
        manager.createTask(task4);

        Task createdTask1 = manager.getTask(task1.getId());
        Task createdTask2 = manager.getTask(task2.getId());
        Task createdTask3 = manager.getTask(task3.getId());
        Task createdTask4 = manager.getTask(task4.getId());

        //ЭПИКИ
        Epic epic1 = new Epic(null, "Epic 1", "Description of epic 1");
        manager.createEpic(epic1);
        Epic epic2 = new Epic(null, "Epic 2", "Description of epic 2");
        manager.createEpic(epic2);
        Epic epic3 = new Epic(null, "Epic 3", "Description of epic 3");
        manager.createEpic(epic3);
        Epic epicWithoutSubtasks = new Epic(null, "Epic epicWithoutSubtasks",
                "Description of epic epicWithoutSubtasks");
        manager.createEpic(epicWithoutSubtasks);

        Epic createdEpic1 = manager.getEpic(epic1.getId());
        Epic createdEpic2 = manager.getEpic(epic2.getId());
        Epic createdEpic3 = manager.getEpic(epic3.getId());
        Epic createdEpicWithoutSubtasks = manager.getEpic(epicWithoutSubtasks.getId());

        //САБТАСКИ
        Subtask subtask1 = new Subtask(null, "Subtask 1", "Description of subtask 1", Status.NEW,
                createdEpic1.getId());
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(null, "Subtask 2", "Description of subtask 2", Status.NEW,
                createdEpic1.getId());
        manager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask(null, "Subtask 3", "Description of subtask 3", Status.NEW,
                createdEpic2.getId());
        manager.createSubtask(subtask3);
        Subtask subtask4 = new Subtask(null, "Subtask 4", "Description of subtask 4", Status.NEW,
                createdEpic3.getId());
        manager.createSubtask(subtask4);
        Subtask subtask5 = new Subtask(null, "Subtask 5", "Description of subtask 5", Status.NEW,
                createdEpic3.getId());
        manager.createSubtask(subtask5);
        Subtask subtask6 = new Subtask(null, "Subtask 6", "Description of subtask 6", Status.NEW,
                createdEpic3.getId());
        manager.createSubtask(subtask6);

        System.out.println("История вариант 1:");
        System.out.println(manager.getHistory());

        Subtask createdSubtask1 = manager.getSubtask(subtask1.getId());
        Subtask createdSubtask2 = manager.getSubtask(subtask2.getId());
        Subtask createdSubtask3 = manager.getSubtask(subtask3.getId());
        Subtask createdSubtask4 = manager.getSubtask(subtask4.getId());
        Subtask createdSubtask5 = manager.getSubtask(subtask5.getId());
        Subtask createdSubtask6 = manager.getSubtask(subtask6.getId());
        Task createdTask5 = manager.getTask(task1.getId());
        Task createdTask6 = manager.getTask(task2.getId());
        Task createdTask7 = manager.getTask(task3.getId());

        Task createdTask1Second = manager.getTask(task1.getId());
        Task createdTask2Second = manager.getTask(task2.getId());
        Task createdTask3Second = manager.getTask(task3.getId());
        Task createdTask4Second = manager.getTask(task4.getId());

        System.out.println("История вариант 2:");
        System.out.println(manager.getHistory());

        manager.deleteTask(task1.getId());
        manager.deleteTask(task2.getId());

        System.out.println("История вариант 3:");
        System.out.println(manager.getHistory());

        manager.deleteEpic(epicWithoutSubtasks.getId());
        System.out.println("История вариант 4:");
        System.out.println(manager.getHistory());

        manager.deleteAllTasks();
        manager.deleteAllSubtask();
        System.out.println("История вариант 5:");
        System.out.println(manager.getHistory());

        manager.deleteAllEpics();
        System.out.println("История вариант 6:");
        System.out.println(manager.getHistory());

        try {
            File file = File.createTempFile("tasks", ".csv");

            FileBackedTaskManager manager1 = new FileBackedTaskManager(file);

            Task task21 = new Task(null, "Task 21", "Description 21", Status.NEW);
            manager1.createTask(task21);

            Epic epic21 = new Epic(null, "Epic 21", "Description Epic 21");
            manager1.createEpic(epic21);

            Subtask subtask21 = new Subtask(null, "Subtask 21", "Description Subtask 21", Status.NEW,epic21.getId());
            manager1.createSubtask(subtask21);

            System.out.println("Tasks in manager1:");
            System.out.println(manager1.findAllTasks());
            System.out.println("Epics in manager1:");
            System.out.println(manager1.findAllEpics());
            System.out.println("Subtasks in manager1:");
            System.out.println(manager1.findAllSubtasks());

            FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);

            System.out.println("\nTasks in manager2:");
            System.out.println(manager2.findAllTasks());
            System.out.println("Epics in manager2:");
            System.out.println(manager2.findAllEpics());
            System.out.println("Subtasks in manager2:");
            System.out.println(manager2.findAllSubtasks());

            if (file.exists()) {
                System.out.println("Файл создан: " + file.getAbsolutePath());
                System.out.println("Размер файла: " + file.length() + " байт");
            } else {
                System.out.println("Файл не создан!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}