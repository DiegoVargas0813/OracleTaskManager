//Comment to test pull

package com.springboot.MyTodoList;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.springboot.MyTodoList.controller.ManagerController;
import com.springboot.MyTodoList.model.Manager;
import com.springboot.MyTodoList.service.ManagerService;
import com.springboot.MyTodoList.service.ProjectService;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.service.TaskService;
import com.springboot.MyTodoList.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

@WebMvcTest(ManagerController.class)
class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private UserService userService;

    @MockBean
    private SprintService sprintService; // Mock SprintService to resolve the dependency issue

    @MockBean
    private ManagerService managerService; // Mock ManagerService to resolve the dependency issue

    @MockBean
    private TaskService taskService; // Mock TaskService to resolve the dependency issue


    @Autowired
    private ObjectMapper objectMapper;

    Manager createMockManager() {
        Manager mockManager = new Manager();
        mockManager.setId(1);
        mockManager.setName("Julieta Carolina Arteaga Legorreta");
        mockManager.setRole("Manager");
        mockManager.setEmail("A01637444@tec.mx");
        mockManager.setPassword("somepassword");
        mockManager.setCreationTs(OffsetDateTime.now());
        mockManager.setUsers(null);
        mockManager.setProjects(null);
        return mockManager;
    }

    @Test
    void testCreateManager() throws Exception {
        Manager mockManager = createMockManager();

        Mockito.when(managerService.createManager(any(Manager.class))).thenReturn(mockManager);

        mockMvc.perform(post("/api/managers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockManager)))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "1"))
                .andExpect(header().string("Access-Control-Expose-Headers", "location"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Julieta Carolina Arteaga Legorreta"))
                .andExpect(jsonPath("$.role").value("Manager"))
                .andExpect(jsonPath("$.email").value("A01637444@tec.mx"))
                .andExpect(jsonPath("$.password").value("somepassword"))
                .andExpect(jsonPath("$.creationTs").exists());
    }

    @Test
    void testGetManagerById() throws Exception {
        // Mock a Manager object    
        Manager mockManager = createMockManager();
    
        Mockito.when(managerService.getManagerById(1)).thenReturn(java.util.Optional.of(mockManager));
    
        // Successful retrieval of manager by ID
        mockMvc.perform(get("/api/managers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Julieta Carolina Arteaga Legorreta"))
                .andExpect(jsonPath("$.role").value("Manager"))
                .andExpect(jsonPath("$.email").value("A01637444@tec.mx"))
                .andExpect(jsonPath("$.password").value("somepassword"))
                .andExpect(jsonPath("$.creationTs").exists());

        // Unsuccessful retrieval of manager by ID (not found)
        mockMvc.perform(get("/api/managers/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetManagerIdByEmail() throws Exception {
        Manager mockManager = createMockManager();
    
        // Mock the service to return the Manager when the email is found
        Mockito.when(managerService.getManagerIdByEmail("A01637444@tec.mx")).thenReturn(Optional.of(mockManager));
    
        // Perform the POST request and validate the response
        mockMvc.perform(post("/api/managers/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"A01637444@tec.mx\"}")) // Pass the email in the request body
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Julieta Carolina Arteaga Legorreta"))
                .andExpect(jsonPath("$.role").value("Manager"))
                .andExpect(jsonPath("$.email").value("A01637444@tec.mx"))
                .andExpect(jsonPath("$.password").value("somepassword"))
                .andExpect(jsonPath("$.creationTs").exists());
    }

    @Test
    void testGetAllManagers() throws Exception {
        Manager manager1 = new Manager();
        manager1.setId(1);
        manager1.setName("Manager 1");
        manager1.setEmail("manager1@example.com");
        manager1.setRole("admin");

        Manager manager2 = new Manager();
        manager2.setId(2);
        manager2.setName("Manager 2");
        manager2.setEmail("manager2@example.com");
        manager2.setRole("user");

        List<Manager> managers = new ArrayList<>();
        managers.add(manager1);
        managers.add(manager2);

        Mockito.when(managerService.getAllManagers()).thenReturn(managers);

        mockMvc.perform(get("/api/managers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Manager 1"))
                .andExpect(jsonPath("$[1].name").value("Manager 2"));
    }
}
