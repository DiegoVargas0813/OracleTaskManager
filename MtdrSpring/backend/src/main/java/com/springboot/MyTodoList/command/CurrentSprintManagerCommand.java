package com.springboot.MyTodoList.command;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import com.springboot.MyTodoList.handler.TelegramBotHandler;
import com.springboot.MyTodoList.util.BotLabels;

import com.springboot.MyTodoList.service.SessionMappingService;

public class CurrentSprintManagerCommand implements Command{
    private final TelegramBotHandler botHandler;
    private final SessionMappingService sessionMappingService;

    public CurrentSprintManagerCommand(TelegramBotHandler botHandler, SessionMappingService sessionMappingService) {
        this.botHandler = botHandler;
        this.sessionMappingService = sessionMappingService;
    }

    @Override
    public SendMessage execute(long chatId, String messageText, int userId) {
        String extractedId = messageText.split(BotLabels.DASH.getLabel())[0];
        int databaseId = sessionMappingService.getOriginalId(chatId, "users", extractedId);
        System.out.println("DATABASE ID: " + databaseId);

        return botHandler.sendCurrentSprintMenu(chatId, databaseId);
    }
}
