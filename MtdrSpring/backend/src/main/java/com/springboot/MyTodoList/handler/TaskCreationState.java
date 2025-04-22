package com.springboot.MyTodoList.handler;

import com.springboot.MyTodoList.service.TaskCreationService;
import com.springboot.MyTodoList.service.UserStateService;
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotMessages;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class TaskCreationState implements StateHandler{
    private final TaskCreationService taskCreationService;
    private final UserStateService userStateService;

    public TaskCreationState(TaskCreationService taskCreationService, UserStateService userStateService) {
        this.taskCreationService = taskCreationService;
        this.userStateService = userStateService;
    }

    @Override
    public SendMessage handle(long chatId, String messageText, Integer userId){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        if(messageText.equalsIgnoreCase(BotCommands.CANCEL.getCommand())){
            userStateService.resetUserState(chatId);
            message.setText(BotMessages.FINISH_TASK_CREATION.getMessage());
            return message;
        }

        message = taskCreationService.handleTaskCreation(chatId, messageText);

        if(message.getText().contains(BotMessages.FINISH_TASK_CREATION.getMessage())){
            userStateService.resetUserState(chatId);
        }

        return message;
    }
}
