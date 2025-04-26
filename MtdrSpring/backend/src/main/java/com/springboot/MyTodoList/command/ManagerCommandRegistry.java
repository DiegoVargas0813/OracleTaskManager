package com.springboot.MyTodoList.command;

import com.springboot.MyTodoList.handler.TelegramBotHandler;
import com.springboot.MyTodoList.service.TaskCreationService;
import com.springboot.MyTodoList.service.TaskCompletionService;
import com.springboot.MyTodoList.service.UserStateService;
import com.springboot.MyTodoList.service.SessionMappingService;
import com.springboot.MyTodoList.service.KPIService;

import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotLabels;

public class ManagerCommandRegistry extends CommandRegistry {
    private TelegramBotHandler telegramBotHandler;
    private TaskCreationService taskCreationService;
    private TaskCompletionService taskCompletionService;
    private UserStateService userStateService;
    private SessionMappingService sessionMappingService;
    private KPIService kpiService;

    public ManagerCommandRegistry(TelegramBotHandler telegramBotHandler, TaskCreationService taskCreationService, TaskCompletionService taskCompletionService, UserStateService userStateService, SessionMappingService sessionMappingService, KPIService kpiService) {
        super();
        this.telegramBotHandler = telegramBotHandler;
        this.taskCreationService = taskCreationService;
        this.taskCompletionService = taskCompletionService;
        this.userStateService = userStateService;
        this.sessionMappingService = sessionMappingService;
        this.kpiService = kpiService;

        // Menu principal
        registerCommand(BotCommands.START_COMMAND.getCommand(), new StartCommandManager(telegramBotHandler));
        registerCommand(BotLabels.SHOW_MAIN_SCREEN.getLabel(), new StartCommandManager(telegramBotHandler));

        // List All
        registerCommand(BotLabels.LIST_USERS.getLabel(), new ListAllManagerCommand(telegramBotHandler));

        // List User Tasks
        registerCommand(BotLabels.LIST_USER_TASKS.getLabel(), new CurrentSprintManagerCommand(telegramBotHandler, sessionMappingService));
        
        //Create Task
        registerCommand(BotCommands.CREATE_TASK.getCommand(), new CreateTaskCommand(taskCreationService, userStateService));
        registerCommand(BotLabels.CREATE_NEW_TASK.getLabel(), new CreateTaskCommand(taskCreationService, userStateService));

        //Check KPIs
        registerCommand(BotLabels.CHECK_KPIS.getLabel(), new CheckKPIManagerCommand(kpiService, sessionMappingService));

         //Mark task as Started
         registerCommand(BotLabels.START_TASK.getLabel(), new StartTaskCommand(telegramBotHandler));

         //Mark task as Done
         registerCommand(BotLabels.DONE.getLabel(), new CompleteTaskCommand(taskCompletionService, userStateService, sessionMappingService));

        //Logout
        registerCommand(BotCommands.LOGOUT.getCommand(), new LogoutCommand(userStateService, sessionMappingService));
    }
}
