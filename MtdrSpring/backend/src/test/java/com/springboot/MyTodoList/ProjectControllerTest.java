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
public class ProjectControllerTest {
    @Autowired
	private MockMvc mockMvc;

    @Test
    public void testCreateProject() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test Project\", \"description\": \"This is a test project.\"}"))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 200 OK status" );
    }
    
    @Test
    public void testGetAllProjects() throws Exception{
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/projects")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 200 OK status" );
        String content = mvcResult.getResponse().getContentAsString();
        assertTrue(content.contains("Test Project"), "Validate response contains project name" );
    }


    @Test
    public void testGetProjectByManagerID() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/projects/manager/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 200 OK status" );
        String content = mvcResult.getResponse().getContentAsString();
        assertTrue(content.contains("Test Project"), "Validate response contains project name" );
    }
}
