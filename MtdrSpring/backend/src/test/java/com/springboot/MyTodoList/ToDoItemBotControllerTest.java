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



import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc

public class ToDoItemBotControllerTest {

    @Autowired 
    private MockMvc mockMvc;
    @Test  

    public void onUpdateReceivedTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/bot/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"updateId\": 12345, \"message\": {\"chat\": {\"id\": 1}, \"text\": \"/start\"}}"))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 200 OK status");
    }

    @Test
    public void getBotUsernameTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/bot/username")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 200 OK status");
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(response.contains("bot"), "Validate response contains 'bot'");
    }

    @Test 
    public void getAllToDoItemsTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/todo/items")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 200 OK status");
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(response.contains("items"), "Validate response contains 'items'");
    }
    @Test
    public void getToDoItemByIdTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/todo/items/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 200 OK status");
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(response.contains("item"), "Validate response contains 'item'");
    }
    @Test
    public void addToDoItemTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/todo/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"New Task\", \"description\": \"Task description\"}"))
                .andReturn();

        assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 201 Created status");
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(response.contains("item"), "Validate response contains 'item'");
    }
    @Test
    public void updateToDoItemTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/todo/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Updated Task\", \"description\": \"Updated description\"}"))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 200 OK status");
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(response.contains("item"), "Validate response contains 'item'");
    }
    @Test
    public void deleteToDoItemTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/todo/items/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(HttpStatus.NO_CONTENT.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 204 No Content status");
    }
    

}

