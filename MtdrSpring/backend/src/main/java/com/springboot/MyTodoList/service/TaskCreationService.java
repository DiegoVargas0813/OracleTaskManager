package com.springboot.MyTodoList.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;


import org.slf4j.Logger;

import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.service.TaskService;
import com.springboot.MyTodoList.util.UserState;

import com.springboot.MyTodoList.util.BotMessages;

public class TaskCreationService {
    private Logger logger;
    private TaskService taskService;
    private SprintService sprintService;
    private int userId;

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
    public SendMessage startTaskCreation(long chatId, int userId) {
        this.userId = userId;
        userTaskStates.put(chatId, new TaskCreationState());
    
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(BotMessages.ENTER_TASK_NAME.getMessage());
    
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
                message = sendMessage(chatId, BotMessages.ENTER_TASK_DESCRIPTION.getMessage());
                return message;
    
            case DESCRIPTION:
                task.setDescription(userInput);
                state.setCurrentStep(TaskStep.STORY_POINTS);
                message = sendMessage(chatId, BotMessages.ENTER_STORY_POINTS.getMessage());
                return message;
    
            case STORY_POINTS:
                try {
                    task.setStoryPoints(Integer.parseInt(userInput));
                    state.setCurrentStep(TaskStep.ESTIMATED_HOURS);
                    message = sendMessage(chatId, BotMessages.ENTER_ESTIMATED_HOURS.getMessage());
                    return message;
                } catch (NumberFormatException e) {
                    message = sendMessage(chatId, BotMessages.ERROR_INVALID_NUMBER.getMessage());
                    return message;
                }
    
            case ESTIMATED_HOURS:
                try {
                    task.setEstimatedHours(Integer.parseInt(userInput));
                    state.setCurrentStep(TaskStep.SPRINT);
                    
                    message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText(BotMessages.ENTER_SPRINT.getMessage());

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
                    message = sendMessage(chatId, BotMessages.ERROR_INVALID_NUMBER.getMessage());
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

        //Cambiar user id por una variable de constructor
        taskService.createTask(userId,task);

        SendMessage message = sendMessage(chatId, "Task saved successfully! ✅");
    
        // Remueve el teclado
        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
        keyboardRemove.setRemoveKeyboard(true);
        message.setReplyMarkup(keyboardRemove);

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
