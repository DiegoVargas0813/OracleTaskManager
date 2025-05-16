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

    Project createMockProject() {
        // Create a mock project object with sample data
        Project project = new Project();
        // Create a mock manager object with sample data
        Manager manager = new Manager();
        manager.setId(1);

        project.setId(1);
        project.setName("An Oracle OCI task manager");
        project.setDescription("Create and deploy an OCI bot and portal");
        project.setCreationTs(OffsetDateTime.now());
        project.setmanager(manager);
        project.setSprints(new ArrayList<>());
        return project;
    }

    @Test
    void testcreateProject() throws Exception{
        Project mockProject = createMockProject();

        Mockito.when(projectService.createProject(any(Project.class))).thenReturn(mockProject);
        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockProject)))
            .andExpect(status().isCreated())
            .andExpect(header().string("location", "1"))
            .andExpect(header().string("Access-Control-Expose-Headers", "location"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1)) 
            .andExpect(jsonPath("$.name").value("An Oracle OCI task manager"))
            .andExpect(jsonPath("$.description").value("Create and deploy an OCI bot and portal"))
            .andExpect(jsonPath("$.sprints").isArray())
            .andExpect(jsonPath("$.sprints").isEmpty())
            .andExpect(jsonPath("$.creationTs").exists());
    }

    @Test 
    void testGetAllProjects() throws Exception {
        
        List<Project> projects = new ArrayList<>();

        Project project1 = createMockProject();
        projects.add(project1);

        Project project2 = new Project();
        project2.setId(2);
        project2.setName("Project 2");
        project2.setDescription("Description 2");
        project2.setCreationTs(OffsetDateTime.now());
        project2.setmanager(new Manager());
        project2.setSprints(new ArrayList<>());
        projects.add(project2);

        Mockito.when(projectService.getAllProjects()).thenReturn(projects);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("An Oracle OCI task manager"))
                .andExpect(jsonPath("$[0].description").value("Create and deploy an OCI bot and portal"))
                .andExpect(jsonPath("$[0].sprints").isArray())
                .andExpect(jsonPath("$[0].sprints").isEmpty())
                .andExpect(jsonPath("$[0].creationTs").exists())
                .andExpect(jsonPath("$[1]").exists());

        // Empty DB case
        List<Project> emptyProjects = new ArrayList<>();

        Mockito.when(projectService.getAllProjects()).thenReturn(emptyProjects);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void testGetProjectByManagerId() throws Exception {
        List<Project> projects = new ArrayList<>();

        Project project1 = createMockProject();
        projects.add(project1);

        Mockito.when(projectService.getProjectsByManagerId(1)).thenReturn(projects);

        mockMvc.perform(get("/api/projects/manager/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("An Oracle OCI task manager"))
                .andExpect(jsonPath("$[0].description").value("Create and deploy an OCI bot and portal"))
                .andExpect(jsonPath("$[0].sprints").isArray())
                .andExpect(jsonPath("$[0].sprints").isEmpty())
                .andExpect(jsonPath("$[0].creationTs").exists())
                .andExpect(jsonPath("$[1]").doesNotExist());

        // Not found case
        projects = new ArrayList<>();
        Mockito.when(projectService.getProjectsByManagerId(2)).thenReturn(projects);
        mockMvc.perform(get("/api/projects/manager/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
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

}