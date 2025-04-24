package com.springboot.MyTodoList.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//  This class is responsible for the sending of messages that come from within other services
//  like sessionmapping expiration


public class MessagingService {
    private final TelegramLongPollingBot bot;

    public MessagingService(TelegramLongPollingBot bot) {
        this.bot = bot;
    }

    public void sendMessage(SendMessage message) {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            Logger logger = LoggerFactory.getLogger(MessagingService.class);
            logger.error("Error sending message: " + e.getMessage(), e);
        }
    }
}
