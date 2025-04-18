package com.springboot.MyTodoList.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import com.springboot.MyTodoList.service.UserService;
import com.springboot.MyTodoList.service.UserStateService;
import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.util.UserState;

public class EmailVerificationState implements StateHandler {
    private final UserService userService;
    private final UserStateService userStateService;
    
    public EmailVerificationState(UserService userService, UserStateService userStateService) {
        this.userService = userService;
        this.userStateService = userStateService;
    }

    @Override
    public SendMessage handle(long chatId, String messageText, Integer userId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);


        // Validate email format
        if (isValidEmail(messageText)) {
            
            
            Integer retrievedUserId = userService.getUserIdByEmail(messageText);
            if (retrievedUserId != null) {
                // Update user state
                UserState userState = userStateService.getUserState(chatId);
                userState.setCurrentProcess(UserState.Process.NONE);
                userState.setProcessState(retrievedUserId);
                userStateService.updateUserState(chatId, userState);

                message.setText(BotMessages.LOGIN_SUCCESS.getMessage());
            } else {
                message.setText(BotMessages.LOGIN_FAILURE.getMessage());
            }
        } else {
            message.setText(BotMessages.LOGIN_INVALID_FORMAT.getMessage());
        }

        return message;
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}
