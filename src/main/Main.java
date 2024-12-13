package main;

import manager.TaskManager;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!\n");

        TaskManager manager = new TaskManager();

        Task task1 = new Task(null, "Task 1", "Description of task 1", Status.NEW);
        Task createdTask1 = manager.createTask(task1);
        Task task2 = new Task(null, "Task 2", "Description of task 2", Status.NEW);
        Task createdTask2 = manager.createTask(task2);

        Epic epic1 = new Epic(null, "Epic 1", "Description of epic 1", Status.NEW);
        manager.createEpic(epic1);
        Epic createdEpic1 = manager.getEpic(epic1.getId());
        Epic epic2 = new Epic(null, "Epic 2", "Description of epic 2", Status.NEW);
        manager.createEpic(epic2);
        Epic createdEpic2 = manager.getEpic(epic2.getId());

        Subtask subtask1 = new Subtask(null, "Subtask 1", "Description of subtask 1", Status.NEW,
                createdEpic1.getId());
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(null,"Subtask 2", "Description of subtask 2", Status.NEW,
                createdEpic1.getId());
        manager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask(null,"Subtask 3", "Description of subtask 3", Status.NEW,
                createdEpic2.getId());
        manager.createSubtask(subtask3);

        System.out.println("*** Test for printing Tasks, Epics, Subtasks ***");
        System.out.println("All Tasks: " + manager.findAllTasks());
        System.out.println("All Epics: " + manager.findAllEpics());
        System.out.println("All Subtasks: " + manager.findAllSubtasks());
        System.out.println();

        System.out.println("Task 1. Name: " + createdTask1.getName() + ". Description: " + createdTask1.getDescription()
                + ". Status: " + createdTask1.getStatus());
        System.out.println("Task 2. Name: " + createdTask2.getName() + ". Description: " + createdTask2.getDescription()
                + ". Status: " + createdTask2.getStatus());
        System.out.println("Epic 1. Name: " + createdEpic1.getName() + ". Description: " + createdEpic1.getDescription()
                + ". Status: " + createdEpic1.getStatus());
        System.out.println("Epic 2. Name: " + createdEpic2.getName() + ". Description: " + createdEpic2.getDescription()
                + ". Status: " + createdEpic2.getStatus());
        System.out.println("Subtask 1. Name: " + subtask1.getName() + ". Description: " +
                subtask1.getDescription() + ". Status: " + subtask1.getStatus());
        System.out.println("Subtask 2. Name: " + subtask2.getName() + ". Description: " +
                subtask2.getDescription() + ". Status: " + subtask2.getStatus());
        System.out.println("Subtask 3. Name: " + subtask3.getName() + ". Description: " +
                subtask3.getDescription() + ". Status: " + subtask3.getStatus());
        System.out.println();

        ArrayList<Subtask> subtasksInEpic = manager.getSubtasksOfEpic(epic1.getId());
        System.out.println("*** Test of finding all Subtasks in Epic ***");
        System.out.println("Epic name: " + epic1.getName() + ". Subtasks: " + subtasksInEpic);
        System.out.println();

        Task updatedTask = new Task(createdTask1.getId(), "Task 1", "Another description of Task 1",
                Status.IN_PROGRESS);
        createdTask1 = manager.updateTask(updatedTask);
        Epic updatedEpic1 = new Epic(epic1.getId(), "Epic 1", "Another description of Epic 1",
                epic1.getStatus());
        manager.updateEpic(updatedEpic1);
        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);
        subtask2.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask2);

        System.out.println("*** Test of updating Tasks, Epics, Subtasks ***");
        System.out.println("Task 1. Name: " + createdTask1.getName() + ". Description: " + createdTask1.getDescription()
                + ". Status: " + createdTask1.getStatus());
        System.out.println("Epic 1. Name: " + createdEpic1.getName() + ". Description: " + createdEpic1.getDescription()
                + ". Status: " + createdEpic1.getStatus());
        System.out.println("Epic 2. Name: " + createdEpic2.getName() + ". Description: " + createdEpic2.getDescription()
                + ". Status: " + epic2.getStatus());
        System.out.println("Subtask 1. Name: " + subtask1.getName() + ". Description: " +
                subtask1.getDescription() + ". Status: " + subtask1.getStatus());
        System.out.println("Subtask 2. Name: " + subtask2.getName() + ". Description: " +
                subtask2.getDescription() + ". Status: " + subtask2.getStatus());
        System.out.println();

        System.out.println("*** Test for finding Tasks, Epics, Subtasks ***");
        Task findTaskById = manager.findTaskById(task1.getId());
        Epic findEpicById = manager.findEpicById(epic1.getId());
        Subtask findSubtaskById = manager.findSubtaskById(subtask1.getId());
        System.out.println(findTaskById);
        System.out.println(findEpicById);
        System.out.println(findSubtaskById);
        System.out.println();

        manager.deleteTask(task1.getId());
        manager.deleteEpic(epic2.getId());
        manager.deleteSubtask(subtask1.getId());
        System.out.println("*** Test of deleting Tasks, Epics, Subtasks ***");
        System.out.println("All Tasks after deletion " + manager.findAllTasks());
        System.out.println("All Epics after deletion " + manager.findAllEpics());
        System.out.println("All Subtasks after deletion " + manager.findAllSubtasks());
        System.out.println();

        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtask();
        System.out.println("*** Test of deleting ALL Tasks, Epics, Subtasks ***");
        System.out.println("All Tasks: " + manager.findAllTasks());
        System.out.println("All Epics: " + manager.findAllEpics());
        System.out.println("All Subtasks: " + manager.findAllSubtasks());
    }
}