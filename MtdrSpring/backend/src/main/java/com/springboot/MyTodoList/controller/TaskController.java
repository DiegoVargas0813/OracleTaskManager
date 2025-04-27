package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.net.URI;

import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    @PostMapping("/user/{UserId}")
    public ResponseEntity<Task> createTask(@PathVariable Integer UserId, @RequestBody Task task) throws Exception {
        if (task.getName() == null || task.getName().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try{
            Task newTask = taskService.createTask(UserId, task);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location",""+newTask.getId());
            responseHeaders.set("Access-Control-Expose-Headers","location");
            return ResponseEntity.created(URI.create("/api/tasks/" + newTask.getId()))
                    .headers(responseHeaders)
                    .body(newTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error creating task: ", e);
            return ResponseEntity.internalServerError().build();
        }


    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        if (tasks == null || tasks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Integer id) {
        Optional<Task> task = taskService.getTaskById(id);
        return task.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<Task> getTasksByUser(@PathVariable int userId) {
        return taskService.getTasksByUserId(userId);
    }

    @GetMapping("/sprint/{sprintId}")
    public List<Task> getTasksBySprintId(@PathVariable int sprintId) {
        return taskService.getTasksBySprintId(sprintId);
    }

    @PutMapping("/{id}/assign-sprint/{sprintId}")
    public ResponseEntity<String> assignTaskToSprint(@PathVariable int id, @PathVariable int sprintId) {
        try {
            taskService.assignTaskToSprint(id, sprintId);
            return ResponseEntity.ok("Task assigned to sprint successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to assign task to sprint.");
        }
    }

    @PutMapping("/{id}/update-status")
    public ResponseEntity<String> updateTaskStatus(@PathVariable int id, @RequestBody Map<String, String> requestBody) {
        String status = requestBody.get("status");

        if (status == null || status.isEmpty()) {
            return ResponseEntity.badRequest().body("Status cannot be null or empty.");
        }

        try {
            taskService.putTaskStatus(id, status);
            return ResponseEntity.ok("Task status updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update task status.");
        }
    }
}
