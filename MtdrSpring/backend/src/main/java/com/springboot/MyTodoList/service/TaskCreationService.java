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
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.service.UserService;
import com.springboot.MyTodoList.util.UserState;
import com.springboot.MyTodoList.service.SessionMappingService;

import oracle.net.aso.b;

import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotHelper;

public class TaskCreationService {
    private Logger logger;
    private TaskService taskService;
    private SprintService sprintService;
    private UserService userService;
    private SessionMappingService sessionMappingService;
    private int userId;

    public TaskCreationService(Logger logger, TaskService taskService, SprintService sprintService, UserService userService, SessionMappingService sessionMappingService) {
        this.logger = logger;
        this.taskService = taskService;
        this.sprintService = sprintService;
        this.userService = userService;
        this.sessionMappingService = sessionMappingService;
    }
 
    // Pasos para la creación de tareas
    public enum TaskStep {
        ASSIGNEE,
        NAME, 
        DESCRIPTION, 
        STORY_POINTS, 
        ESTIMATED_HOURS, 
        SPRINT, 
        COMPLETED
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
    public SendMessage startTaskCreation(long chatId, int userId, boolean isManager) {
        this.userId = userId;
        userTaskStates.put(chatId, new TaskCreationState());
        TaskCreationState state = userTaskStates.get(chatId);
    
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        if(isManager) {
            message.setText(BotMessages.ENTER_TASK_ASSIGNEE.getMessage());
            state.setCurrentStep(TaskStep.ASSIGNEE);
    
            // Generate and store user mappings
            List<User> managedUsers = userService.getUserByManagerId(userId);
            Map<Integer, String> userMap = new HashMap<>();
            for (User user : managedUsers) {
                userMap.put(user.getId(), user.getName());
            }
            Map<String, Integer> userIdMapping = sessionMappingService.generateMapping(userMap);
            sessionMappingService.storeMapping(chatId, "users", userIdMapping);
    
            // Create keyboard for users
            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
            List<KeyboardRow> keyboard = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : userIdMapping.entrySet()) {
                KeyboardRow row = new KeyboardRow();
                row.add(entry.getKey() + BotLabels.DASH.getLabel() + userMap.get(entry.getValue()));
                keyboard.add(row);
            }
    
            keyboardMarkup.setKeyboard(keyboard);
            message.setReplyMarkup(keyboardMarkup);
    
            return message;
        } else {
            message.setText(BotMessages.ENTER_TASK_NAME.getMessage());
        }
       
    
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
            case ASSIGNEE:
                int assigneeId = sessionMappingService.getOriginalId(chatId, "users", userInput.split(BotLabels.DASH.getLabel())[0]);
                this.userId = assigneeId;
                state.setCurrentStep(TaskStep.NAME);
                message = sendMessage(chatId, BotMessages.ENTER_TASK_NAME.getMessage());
                return message;
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
                    int estimatedHours = Integer.parseInt(userInput);
                    if (estimatedHours > 4) {
                        message = sendMessage(chatId, "Estimated hours cannot exceed 4. Please enter a valid number (1-4).");
                        return message;
                    }
                    task.setEstimatedHours(estimatedHours);
                    state.setCurrentStep(TaskStep.SPRINT);
            
                    // Generate and store sprint mappings
                    List<Sprint> activeSprints = sprintService.getActiveSprintsByUserId(userId);
                    Map<Integer, String> sprintMap = new HashMap<>();
                    for (Sprint sprint : activeSprints) {
                        sprintMap.put(sprint.getId(), sprint.getName());
                    }
                    Map<String, Integer> sprintIdMapping = sessionMappingService.generateMapping(sprintMap);
                    sessionMappingService.storeMapping(chatId, "sprints", sprintIdMapping);
            
                    // Create keyboard for sprints
                    message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText(BotMessages.ENTER_SPRINT.getMessage());
            
                    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                    List<KeyboardRow> keyboard = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry : sprintIdMapping.entrySet()) {
                        KeyboardRow row = new KeyboardRow();
                        row.add(entry.getKey() + BotLabels.DASH.getLabel() + sprintMap.get(entry.getValue()));
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
                    String shortId = userInput.split(BotLabels.DASH.getLabel())[0];
                    Integer sprintId = sessionMappingService.getOriginalId(chatId, "sprints", shortId);
            
                    if (sprintId == null) {
                        message = sendMessage(chatId, "Invalid sprint selection. Please select a valid sprint.");
                        return message;
                    }
            
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
                    message = sendMessage(chatId, BotMessages.ERROR_INVALID_NUMBER.getMessage());
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

        SendMessage message = sendMessage(chatId, BotMessages.FINISH_TASK_CREATION.getMessage());
    
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
        return message;
    }
}
