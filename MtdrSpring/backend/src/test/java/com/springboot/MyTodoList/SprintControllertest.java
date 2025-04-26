package com.springboot.MyTodoList;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.springboot.MyTodoList.controller.SprintController;
import com.springboot.MyTodoList.model.Manager;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.service.ManagerService;
import com.springboot.MyTodoList.service.TaskService;
import com.springboot.MyTodoList.service.UserService;

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
class SprintControllerTest {

    @Autowired
    private MockMvc mockMvc;
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

    Sprint createMockSprint() {
        // Create a mock sprint object with sample data
        Sprint sprint = new Sprint();

        Project project = new Project();
        project.setId(1);


        sprint.setId(1);
        sprint.setName("Basic Infraestructure");
        sprint.setstartDate(OffsetDateTime.now().minusDays(7));
        sprint.setendDate(OffsetDateTime.now().plusDays(7));
        sprint.setProject(project);
        sprint.setTasks(new ArrayList<>());
        sprint.setIssues(new ArrayList<>());
        return sprint;
    }

    @Test 
    void  testCreateSprint()throws Exception {
        Sprint mockSprint = createMockSprint();

        Mockito.when(sprintService.createSprint(any(Sprint.class))).thenReturn(mockSprint);
        mockMvc.perform(post("/api/sprints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockSprint)))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "1"))
                .andExpect(header().string("Access-Control-Expose-Headers", "location"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Basic Infraestructure"))
                .andExpect(jsonPath("$.startDate").exists())
                .andExpect(jsonPath("$.endDate").exists())
                .andExpect(jsonPath("$.tasks").isArray())
                .andExpect(jsonPath("$.tasks").isEmpty())
                .andExpect(jsonPath("$.issues").isArray())
                .andExpect(jsonPath("$.issues").isEmpty());
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
        Sprint mockSprint = createMockSprint();

        Mockito.when(sprintService.getSprintById(eq(1))).thenReturn(java.util.Optional.of(mockSprint));

        mockMvc.perform(get("/api/sprints/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Basic Infraestructure")))
                .andExpect(jsonPath("$.startDate").exists())
                .andExpect(jsonPath("$.endDate").exists())
                .andExpect(jsonPath("$.tasks").isArray())
                .andExpect(jsonPath("$.tasks").isEmpty())
                .andExpect(jsonPath("$.issues").isArray())
                .andExpect(jsonPath("$.issues").isEmpty());

        // Not found case
        mockMvc.perform(get("/api/sprints/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test 
    void testGetActiveSprints() throws Exception {
        List<Sprint> activeSprints = new ArrayList<>();
        Sprint sprint1 = createMockSprint();
        activeSprints.add(sprint1);

        Sprint sprint2 = createMockSprint();
        sprint2.setId(2);
        sprint2.setName("Active Sprint 2");
        activeSprints.add(sprint2);

        Mockito.when(sprintService.getActiveSprints()).thenReturn(activeSprints);

        mockMvc.perform(get("/api/sprints/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Basic Infraestructure")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Active Sprint 2")));

        //Not found case

        activeSprints = new ArrayList<>();

        Mockito.when(sprintService.getActiveSprints()).thenReturn(activeSprints);

        mockMvc.perform(get("/api/sprints/active"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }
}

