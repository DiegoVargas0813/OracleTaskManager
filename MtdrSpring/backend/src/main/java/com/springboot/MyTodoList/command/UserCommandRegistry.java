package com.springboot.MyTodoList.command;

import com.springboot.MyTodoList.handler.TelegramBotHandler;
import com.springboot.MyTodoList.service.TaskCreationService;
import com.springboot.MyTodoList.service.TaskCompletionService;
import com.springboot.MyTodoList.service.UserStateService;
import com.springboot.MyTodoList.service.SessionMappingService;

import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotLabels;

public class UserCommandRegistry extends CommandRegistry {
    private TelegramBotHandler telegramBotHandler;
    private TaskCreationService taskCreationService;
    private TaskCompletionService taskCompletionService;
    private UserStateService userStateService;
    private SessionMappingService sessionMappingService;

    public UserCommandRegistry(TelegramBotHandler telegramBotHandler, TaskCreationService taskCreationService, TaskCompletionService taskCompletionService, UserStateService userStateService, SessionMappingService sessionMappingService) {
        super();
        this.telegramBotHandler = telegramBotHandler;
        this.taskCreationService = taskCreationService;
        this.taskCompletionService = taskCompletionService;
        this.userStateService = userStateService;
        this.sessionMappingService = sessionMappingService;

        // Menu principal
        registerCommand(BotCommands.START_COMMAND.getCommand(), new StartCommand(telegramBotHandler));
        registerCommand(BotLabels.SHOW_MAIN_SCREEN.getLabel(), new StartCommand(telegramBotHandler));

        // List all
        registerCommand(BotCommands.LIST_ALL.getCommand(), new ListAllCommand(telegramBotHandler));
        registerCommand(BotLabels.LIST_ALL_TASKS.getLabel(), new ListAllCommand(telegramBotHandler));

        //Create Task
        registerCommand(BotCommands.CREATE_TASK.getCommand(), new CreateTaskCommand(taskCreationService, userStateService));
        registerCommand(BotLabels.CREATE_NEW_TASK.getLabel(), new CreateTaskCommand(taskCreationService, userStateService));

        //Current Sprint
        registerCommand(BotCommands.CURRENT_SPRINT.getCommand(), new CurrentSprintCommand(telegramBotHandler));
        registerCommand(BotLabels.CURRENT_SPRINT.getLabel(), new CurrentSprintCommand(telegramBotHandler));

        //Backlog
        registerCommand(BotLabels.BACKLOG.getLabel(), new BacklogCommand(telegramBotHandler));
        //registerCommand(BotCommands.BACKLOG.getCommand(), new BacklogCommand(telegramBotHandler));

        //Mark task as Started
        registerCommand(BotLabels.START_TASK.getLabel(), new StartTaskCommand(telegramBotHandler));

        //Mark task as Done
        registerCommand(BotLabels.DONE.getLabel(), new CompleteTaskCommand(taskCompletionService, userStateService, sessionMappingService));

        //Logout
        registerCommand(BotCommands.LOGOUT.getCommand(), new LogoutCommand(userStateService));
    }
}

