package com.springboot.MyTodoList;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.springboot.MyTodoList.controller.ManagerController;
import com.springboot.MyTodoList.controller.TaskController;
import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.service.ManagerService;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.service.TaskService;
import com.springboot.MyTodoList.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest{

    @Autowired
    private MockMvc mockMvc;

   @MockBean
    private UserService userService;

    @MockBean
    private SprintService sprintService; // Mock SprintService to resolve the dependency issue

    @MockBean
    private ManagerService managerService; // Mock ManagerService to resolve the dependency issue

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    Task createMockTask() {
        // Create a mock task object with sample data
        Task task = new Task();

        Sprint sprint = new Sprint();
        sprint.setId(1);

        task.setId(1);
        task.setName("Reconocimiento del usuario como admin");
        task.setDescription("Identificar usuario que interactúa con el bot.");
        task.setStatus("Not-started");
        task.setStoryPoints(1);
        task.setEstimatedHours(2);
        task.setRealHours(0);
        task.setSprint(sprint);
        return task;
    }

    @Test 
    void testCreateTask() throws Exception {
    
        Task mockTask = createMockTask();

        Mockito.when(taskService.createTask(eq(1), any(Task.class))).thenReturn(mockTask);
        mockMvc.perform(post("/api/tasks/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockTask)))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Reconocimiento del usuario como admin"))
                .andExpect(jsonPath("$.description").value("Identificar usuario que interactúa con el bot."))
                .andExpect(jsonPath("$.status").value("Not-started"))
                .andExpect(jsonPath("$.storyPoints").value(1))
                .andExpect(jsonPath("$.estimatedHours").value(2))
                .andExpect(jsonPath("$.realHours").value(0))
                .andExpect(jsonPath("$.assignments").isArray())
                .andExpect(jsonPath("$.assignments").isEmpty());

        // Bad request due to empty name
        mockTask.setName("");
        Mockito.when(taskService.createTask(eq(1), any(Task.class))).thenThrow(new IllegalArgumentException("Task name cannot be empty."));
        mockMvc.perform(post("/api/tasks/user/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test 
    void testGetAllTasks() throws Exception {
        List<Task> tasks = new ArrayList<>();
        Task task1 = createMockTask();
        tasks.add(task1);
        
        Task task2 = createMockTask();
        task2.setId(2);
        task2.setName("Task 2");
        tasks.add(task2);

        Mockito.when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Reconocimiento del usuario como admin")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Task 2")));

        // No content case
        Mockito.when(taskService.getAllTasks()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetTaskById() throws Exception {
        Task task = new Task();
        task.setId(1);
        task.setName("Task 1");

        Mockito.when(taskService.getTaskById(1)).thenReturn(java.util.Optional.of(task));

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Task 1")));
    }
    @Test 
    void testGetTasksByUser() throws Exception {
        List<Task> tasks = new ArrayList<>();
        Task task1 = new Task();
        task1.setId(1);
        task1.setName("Task 1");
        tasks.add(task1);

        Task task2 = new Task();
        task2.setId(2);
        task2.setName("Task 2");
        tasks.add(task2);

        Mockito.when(taskService.getTasksByUserId(1)).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Task 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Task 2")));
    }

    @Test 
    void testGetTasksBySprintId() throws Exception {
        List<Task> tasks = new ArrayList<>();
        Task task1 = new Task();
        task1.setId(1);
        task1.setName("Task 1");
        tasks.add(task1);

        Task task2 = new Task();
        task2.setId(2);
        task2.setName("Task 2");
        tasks.add(task2);

        Mockito.when(taskService.getTasksBySprintId(1)).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks/sprint/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Task 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Task 2")));
    }
    @Test 
    void testAssignTaskToSprint() throws Exception {
        Mockito.doNothing().when(taskService).assignTaskToSprint(1, 1);

        mockMvc.perform(put("/api/tasks/1/assign-sprint/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Task assigned to sprint successfully."));
    }
    @Test
    void testUpdateTaskStatus() throws Exception {
        Task mockTask = createMockTask();

        Task mockTaskUpdated = createMockTask();   
        mockTaskUpdated.setStatus("Started");

        Mockito.doNothing().when(taskService).putTaskStatus(1, "Started");
        
        mockMvc.perform(put("/api/tasks/1/update-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"Started\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Task status updated successfully."));

        // Bad request due to empty status
        mockTask.setStatus(null);

        mockMvc.perform(put("/api/tasks/1/update-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Status cannot be null or empty."));
    }
}