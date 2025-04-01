package com.springboot.MyTodoList.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;


import org.slf4j.Logger;

import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.service.TaskService;

public class TaskCreationService {
    private Logger logger;
    private TaskService taskService;
    private SprintService sprintService;

    public TaskCreationService(Logger logger, TaskService taskService, SprintService sprintService) {
        this.logger = logger;
        this.taskService = taskService;
        this.sprintService = sprintService;
    }
 
    // Pasos para la creación de tareas
    public enum TaskStep {
        NAME, DESCRIPTION, STORY_POINTS, ESTIMATED_HOURS, SPRINT, COMPLETED
    }

    // Estado de la creación de tareas
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
    // Mapa para almacenar el estado de creación de tareas por usuario
    private Map<Long, TaskCreationState> userTaskStates = new HashMap<>();

    //Utilidades
    public SendMessage startTaskCreation(long chatId) {
        userTaskStates.put(chatId, new TaskCreationState());
    
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Please enter the task name:");
    
        // Remove keyboard for free text input
        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
        keyboardRemove.setRemoveKeyboard(true);
        message.setReplyMarkup(keyboardRemove);
    
        return message;
    }

    public SendMessage handleTaskCreation(long chatId, String userInput) {
        TaskCreationState state = userTaskStates.get(chatId);
        SendMessage message = new SendMessage();
    
        if (state == null) {
            message = sendMessage(chatId, "No task creation in progress. Use /create to start.");
            return message;
        }
    
        Task task = state.getTask();
        TaskStep currentStep = state.getCurrentStep();
    
        switch (currentStep) {
            case NAME:
                task.setName(userInput);
                state.setCurrentStep(TaskStep.DESCRIPTION);
                message = sendMessage(chatId, "Now enter the task description:");
                return message;
    
            case DESCRIPTION:
                task.setDescription(userInput);
                state.setCurrentStep(TaskStep.STORY_POINTS);
                message = sendMessage(chatId, "Enter the story points (a number):");
                return message;
    
            case STORY_POINTS:
                try {
                    task.setStoryPoints(Integer.parseInt(userInput));
                    state.setCurrentStep(TaskStep.ESTIMATED_HOURS);
                    message = sendMessage(chatId, "Enter estimated hours (a number):");
                    return message;
                } catch (NumberFormatException e) {
                    message = sendMessage(chatId, "Invalid input. Please enter a valid number for story points.");
                    return message;
                }
    
            case ESTIMATED_HOURS:
                try {
                    task.setEstimatedHours(Integer.parseInt(userInput));
                    state.setCurrentStep(TaskStep.SPRINT);
                    
                    message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText("Select a sprint for the task:");

                    List<Sprint> activeSprints = sprintService.getActiveSprints();
                    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

                    List<KeyboardRow> keyboard = new ArrayList<>();

                    for (Sprint sprint : activeSprints) {
                        KeyboardRow row = new KeyboardRow();
                        row.add(String.valueOf(sprint.getId()) + ".-" + sprint.getName());
                        keyboard.add(row);
                    }

                    keyboardMarkup.setKeyboard(keyboard);

                    message.setReplyMarkup(keyboardMarkup);

                    return message;

                } catch (NumberFormatException e) {
                    message = sendMessage(chatId, "Invalid input. Please enter a valid number for estimated hours.");
                    return message;
                }


            case SPRINT:
                try {
                    int sprintId = Integer.parseInt(userInput.split("\\.")[0]);
                    Sprint selectedSprint = sprintService.getSprintById(sprintId).orElse(null);
    
                    if (selectedSprint != null) {
                        task.setSprint(selectedSprint);
                        state.setCurrentStep(TaskStep.COMPLETED);
                        message = saveTask(chatId, task);
                        return message;
                    } else {
                        message = sendMessage(chatId, "Invalid sprint selection. Please select a valid sprint.");
                        return message;
                    }
                } catch (NumberFormatException e) {
                    message = sendMessage(chatId, "Invalid input. Please enter a valid number for the sprint.");
                    return message;
                }
    
            case COMPLETED:
                message = sendMessage(chatId, "Task creation is already completed. Use the menu to create another.");
                return message;
            default:
                message = sendMessage(chatId, "Unknown step. Please start over.");
                return message;
        }
    }


    private SendMessage saveTask(long chatId, Task task) {
        // Simulate saving task to DB
        task.setStatus("Not-started");

        taskService.createTask(1,task);

        SendMessage message = sendMessage(chatId, "Task saved successfully! ✅");
    
        // Remove from tracking map
        userTaskStates.remove(chatId);

        return message;
    

        
        // Show main menu again
        //sendMainMenu(chatId);
    }
    

    private SendMessage sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        /*     
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
         */
        return message;
    }

}
