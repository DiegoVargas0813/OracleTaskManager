package com.springboot.MyTodoList.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import com.springboot.MyTodoList.service.TaskCompletionService;
import com.springboot.MyTodoList.service.UserStateService;
import com.springboot.MyTodoList.util.UserState;

import com.springboot.MyTodoList.util.BotLabels;

public class CompleteTaskCommand implements Command{
    private final TaskCompletionService taskCompletionService;
    private final UserStateService userStateService;

    public CompleteTaskCommand(TaskCompletionService taskCompletionService, UserStateService userStateService) {
        this.taskCompletionService = taskCompletionService;
        this.userStateService = userStateService;
    }

    @Override
    public SendMessage execute(long chatId, String messageText, int userId) {
        int taskId = Integer.parseInt(messageText.split(BotLabels.DASH.getLabel())[0]);
        
        System.out.println("Task ID in registry: " + taskId);

        // Update user state to TASK_COMPLETION
        UserState userState = userStateService.getUserState(chatId);
        userState.setCurrentProcess(UserState.Process.TASK_COMPLETION);
        userStateService.updateUserState(chatId, userState);

        // Start task completion
        return taskCompletionService.startTaskCompletionProcess(chatId, taskId, userId); // Pass userId if needed
    }
}
