package com.springboot.MyTodoList.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import com.springboot.MyTodoList.handler.TelegramBotHandler;

public class BacklogCommand implements Command{
    private final TelegramBotHandler botHandler;

    public BacklogCommand(TelegramBotHandler botHandler) {
        this.botHandler = botHandler;
    }

    @Override
    public SendMessage execute(long chatId, String messageText, int userId) {
        return botHandler.sendBacklogMenu(chatId, userId);
    }
}
