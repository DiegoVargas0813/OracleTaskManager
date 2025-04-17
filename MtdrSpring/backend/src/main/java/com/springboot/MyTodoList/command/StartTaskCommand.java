package com.springboot.MyTodoList.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import com.springboot.MyTodoList.handler.TelegramBotHandler;
import com.springboot.MyTodoList.util.BotLabels;

public class StartTaskCommand implements Command {
    private final TelegramBotHandler botHandler;

    public StartTaskCommand(TelegramBotHandler botHandler) {
        this.botHandler = botHandler;
    }

    @Override
    public SendMessage execute(long chatId, String messageText, int userId) {
        int taskId = Integer.parseInt(messageText.split(BotLabels.DASH.getLabel())[0]);

        return botHandler.sendStartTaskMessage(chatId, taskId);
    }    
}
