package com.springboot.MyTodoList;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.springboot.MyTodoList.controller.SprintController;
import com.springboot.MyTodoList.model.Manager;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.service.ManagerService;
import com.springboot.MyTodoList.service.SprintService;
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

@WebMvcTest(SprintController.class)
public class TaskItemBotControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SprintService sprintService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test 
    void testOnUpdateReceived() throws Exception {
        // Mocking the SprintService to return a list of sprints
        List<Sprint> sprints = new ArrayList<>();
        sprints.add(new Sprint());
        Mockito.when(sprintService.getAllSprints()).thenReturn(sprints);

        // Perform a GET request to the endpoint
        mockMvc.perform(get("/sprints")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is("Sprint 1")))
                .andExpect(jsonPath("$[0].status", is("In Progress")));
    }

    @Test
    void sendMessage() throws Exception {
        // Mocking the SprintService to return a sprint
        Sprint sprint = new Sprint();
        OffsetDateTime startDate = OffsetDateTime.now();
        sprint.setId(1);
        sprint.setName("Sprint 1");
        sprint.setstartDate(startDate);
        Mockito.when(sprintService.getSprintById(1)).thenReturn(sprint);

        // Perform a POST request to the endpoint
        mockMvc.perform(post("/sprints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sprint)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Sprint 1")))
                .andExpect(jsonPath("$.status", is("In Progress")));
    }

    @Test 
    void testTrySendMessage() throws Exception {
        // Mocking the SprintService to return a sprint
        Sprint sprint = new Sprint();
        OffsetDateTime startDate = OffsetDateTime.now();
        sprint.setId(1);
        sprint.setName("Sprint 1");
        sprint.setstartDate(startDate);
        Mockito.when(sprintService.getSprintById(1)).thenReturn(sprint);

        // Perform a POST request to the endpoint
        mockMvc.perform(post("/sprints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sprint)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Sprint 1")))
                .andExpect(jsonPath("$.status", is("In Progress")));
    }
    @Test
    void testGetBotUsername() {
        // Mocking the SprintService to return a sprint
        Sprint sprint = new Sprint();
        OffsetDateTime startDate = OffsetDateTime.now();
        sprint.setId(1);
        sprint.setName("Sprint 1");
        sprint.setstartDate(startDate);
        Mockito.when(sprintService.getSprintById(1)).thenReturn(sprint);

        // Perform a GET request to the endpoint
        mockMvc.perform(get("/sprints")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Sprint 1")))
                .andExpect(jsonPath("$.status", is("In Progress")));
    }

    
    
}
