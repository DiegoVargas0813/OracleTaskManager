package com.springboot.MyTodoList;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControlletTest {
    @Autowired
	private MockMvc mockMvc;

    @Test
    public void testCreateTask() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test Task\", \"description\": \"This is a test task.\"}"))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 200 OK status" );
    }

    @Test
    public void testGetAllTasks() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 200 OK status" );
        String content = mvcResult.getResponse().getContentAsString();
        assertTrue(content.contains("Test Task"), "Validate response contains 'Test Task'" );
    }
    @Test
    public void testGetTaskById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/1")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 200 OK status" );
        String content = mvcResult.getResponse().getContentAsString();
        assertTrue(content.contains("Test Task"), "Validate response contains 'Test Task'" );
    }
    @Test
    public void testGetTasksByUser() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/user/1")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 200 OK status" );
        String content = mvcResult.getResponse().getContentAsString();
        assertTrue(content.contains("Test Task"), "Validate response contains 'Test Task'" );
    }
    @Test
    public void testGetTasksBySprintId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/sprint/1")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 200 OK status" );
        String content = mvcResult.getResponse().getContentAsString();
        assertTrue(content.contains("Test Task"), "Validate response contains 'Test Task'" );
    }
    @Test
    public void testAssignTaskToSprint() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/tasks/1/assign-sprint/1")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 200 OK status" );
        String content = mvcResult.getResponse().getContentAsString();
        assertTrue(content.contains("Task assigned to sprint successfully"), "Validate response contains 'Task assigned to sprint successfully'" );
    }
    @Test
    public void testUpdateTaskStatus() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/tasks/1/update-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"In Progress\"}"))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 200 OK status" );
        String content = mvcResult.getResponse().getContentAsString();
        assertTrue(content.contains("Task status updated successfully"), "Validate response contains 'Task status updated successfully'" );
    }
}
