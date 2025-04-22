package com.springboot.MyTodoList.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import com.springboot.MyTodoList.handler.TelegramBotHandler;

public class CurrentSprintCommand implements Command{
    private final TelegramBotHandler botHandler;
    
    public CurrentSprintCommand(TelegramBotHandler botHandler) {
        this.botHandler = botHandler;
    }

    @Override
    public SendMessage execute(long chatId, String messageText, int userId) {
        return botHandler.sendCurrentSprintMenu(chatId, userId);
    }
}
