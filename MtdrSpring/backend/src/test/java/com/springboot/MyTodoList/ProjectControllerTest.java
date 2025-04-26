package com.springboot.MyTodoList;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.springboot.MyTodoList.controller.ProjectController;
import com.springboot.MyTodoList.model.Manager;
import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.service.ManagerService;
import com.springboot.MyTodoList.service.ProjectService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

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

    @Test
    void testcreateProject() throws Exception{
        Project inputProject = new Project();
        inputProject.setId(0);
        inputProject.setName("Test Project");
        inputProject.setDescription("Test Description");
        inputProject.setCreationTs(OffsetDateTime.now());
        inputProject.setmanager(new Manager());
        inputProject.setSprints(new ArrayList<>());

        Mockito.when(projectService.createProject(any(Project.class)))
                .thenReturn(inputProject);
        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputProject)))
                .andExpect(status().isOk())
                .andExpect(header().string("location", "0"))
                .andExpect(header().string("Access-Control-Expose-Headers", "location"));   
    }

    @Test 
    void testGetAllProjects() throws Exception {
        List<Project> projects = new ArrayList<>();
        Project project1 = new Project();
        project1.setId(1);
        project1.setName("Project 1");
        project1.setDescription("Description 1");
        projects.add(project1);

        Project project2 = new Project();
        project2.setId(2);
        project2.setName("Project 2");
        project2.setDescription("Description 2");
        projects.add(project2);

        Mockito.when(projectService.getAllProjects()).thenReturn(projects);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Project 1")))
                .andExpect(jsonPath("$[0].description", is("Description 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Project 2")))
                .andExpect(jsonPath("$[1].description", is("Description 2")));
    }
    @Test
    void testGetProjectById() throws Exception {
        Project project = new Project();
        project.setId(1);
        project.setName("Project 1");
        project.setDescription("Description 1");

        Mockito.when(projectService.getProjectById(1)).thenReturn(java.util.Optional.of(project));

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Project 1")))
                .andExpect(jsonPath("$.description", is("Description 1")));
    }
    @Test
    void testGetProjectByManagerId() throws Exception {
        List<Project> projects = new ArrayList<>();
        Project project1 = new Project();
        project1.setId(1);
        project1.setName("Project 1");
        project1.setDescription("Description 1");
        projects.add(project1);

        Project project2 = new Project();
        project2.setId(2);
        project2.setName("Project 2");
        project2.setDescription("Description 2");
        projects.add(project2);

        Mockito.when(projectService.getProjectsByManagerId(1)).thenReturn(projects);

        mockMvc.perform(get("/api/projects/manager/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Project 1")))
                .andExpect(jsonPath("$[0].description", is("Description 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Project 2")))
                .andExpect(jsonPath("$[1].description", is("Description 2")));
    }


}