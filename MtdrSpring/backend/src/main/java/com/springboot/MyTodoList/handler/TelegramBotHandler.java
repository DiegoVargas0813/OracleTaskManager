package com.springboot.MyTodoList.handler;

import com.springboot.MyTodoList.service.TaskService;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;

import oracle.net.aso.c;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.service.UserService;
import com.springboot.MyTodoList.service.SessionMappingService;

public class TelegramBotHandler {
    private final TaskService taskService;
    private final SprintService sprintService;
    private final UserService userService;
    private final SessionMappingService sessionMappingService;

    public TelegramBotHandler(TaskService taskService, SprintService sprintService, UserService userService,SessionMappingService sessionMappingService) {
        this.taskService = taskService;
        this.sprintService = sprintService;
        this.userService = userService;
        this.sessionMappingService = sessionMappingService;
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
    }

    public SendMessage sendMainMenuManager(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Main Menu:");
    
        // Create keyboard
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
    
        KeyboardRow row1 = new KeyboardRow();
        row1.add(BotLabels.LIST_USERS.getLabel());
    
        KeyboardRow row2 = new KeyboardRow();
        row2.add(BotLabels.CREATE_NEW_TASK.getLabel());
    
        keyboard.add(row1);
        keyboard.add(row2);
    
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);
    
        return message;
    }

    public SendMessage sendListAllTasksMenu(long chatId){
        //Obtenemos la lista de tareas
        //List<Task> tasks = getTasksByUserId(1);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        //Boton de menu
        KeyboardRow mainScreen = new KeyboardRow();
        mainScreen.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
        keyboard.add(mainScreen);

        //Backlog
        KeyboardRow backlog = new KeyboardRow();
        backlog.add(BotLabels.BACKLOG.getLabel());
        keyboard.add(backlog);

        //Sprint activo
        KeyboardRow currentSprint = new KeyboardRow();
        currentSprint.add(BotLabels.CURRENT_SPRINT.getLabel());
        keyboard.add(currentSprint);

        //Setear teclado
        keyboardMarkup.setKeyboard(keyboard);

        //Crear mensaje
        SendMessage messageToTelegram = new SendMessage();
        messageToTelegram.setChatId(chatId);
        messageToTelegram.setText(BotLabels.LIST_ALL_TASKS.getLabel());
        messageToTelegram.setReplyMarkup(keyboardMarkup);

        return messageToTelegram;
    }

    public SendMessage sendListAllTasksManagerMenu(long chatId, int managerId){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        //Boton de menu
        KeyboardRow mainScreen = new KeyboardRow();
        mainScreen.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
        keyboard.add(mainScreen);

        List<User> managedUsers = userService.getUserByManagerId(managerId);
        Map<String, Integer> userIdMapping = new HashMap<>();
        int index = 1;
        for(User user : managedUsers){
            userIdMapping.put(String.valueOf(index++), user.getId());
        }

        // Store the mapping in the SessionMappingService
        sessionMappingService.storeMapping(chatId, "users", userIdMapping);
        
        for(Map.Entry<String, Integer> entry : userIdMapping.entrySet()) {
            String shortId = entry.getKey();
            User user = managedUsers.stream().filter(u -> u.getId() == entry.getValue()).findFirst().orElse(null);
            if(user != null){
                KeyboardRow currentRow = new KeyboardRow();
                currentRow.add(shortId + BotLabels.DASH.getLabel() + user.getName());
                currentRow.add(shortId + BotLabels.DASH.getLabel() + BotLabels.LIST_USER_TASKS.getLabel());
                currentRow.add(shortId + BotLabels.DASH.getLabel() + BotLabels.CHECK_KPIS.getLabel());
                keyboard.add(currentRow);
            }
        }

        keyboardMarkup.setKeyboard(keyboard);

        SendMessage messageToTelegram = new SendMessage();
        messageToTelegram.setText(BotLabels.LIST_ALL_TASKS.getLabel());
        messageToTelegram.setChatId(chatId);
        messageToTelegram.setReplyMarkup(keyboardMarkup);

        return messageToTelegram;
    }

    public SendMessage sendBacklogMenu(long chatId, int userId){
        List<Task> tasks = taskService.getTasksByUserId(userId);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(BotMessages.BACKLOG_NOTICE.getMessage());

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow mainScreen = new KeyboardRow();
        mainScreen.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
        keyboard.add(mainScreen);

        //Iteramos sobre la lista de tareas y creamos un teclado para cada tarea
        for(Task task : tasks){
            KeyboardRow currentRow = new KeyboardRow();
            currentRow.add(task.getId() + BotLabels.DASH.getLabel() + task.getName());
            
            
            if (task.getStatus() != null){
                currentRow.add("Status: " + task.getStatus());
            } else {
                currentRow.add("Status: No status");
            }

            keyboard.add(currentRow); 
        }

        //Setear teclado
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    public SendMessage sendCurrentSprintMenu(long chatId, int userId){
        //userId sera el id del usuario que ha iniciado sesion
        Sprint currentSprint = sprintService.getActiveSprintsByUserId(userId).get(0);
        List<Task> tasks = taskService.getTasksByUserIdAndSprintId(userId, currentSprint.getId());

        Map<String, Integer> taskIdMapping = new HashMap<>();
        int index = 1;
        for (Task task : tasks) {
            taskIdMapping.put(String.valueOf(index++), task.getId());
        }

        // Store the mapping in the SessionMappingService
        sessionMappingService.storeMapping(chatId, "tasks", taskIdMapping);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow mainScreen = new KeyboardRow();
        mainScreen.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
        keyboard.add(mainScreen);

        //Iteramos sobre la lista de tareas y creamos un teclado para cada tarea
        for (Map.Entry<String, Integer> entry : taskIdMapping.entrySet()) {
            String shortId = entry.getKey();
            Task task = tasks.stream().filter(t -> t.getId() == entry.getValue()).findFirst().orElse(null);
            if (task != null) {
                KeyboardRow currentRow = new KeyboardRow();
                currentRow.add(shortId + BotLabels.DASH.getLabel() + task.getName());
    
                if (task.getStatus().equals("Not-started")) {
                    currentRow.add(shortId + BotLabels.DASH.getLabel() + BotLabels.START_TASK.getLabel());
                    currentRow.add(shortId + BotLabels.DASH.getLabel() + BotLabels.DONE.getLabel());
                } else if (task.getStatus().equals("Started")) {
                    currentRow.add(shortId + BotLabels.DASH.getLabel() + BotLabels.DONE.getLabel());
                } else {
                    currentRow.add(BotLabels.IS_COMPLETED.getLabel());
                }
    
                keyboard.add(currentRow);
            }
        }

        //Setear teclado
        keyboardMarkup.setKeyboard(keyboard);

        //Crear mensaje
        SendMessage messageToTelegram = new SendMessage();
        messageToTelegram.setChatId(chatId);
        messageToTelegram.setText(BotLabels.LIST_ALL_TASKS.getLabel());
        messageToTelegram.setReplyMarkup(keyboardMarkup);

        return messageToTelegram;
    }

    public SendMessage sendStartTaskMessage(long chatId, int taskId) {
        Integer originalTaskId = sessionMappingService.getOriginalId(chatId, "tasks", String.valueOf(taskId));

        taskService.putTaskStatus(originalTaskId, "Started");
        
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Task #" + taskId + " started successfully.");

        return message;
    }
}
