package com.springboot.MyTodoList.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import com.springboot.MyTodoList.service.UserStateService;
import com.springboot.MyTodoList.service.SessionMappingService;

import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.UserState;

public class LogoutCommand implements Command {
    private final UserStateService userStateService;
    private final SessionMappingService sessionMappingService;

    public LogoutCommand(UserStateService userStateService, SessionMappingService sessionMappingService) {
        this.userStateService = userStateService;
        this.sessionMappingService = sessionMappingService;
    }

    @Override
    public SendMessage execute(long chatId, String messageText, int userId) {
        // Reset user state
        UserState userState = userStateService.getUserState(chatId);
        userState.setCurrentProcess(UserState.Process.EMAIL_VERIFICATION);
        userState.setProcessState(null);
        userStateService.updateUserState(chatId, userState);

        // Clear user ID
        userId = 0; // Reset userId to indicate no user is logged in

        // Clear session mappings
        sessionMappingService.cleanupSession(chatId);

        // Send logout success message
        SendMessage message = BotHelper.createMessage(chatId, BotMessages.LOGOUT_SUCCESS.getMessage());
        return message;
    }
}