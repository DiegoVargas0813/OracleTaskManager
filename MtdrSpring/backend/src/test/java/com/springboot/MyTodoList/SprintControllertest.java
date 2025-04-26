package com.springboot.MyTodoList;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.springboot.MyTodoList.controller.ManagerController;
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
class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SprintService sprintService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test 
    void  testCreateSprint()throws Exception {
        Sprint inputSprint = new Sprint();
        inputSprint.setName("Test Sprint");
        inputSprint.setstartDate(OffsetDateTime.now());
        inputSprint.setendDate(OffsetDateTime.now().plusDays(7));
        inputSprint.setProject(null);
        inputSprint.setTasks(new ArrayList<>());
        inputSprint.setIssues(new ArrayList<>());
        Sprint savedSprint = new Sprint();
        savedSprint.setId(1);

        Mockito.when(sprintService.createSprint(any(Sprint.class)))
                .thenReturn(savedSprint);
        mockMvc.perform(post("/api/sprints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputSprint)))
                .andExpect(status().isOk())
                .andExpect(header().string("location", "1"));   

    }

    @Test 
    void testGetAllSprints() throws Exception {
        List<Sprint> sprints = new ArrayList<>();
        Sprint sprint1 = new Sprint();
        sprint1.setId(1);
        sprint1.setName("Sprint 1");
        sprints.add(sprint1);

        Sprint sprint2 = new Sprint();
        sprint2.setId(2);
        sprint2.setName("Sprint 2");
        sprints.add(sprint2);

        Mockito.when(sprintService.getAllSprints()).thenReturn(sprints);

        mockMvc.perform(get("/api/sprints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Sprint 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Sprint 2")));
    } 

    @Test 
    void testGetSprintById() throws Exception {
        Sprint sprint = new Sprint();
        sprint.setId(1);
        sprint.setName("Sprint 1");

        Mockito.when(sprintService.getSprintById(1)).thenReturn(java.util.Optional.of(sprint));

        mockMvc.perform(get("/api/sprints/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Sprint 1")));
    }

    @Test 
    void testGetActiveSprints() throws Exception {
        List<Sprint> activeSprints = new ArrayList<>();
        Sprint sprint1 = new Sprint();
        sprint1.setId(1);
        sprint1.setName("Active Sprint 1");
        activeSprints.add(sprint1);

        Sprint sprint2 = new Sprint();
        sprint2.setId(2);
        sprint2.setName("Active Sprint 2");
        activeSprints.add(sprint2);

        Mockito.when(sprintService.getActiveSprints()).thenReturn(activeSprints);

        mockMvc.perform(get("/api/sprints/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Active Sprint 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Active Sprint 2")));
    }
    
}

