package com.springboot.MyTodoList.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import com.springboot.MyTodoList.service.UserService;
import com.springboot.MyTodoList.service.ManagerService;
import com.springboot.MyTodoList.service.UserStateService;
import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.util.UserState;
import com.springboot.MyTodoList.model.Manager;
import com.springboot.MyTodoList.model.User;
import java.util.Optional;

public class EmailVerificationState implements StateHandler {
    private final UserService userService;
    private final ManagerService managerService;
    private final UserStateService userStateService;
    
    public EmailVerificationState(UserService userService, UserStateService userStateService, ManagerService managerService) {
        this.userService = userService;
        this.userStateService = userStateService;
        this.managerService = managerService;
    }   

    @Override
    public SendMessage handle(long chatId, String messageText, Integer userId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);


        // Validate email format
        if (isValidEmail(messageText)) {  
            Integer retrievedUserId = userService.getUserIdByEmail(messageText);
            Optional<Manager> retrievedManager = managerService.getManagerIdByEmail(messageText);
            Integer retrievedManagerId = retrievedManager.map(Manager::getId).orElse(null);
            
            if (retrievedUserId != null) {
                // Update user state
                UserState userState = userStateService.getUserState(chatId);
                userState.setCurrentProcess(UserState.Process.NONE);
                userState.setProcessState(retrievedUserId);
                userState.setRole(UserState.Role.USER);
                userStateService.updateUserState(chatId, userState);
                message.setText(BotMessages.LOGIN_SUCCESS.getMessage());
            } else if (retrievedManagerId != null) {
                // Update user state
                UserState userState = userStateService.getUserState(chatId);
                userState.setCurrentProcess(UserState.Process.NONE);
                userState.setProcessState(retrievedManagerId);
                userState.setRole(UserState.Role.MANAGER);
                userStateService.updateUserState(chatId, userState);
                message.setText(BotMessages.LOGIN_SUCCESS.getMessage());
            }
            else {
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
