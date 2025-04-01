package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping("/user/{UserId}")
    public ResponseEntity createTask(@PathVariable Integer UserId, @RequestBody Task task) throws Exception {
        Task newTask = taskService.createTask(UserId, task);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location",""+newTask.getId());
        responseHeaders.set("Access-Control-Expose-Headers","location");
        return ResponseEntity.ok()
            .headers(responseHeaders).build();
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
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
        try {
            String status = requestBody.get("status");
            taskService.putTaskStatus(id, status);
            return ResponseEntity.ok("Task status updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update task status.");
        }
    }
}
