package com.springboot.MyTodoList;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.springboot.MyTodoList.controller.ManagerController;
import com.springboot.MyTodoList.model.Manager;
import com.springboot.MyTodoList.service.ManagerService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ManagerController.class)
class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerService managerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateManager() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        Manager inputManager = new Manager();
        inputManager.setName("Test Manager");
        inputManager.setRole("admin");
        inputManager.setPassword("123");
        inputManager.setEmail("roberto@gmail.com");
        inputManager.setCreationTs(now);
        inputManager.setUsers(null);
        inputManager.setProjects(null);

        Manager savedManager = new Manager();
        savedManager.setId(1);

        Mockito.when(managerService.createManager(any(Manager.class)))
                .thenReturn(savedManager);

        mockMvc.perform(post("/api/managers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputManager)))
                .andExpect(status().isOk())
                .andExpect(header().string("location", "1"))
                .andExpect(header().string("Access-Control-Expose-Headers", "location"));
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

    @Test
    void testGetManagerById() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        Manager manager = new Manager();
        manager.setId(1);
        manager.setName("Manager 1");
        manager.setEmail("manager1@tec.com");
        manager.setRole("admin");
        manager.setCreationTs(now);
        manager.setUsers(null);
        manager.setProjects(null);

        Mockito.when(managerService.getManagerById(1)).thenReturn(java.util.Optional.of(manager));

        mockMvc.perform(get("/api/managers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Manager 1"))
                .andExpect(jsonPath("$.email").value("manager1@tec.com"))
                .andExpect(jsonPath("$.role").value("admin"))
                .andExpect(jsonPath("$.creationTs").value(manager.getCreationTs().toString()))
                .andExpect(jsonPath("$.users").doesNotExist())
                .andExpect(jsonPath("$.projects").doesNotExist());
    }

    @Test
    void testGetManagerIdByEmail() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        Manager manager = new Manager();
        manager.setId(1);
        manager.setName("Manager 1");
        manager.setEmail("manager1@tec.com");
        manager.setRole("admin");
        manager.setCreationTs(now);
        manager.setUsers(null);
        manager.setProjects(null);

        Mockito.when(managerService.getManagerIdByEmail("manager1@tec.com")).thenReturn(1);  
 
        mockMvc.perform(get("/api/managers/manager1@tec.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Manager 1"))
                .andExpect(jsonPath("$.email").value("manager1@tec.com"))
                .andExpect(jsonPath("$.role").value("admin"))
                .andExpect(jsonPath("$.creationTs").value(manager.getCreationTs().toString()))
                .andExpect(jsonPath("$.users").doesNotExist())
                .andExpect(jsonPath("$.projects").doesNotExist());
    }
}
