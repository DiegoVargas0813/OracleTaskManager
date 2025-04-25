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
public class TaskItemBotControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    public void onUpdateReceivedTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/bot/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"updateId\": 12345, \"message\": {\"chat\": {\"id\": 1}, \"text\": \"/start\"}}"))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 200 OK status");
    }
    public void sendMessageTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/bot/sendMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"chatId\": 1, \"text\": \"Hello, World!\"}"))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 200 OK status");
    }
    public void getBotUsernameTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/bot/username")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 200 OK status");
    }
    public void trySendMessageTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/bot/trySendMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"chatId\": 1, \"text\": \"Hello, World!\"}"))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 200 OK status");
    }
}


