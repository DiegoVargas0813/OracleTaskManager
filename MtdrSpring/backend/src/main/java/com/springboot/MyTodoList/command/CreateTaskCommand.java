package com.springboot.MyTodoList.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import com.springboot.MyTodoList.service.TaskCreationService;
import com.springboot.MyTodoList.service.UserStateService;
import com.springboot.MyTodoList.util.UserState;

public class CreateTaskCommand implements Command {
    private final TaskCreationService taskCreationService;
    private final UserStateService userStateService;

    public CreateTaskCommand(TaskCreationService taskCreationService, UserStateService userStateService) {
        this.taskCreationService = taskCreationService;
        this.userStateService = userStateService;
    }

    @Override
    public SendMessage execute(long chatId, String messageText, int userId) {
        // Update user state to TASK_CREATION
        UserState userState = userStateService.getUserState(chatId);
        userState.setCurrentProcess(UserState.Process.TASK_CREATION);
        userStateService.updateUserState(chatId, userState);

        // Start task creation
        return taskCreationService.startTaskCreation(chatId, userId); // Pass userId if needed
    }
}
