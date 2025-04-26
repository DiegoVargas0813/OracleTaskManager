package com.springboot.MyTodoList;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.springboot.MyTodoList.controller.SprintController;
import com.springboot.MyTodoList.model.Manager;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.service.ManagerService;
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

@WebMvcTest(SprintController.class)
public class TaskItemBotControllerTest {


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

    @Test 
    void testOnUpdateReceived() throws Exception {
        // Arrange
        String updateJson = "{ \"update_id\": 12345, \"message\": { \"chat\": { \"id\": 1, \"first_name\": \"Test\", \"last_name\": \"User\" }, \"text\": \"/start\" } }";
        mockMvc.perform(post("/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk());
    }
    @Test
    void testSendMessage() throws Exception {
        // Arrange
        String chatId = "12345";
        String message = "Hello, World!";
        mockMvc.perform(post("/sendMessage")
                .param("chatId", chatId)
                .param("message", message))
                .andExpect(status().isOk());
    }
    @Test
    void testTryToSendMessage() throws Exception {
        // Arrange
        String chatId = "12345";
        String message = "Hello, World!";
        mockMvc.perform(post("/tryToSendMessage")
                .param("chatId", chatId)
                .param("message", message))
                .andExpect(status().isOk());
    }
    @Test
    void testGetBotUsername() throws Exception {
        // Arrange
        String expectedUsername = "TestBot";
        mockMvc.perform(get("/getBotUsername"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedUsername));
    }    
}
