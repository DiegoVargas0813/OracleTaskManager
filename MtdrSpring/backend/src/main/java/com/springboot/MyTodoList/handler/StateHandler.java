package com.springboot.MyTodoList.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface StateHandler {
    SendMessage handle(long chatId, String messageText, Integer userId);
}