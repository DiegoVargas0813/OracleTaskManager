package com.springboot.MyTodoList.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import com.springboot.MyTodoList.service.TaskCompletionService;
import com.springboot.MyTodoList.service.UserStateService;
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotMessages;

public class TaskCompletionState implements StateHandler{
    private final TaskCompletionService taskCompletionService;
    private final UserStateService userStateService;

    public TaskCompletionState(TaskCompletionService taskCompletionService, UserStateService userStateService) {
        this.taskCompletionService = taskCompletionService;
        this.userStateService = userStateService;
    }

    @Override
    public SendMessage handle(long chatId, String messageText, Integer userId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        // Handle cancellation
        if (messageText.equalsIgnoreCase(BotCommands.CANCEL.getCommand())) {
            userStateService.resetUserState(chatId);
            message.setText(BotMessages.FINISH_COMPLETION.getMessage());
            return message;
        }

        // Delegate to TaskCompletionService for handling the task completion process
        message = taskCompletionService.handleTaskCompletition(chatId, messageText);



        // Check if the process is completed
        if (message.getText().contains(BotMessages.FINISH_COMPLETION.getMessage())) {
            // Reset the user state after completion
            userStateService.resetUserState(chatId);
        }

        return message;
    }
}
