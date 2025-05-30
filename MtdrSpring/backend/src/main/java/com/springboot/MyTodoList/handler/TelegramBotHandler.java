package com.springboot.MyTodoList.handler;

import com.springboot.MyTodoList.service.TaskService;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;

import oracle.net.aso.c;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.service.UserService;
import com.springboot.MyTodoList.service.SessionMappingService;

import com.springboot.MyTodoList.util.BotHelper;

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

        // Creamos las filas del teclado
        List<KeyboardRow> keyboard = new ArrayList<>();
    
        // Llamada a BotHelper para crear las filas del teclado
        KeyboardRow row1 = BotHelper.createKeyboardRow(BotLabels.LIST_ALL_TASKS.getLabel());
        KeyboardRow row2 = BotHelper.createKeyboardRow(BotLabels.CREATE_NEW_TASK.getLabel());
        keyboard.add(row1);
        keyboard.add(row2);

        ReplyKeyboardMarkup keyboardMarkup = BotHelper.createKeyboard(keyboard);

        SendMessage message = BotHelper.createMessage(chatId, BotLabels.SHOW_MAIN_SCREEN.getLabel(), keyboardMarkup);
    
        return message;
    }

    public SendMessage sendMainMenuManager(long chatId) {

        // Create keyboard
        List<KeyboardRow> keyboard = new ArrayList<>();
    
        // Llamada a BotHelper para crear las filas del teclado
        KeyboardRow row1 = BotHelper.createKeyboardRow(BotLabels.LIST_USERS.getLabel());
        KeyboardRow row2 = BotHelper.createKeyboardRow(BotLabels.CREATE_NEW_TASK.getLabel());
        keyboard.add(row1);
        keyboard.add(row2);
    
        ReplyKeyboardMarkup keyboardMarkup = BotHelper.createKeyboard(keyboard);
        SendMessage message = BotHelper.createMessage(chatId, BotLabels.SHOW_MAIN_SCREEN.getLabel(), keyboardMarkup);
    
        return message;
    }

    public SendMessage sendListAllTasksMenu(long chatId){
        // Preparacion del teclado
        List<KeyboardRow> keyboard = new ArrayList<>();

        //Boton de menu
        keyboard.add(BotHelper.createKeyboardRow(BotLabels.SHOW_MAIN_SCREEN.getLabel()));

        //Backlog
        keyboard.add(BotHelper.createKeyboardRow(BotLabels.BACKLOG.getLabel()));

        //Sprint activo
        keyboard.add(BotHelper.createKeyboardRow(BotLabels.CURRENT_SPRINT.getLabel()));

        ReplyKeyboardMarkup keyboardMarkup = BotHelper.createKeyboard(keyboard);
        SendMessage message = BotHelper.createMessage(chatId, BotLabels.LIST_ALL_TASKS.getLabel(), keyboardMarkup);

        return message;
    }

    public SendMessage sendListAllTasksManagerMenu(long chatId, int managerId){

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(BotHelper.createKeyboardRow(BotLabels.SHOW_MAIN_SCREEN.getLabel()));

        List<User> managedUsers = userService.getUserByManagerId(managerId);
        managedUsers.sort((u1, u2) -> Integer.compare(u1.getId(), u2.getId()));

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
                KeyboardRow currentRow = BotHelper.createKeyboardRow(
                    shortId + BotLabels.DASH.getLabel() + user.getName(),
                    shortId + BotLabels.DASH.getLabel() + BotLabels.LIST_USER_TASKS.getLabel(),
                    shortId + BotLabels.DASH.getLabel() + BotLabels.CHECK_KPIS.getLabel()

                );
                keyboard.add(currentRow);
            }
        }

        ReplyKeyboardMarkup keyboardMarkup = BotHelper.createKeyboard(keyboard);
        SendMessage message = BotHelper.createMessage(chatId, BotLabels.LIST_ALL_TASKS.getLabel(), keyboardMarkup);

        return message;
    }

    public SendMessage sendBacklogMenu(long chatId, int userId){
        List<Task> tasks = taskService.getTasksByUserId(userId);
        tasks.sort((t1, t2) -> Integer.compare(t1.getId(), t2.getId()));

        Map<String, Integer> taskIdMapping = new LinkedHashMap<>();
        int index = 1;
        for (Task task : tasks) {
            taskIdMapping.put(String.valueOf(index++), task.getId());
        }

        // Guardamos el mapeo
        sessionMappingService.storeMapping(chatId, "tasks", taskIdMapping);

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(BotHelper.createKeyboardRow(BotLabels.SHOW_MAIN_SCREEN.getLabel()));

        Map<Integer, Task> taskMap = tasks.stream()
            .collect(Collectors.toMap(Task::getId, task -> task));

        //Iteramos sobre la lista de tareas y creamos un teclado para cada tarea
        for(Map.Entry<String,Integer> entry : taskIdMapping.entrySet()){
            String shortId = entry.getKey();
            Task task = taskMap.get(entry.getValue());

            
            if (task != null) {
                KeyboardRow row = BotHelper.createKeyboardRow(
                    shortId + BotLabels.DASH.getLabel() + task.getName(),
                    "Status: " + (task.getStatus() != null ? task.getStatus() : "No status")
                );
                keyboard.add(row);
            }
        }

        ReplyKeyboardMarkup keyboardMarkup = BotHelper.createKeyboard(keyboard);
        SendMessage message = BotHelper.createMessage(chatId, BotMessages.BACKLOG_NOTICE.getMessage(), keyboardMarkup);

        return message;
    }

    public SendMessage sendCurrentSprintMenu(long chatId, int userId){
        //Recuperamos las tareas de un usuario en un sprint activo
        Sprint currentSprint = sprintService.getActiveSprintsByUserId(userId).get(0);
        List<Task> tasks = taskService.getTasksByUserIdAndSprintId(userId, currentSprint.getId());
        tasks.sort((t1, t2) -> Integer.compare(t1.getId(), t2.getId()));

        //Generamos un mapeo de IDs cortas
        Map<String, Integer> taskIdMapping = new LinkedHashMap<>();
        int index = 1;
        for (Task task : tasks) {
            taskIdMapping.put(String.valueOf(index++), task.getId());
        }

        // Guardamos en el mapping service
        sessionMappingService.storeMapping(chatId, "tasks", taskIdMapping);

        // Creamos el teclado
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(BotHelper.createKeyboardRow(BotLabels.SHOW_MAIN_SCREEN.getLabel()));

        // Mapa de tareas con IDs originales
        Map<Integer, Task> taskMap = tasks.stream()
            .collect(Collectors.toMap(Task::getId, task -> task));

        //Iteramos sobre la lista de tareas y creamos un teclado para cada tarea
        for (Map.Entry<String, Integer> entry : taskIdMapping.entrySet()) {
            String shortId = entry.getKey();
            Task task = taskMap.get(entry.getValue());
            
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

        // Create the keyboard markup and message
        ReplyKeyboardMarkup keyboardMarkup = BotHelper.createKeyboard(keyboard);
        SendMessage message = BotHelper.createMessage(chatId, BotLabels.LIST_ALL_TASKS.getLabel(), keyboardMarkup);

        return message;
    }

    public SendMessage sendStartTaskMessage(long chatId, int taskId) {
        Integer originalTaskId = sessionMappingService.getOriginalId(chatId, "tasks", String.valueOf(taskId));

        taskService.putTaskStatus(originalTaskId, "Started");
        
        SendMessage message = BotHelper.createMessage(chatId, "Task #" + taskId + " started successfully.");
        message = BotHelper.removeKeyboard(message);

        return message;
    }
}
