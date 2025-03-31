package com.springboot.MyTodoList.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.springboot.MyTodoList.controller.TaskItemBotController;
import com.springboot.MyTodoList.controller.TaskItemBotController.TaskCreationState;
import com.springboot.MyTodoList.controller.TaskItemBotController.TaskStep;
import com.springboot.MyTodoList.model.Task;

public class BotTaskCreator {
/*     private static final Logger logger = LoggerFactory.getLogger(TaskItemBotController.class);

    // Guarda el estado de creación de tareas para cada usuario
    // (identificado por su chatId)
    private Map<Long, TaskCreationState> userTaskStates = new HashMap<>();

    public class TaskCreationState {
        private TaskStep currentStep;
        private Task task;
    
        public TaskCreationState() {
            this.currentStep = TaskStep.NAME;
            this.task = new Task();
        }
    
        public TaskStep getCurrentStep() {
            return currentStep;
        }
    
        public void setCurrentStep(TaskStep currentStep) {
            this.currentStep = currentStep;
        }
    
        public Task getTask() {
            return task;
        }
    }

    private void startTaskCreation(long chatId) {
        userTaskStates.put(chatId, new TaskCreationState());
    
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Please enter the task name:");
    
        // Remove keyboard for free text input
        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
        keyboardRemove.setRemoveKeyboard(true);
        message.setReplyMarkup(keyboardRemove);
    
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private void handleTaskCreation(long chatId, String userInput) {
        TaskCreationState state = userTaskStates.get(chatId);
    
        if (state == null) {
            sendMessage(chatId, "No task creation in progress. Use /create to start.");
            return;
        }
    
        Task task = state.getTask();
        TaskStep currentStep = state.getCurrentStep();
    
        switch (currentStep) {
            case NAME:
                task.setName(userInput);
                state.setCurrentStep(TaskStep.DESCRIPTION);
                sendMessage(chatId, "Now enter the task description:");
                break;
    
            case DESCRIPTION:
                task.setDescription(userInput);
                state.setCurrentStep(TaskStep.STORY_POINTS);
                sendMessage(chatId, "Enter the story points (a number):");
                break;
    
            case STORY_POINTS:
                try {
                    task.setStoryPoints(Integer.parseInt(userInput));
                    state.setCurrentStep(TaskStep.ESTIMATED_HOURS);
                    sendMessage(chatId, "Enter estimated hours (a number):");
                } catch (NumberFormatException e) {
                    sendMessage(chatId, "Invalid input. Please enter a valid number for story points.");
                }
                break;
    
            case ESTIMATED_HOURS:
                try {
                    task.setEstimatedHours(Integer.parseInt(userInput));
                    state.setCurrentStep(TaskStep.COMPLETED);
                    saveTask(chatId, task);
                } catch (NumberFormatException e) {
                    sendMessage(chatId, "Invalid input. Please enter a valid number for estimated hours.");
                }
                break;
    
            case COMPLETED:
                sendMessage(chatId, "Task creation is already completed. Use the menu to create another.");
                break;
        }
    }

    
    private void saveTask(long chatId, Task task) {
        // Simulate saving task to DB
        System.out.println("Saving task: " + task);
    
        task.setStatus("Not-started");

        createTask(1, task);

        sendMessage(chatId, "Task saved successfully! ✅");
    
        // Remove from tracking map
        userTaskStates.remove(chatId);
    
        // Show main menu again
        sendMainMenu(chatId);
    }
    

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
    
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private void sendMainMenu(long chatId) {
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
    
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    } */
}
