package com.springboot.MyTodoList.handler;

import com.springboot.MyTodoList.service.TaskService;
import com.springboot.MyTodoList.util.BotLabels;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import com.springboot.MyTodoList.service.SprintService;

public class TelegramBotHandler {
    private final TaskService taskService;
    private final SprintService sprintService;

    public TelegramBotHandler(TaskService taskService, SprintService sprintService) {
        this.taskService = taskService;
        this.sprintService = sprintService;
    }

    // Add methods to handle Telegram bot commands and interactions here
    public SendMessage sendMainMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Main Menu:");
    
        // Create keyboard
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
    
        KeyboardRow row1 = new KeyboardRow();
        row1.add(BotLabels.LIST_ALL_TASKS.getLabel());
    
        KeyboardRow row2 = new KeyboardRow();
        row2.add(BotLabels.CREATE_NEW_TASK.getLabel());
    
        keyboard.add(row1);
        keyboard.add(row2);
    
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);
    
        return message;
/* 
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error(e.getLocalizedMessage(), e);
        } */
    }
}
