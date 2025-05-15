package com.springboot.MyTodoList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;


import org.telegram.telegrambots.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.abilitybots.api.bot.BaseAbilityBot;

import com.springboot.MyTodoList.command.ManagerCommandRegistry;
import com.springboot.MyTodoList.command.UserCommandRegistry;
import com.springboot.MyTodoList.controller.TaskItemBotController;
import com.springboot.MyTodoList.handler.StateHandler;
import com.springboot.MyTodoList.handler.StateHandlerRegistry;
import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.service.TaskService;
import com.springboot.MyTodoList.service.UserService;
import com.springboot.MyTodoList.service.UserStateService;
import com.springboot.MyTodoList.service.ManagerService;
import com.springboot.MyTodoList.handler.TelegramBotHandler;
import com.springboot.MyTodoList.command.CurrentSprintCommand;
import com.springboot.MyTodoList.command.ListAllManagerCommand;

import com.springboot.MyTodoList.service.SessionMappingService;

import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.util.UserState;
import com.springboot.MyTodoList.util.BotLabels;

import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;
import static org.mockito.Mockito.spy;

import org.eclipse.jetty.server.session.Session;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TaskItemBotControllerTest {
    
    @Value("${telegram.bot.token}")
    public static String BOT_TOKEN;

    @Value("${telegram.bot.name}")
    public static String BOT_NAME;

    public static final long USER_ID = 123456789L;
    public static final String USER_NAME = "testUser";
    public static final long CHAT_ID = 987654321L;

    private TaskItemBotController taskItemBotController;
    private SilentSender silentSender;

    @Mock
    private Update mockUpdate;

    @Mock
    private User mockUser;

    //@Mock
    //private BaseAbilityBot mockBot;

    @Mock
    private UserStateService mockUserStateService;

    @Mock
    private StateHandlerRegistry mockStateHandlerRegistry;

    @Mock
    private StateHandler mockStateHandler;

    @Mock
    private TaskService mockTaskService;

    @Mock
    private SprintService  mockSprintService;

    @Mock
    private ManagerService mockManagerService;

    @Mock
    private UserService mockUserService;

    @Mock
    private ManagerCommandRegistry mockManagerCommandRegistry;

    @Mock
    private SessionMappingService mockSessionMappingService;

    @BeforeEach
    public void setUp() {
        taskItemBotController = spy(new TaskItemBotController(BOT_TOKEN, BOT_NAME, mockTaskService, mockSprintService, mockUserService, mockManagerService));
        mockUser = new User(USER_ID, USER_NAME, false);
        mockUser.setLanguageCode("en");
        taskItemBotController.setUserId(0);
        mockUpdate = Mockito.mock(Update.class);
        //mockBot = Mockito.mock(BaseAbilityBot.class);
    }



    @Test
    public void testCurrentSprintCommandExecution() {
        // Arrange
        Update mockUpdate = Mockito.mock(Update.class);
        org.telegram.telegrambots.meta.api.objects.Message mockMessage = Mockito.mock(org.telegram.telegrambots.meta.api.objects.Message.class);
        UserState mockUserState = Mockito.mock(UserState.class);
        CurrentSprintCommand mockCurrentSprintCommand = Mockito.mock(CurrentSprintCommand.class);
        SendMessage expectedMessage = new SendMessage();
        expectedMessage.setChatId(String.valueOf(CHAT_ID));
        expectedMessage.setText("Current sprint menu");

        // Mock update and message behavior
        Mockito.when(mockUpdate.hasMessage()).thenReturn(true);
        Mockito.when(mockUpdate.getMessage()).thenReturn(mockMessage);
        Mockito.when(mockMessage.hasText()).thenReturn(true);
        Mockito.when(mockMessage.getText()).thenReturn("/currentsprint");
        Mockito.when(mockMessage.getChatId()).thenReturn(CHAT_ID);

        // Mock user state behavior
        Mockito.when(mockUserStateService.getUserState(CHAT_ID)).thenReturn(mockUserState);
        Mockito.when(mockUserState.getRole()).thenReturn(UserState.Role.MANAGER);
        Mockito.when(mockUserState.getCurrentProcess()).thenReturn(UserState.Process.NONE);

        taskItemBotController.setUserStateService(mockUserStateService);
        taskItemBotController.setManagerCommandRegistry(mockManagerCommandRegistry);

        // Mock command registry behavior
        Mockito.when(mockManagerCommandRegistry.getCommand("/currentsprint")).thenReturn(mockCurrentSprintCommand);
        Mockito.when(mockCurrentSprintCommand.execute(CHAT_ID, "/currentsprint", 0)).thenReturn(expectedMessage);

        // Act
        taskItemBotController.onUpdateReceived(mockUpdate);

        // Assert
        Mockito.verify(mockManagerCommandRegistry, Mockito.times(1)).getCommand("/currentsprint");
        Mockito.verify(mockCurrentSprintCommand, Mockito.times(1)).execute(CHAT_ID, "/currentsprint", 0);
    }

@Test
public void testListAllCommandExecution() {
    // Arrange
    long chatId = CHAT_ID;
    int managerId = 0;

    // Mock the list of users under the manager
    com.springboot.MyTodoList.model.User user1 = new com.springboot.MyTodoList.model.User();
    user1.setId(1);
    user1.setName("User1");
    user1.setRole("USER");

    com.springboot.MyTodoList.model.User user2 = new com.springboot.MyTodoList.model.User();
    user2.setId(2);
    user2.setName("User2");
    user2.setRole("USER");

    com.springboot.MyTodoList.model.User user3 = new com.springboot.MyTodoList.model.User();
    user3.setId(3);
    user3.setName("User3");
    user3.setRole("USER");

    // Use a mutable list instead of List.of()
    List<com.springboot.MyTodoList.model.User> mockUsers = new ArrayList<>();
    mockUsers.add(user1);
    mockUsers.add(user2);
    mockUsers.add(user3);

    // Mock the UserState object
    UserState mockUserState = Mockito.mock(UserState.class);

    // Mock user state behavior
    Mockito.when(mockUserStateService.getUserState(CHAT_ID)).thenReturn(mockUserState);
    Mockito.when(mockUserState.getRole()).thenReturn(UserState.Role.MANAGER);
    Mockito.when(mockUserState.getCurrentProcess()).thenReturn(UserState.Process.NONE);

    // Mock the behavior of the UserService to return the list of users
    Mockito.when(mockUserService.getUserByManagerId(managerId)).thenReturn(mockUsers);

    // Mock the behavior of the SessionMappingService to store the mapping
    Mockito.doNothing().when(mockSessionMappingService).storeMapping(Mockito.eq(chatId), Mockito.eq("users"), Mockito.anyMap());

    // Mock the Update and Message objects
    Update mockUpdate = Mockito.mock(Update.class);
    org.telegram.telegrambots.meta.api.objects.Message mockMessage = Mockito.mock(org.telegram.telegrambots.meta.api.objects.Message.class);

    // Mock the behavior of the Update and Message
    Mockito.when(mockUpdate.hasMessage()).thenReturn(true);
    Mockito.when(mockUpdate.getMessage()).thenReturn(mockMessage);
    Mockito.when(mockMessage.hasText()).thenReturn(true);
    Mockito.when(mockMessage.getText()).thenReturn(BotLabels.LIST_USERS.getLabel());
    Mockito.when(mockMessage.getChatId()).thenReturn(chatId);

    // Use the real TelegramBotHandler implementation
    TelegramBotHandler telegramBotHandler = new TelegramBotHandler(mockTaskService, mockSprintService, mockUserService, mockSessionMappingService);

    // Inject the TelegramBotHandler into the ListAllManagerCommand
    ListAllManagerCommand listAllManagerCommand = new ListAllManagerCommand(telegramBotHandler);

    // Mock the ManagerCommandRegistry to return the ListAllManagerCommand
    Mockito.when(mockManagerCommandRegistry.getCommand(BotLabels.LIST_USERS.getLabel())).thenReturn(listAllManagerCommand);

    // Inject the mocked ManagerCommandRegistry into the TaskItemBotController
    taskItemBotController.setManagerCommandRegistry(mockManagerCommandRegistry);
    taskItemBotController.setTelegramBotHandler(telegramBotHandler);
    taskItemBotController.setUserStateService(mockUserStateService);
    taskItemBotController.setManagerService(mockManagerService);
    taskItemBotController.setUserService(mockUserService);

    // Act
    taskItemBotController.onUpdateReceived(mockUpdate);

    // Assert
    Mockito.verify(mockUserService, Mockito.times(1)).getUserByManagerId(managerId); // Ensure this is called
    Mockito.verify(mockSessionMappingService, Mockito.times(1)).storeMapping(Mockito.eq(chatId), Mockito.eq("users"), Mockito.anyMap());
    Mockito.verify(mockManagerCommandRegistry, Mockito.times(1)).getCommand(BotLabels.LIST_USERS.getLabel());
}
}
