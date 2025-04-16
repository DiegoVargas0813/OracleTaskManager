package com.springboot.MyTodoList.controller;

import java.security.Key;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.service.TaskService;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.service.UserService;

import com.springboot.MyTodoList.service.UserStateService;
import com.springboot.MyTodoList.service.TaskCreationService;
import com.springboot.MyTodoList.service.TaskCompletionService;

import com.springboot.MyTodoList.handler.TelegramBotHandler;

import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.util.UserState;

import java.util.Map;


//Nuestra clase TaskItemBotController extiende TelegramLongPollingBot para manejar las interacciones con el bot de Telegram
//y contiene métodos para enviar mensajes y manejar comandos de los usuarios.
public class TaskItemBotController extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(TaskItemBotController.class);

    // Servicios para manejo de multiples mensajes
    private TaskCreationService taskCreationService;
    private TaskCompletionService taskCompletionService;

    // Servicios de base de datos
    private TaskService taskService;
    private SprintService sprintService;
    private UserService userService;
    private UserStateService userStateService;

    // Handlers para el bot
    private TelegramBotHandler telegramBotHandler;

    // Variables para el bot
    private String botName;
    private Integer userId;

    // Variables para el manejo de estados de usuario
    
    public TaskItemBotController(String botToken, String botName, TaskService taskService, SprintService sprintService, UserService userService) {
        super(botToken);
        logger.info("Bot Token: " + botToken);
        logger.info("Bot Name: " + botName);
        this.taskService = taskService;
        this.sprintService = sprintService;
        this.userService = userService;
        this.botName = botName;
        this.taskCreationService = new TaskCreationService(logger, taskService, sprintService);
        this.taskCompletionService = new TaskCompletionService(taskService);
        this.userStateService = new UserStateService();
        this.telegramBotHandler = new TelegramBotHandler(taskService, sprintService);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            //Recuperamos el texto del mensaje y el id del chat
            String messageTextFromTelegram = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            //Recuperamos si hay un estado de usuario
            UserState userState = userStateService.getUserState(chatId);
            userStateService.updateUserState(chatId, userState);

            SendMessage message = new SendMessage();

            // Este switch case se encarga de controlar el flujo de la aplicacion
            // Hay ciertos comandos que requieren un flujo de mensajes continous.
            // En este caso, el flujo de mensajes es controlado por el estado del usuario
            // En el caso de no tener un estado, se ejecuta el flujo normal
            switch(userState.getCurrentProcess()) {
                case EMAIL_VERIFICATION:
                // Validate email and retrieve user ID
                if (isValidEmail(messageTextFromTelegram)) {
                    userId = userService.getUserIdByEmail(messageTextFromTelegram);
                    if (userId != null) {
                        userState.setCurrentProcess(UserState.Process.NONE);
                        userState.setProcessState(userId);
                        sendMessage(chatId, BotMessages.LOGIN_SUCCESS.getMessage());
                    } else {
                        sendMessage(chatId, BotMessages.LOGIN_FAILURE.getMessage());
                    }
                } else {
                    sendMessage(chatId, BotMessages.LOGIN_INVALID_FORMAT.getMessage());
                }
                break;

                case TASK_CREATION:
                    message = new SendMessage();

                    if (messageTextFromTelegram.equalsIgnoreCase(BotCommands.CANCEL.getCommand())) {
                        userStateService.resetUserState(chatId);
                        sendMessage(chatId, BotMessages.FINISH_TASK_CREATION.getMessage());
                        return;
                    }

                    message = taskCreationService.handleTaskCreation(chatId, messageTextFromTelegram);

                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                    logger.error(e.getLocalizedMessage(), e);
                    }

                    if (message.getText().contains(BotMessages.FINISH_TASK_CREATION.getMessage())) {
                        userStateService.resetUserState(chatId);
                        message = telegramBotHandler.sendCurrentSprintMenu(chatId, userId);
                        trySendMessage(message);
                    }

                    break;
                case TASK_COMPLETION:
                    message = new SendMessage();

                    if (messageTextFromTelegram.equalsIgnoreCase(BotCommands.CANCEL.getCommand())) {
                        userStateService.resetUserState(chatId);
                        sendMessage(chatId, BotMessages.FINISH_COMPLETION.getMessage());
                        return;
                    }

                    message = taskCompletionService.handleTaskCompletition(chatId, messageTextFromTelegram);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        logger.error(e.getLocalizedMessage(), e);
                    }

                    if (message.getText().contains(BotMessages.FINISH_COMPLETION.getMessage())) {
                        userStateService.resetUserState(chatId);
                        message = telegramBotHandler.sendCurrentSprintMenu(chatId, userId);
                        trySendMessage(message);
                    }
                    break;
                case OTHER_PROCESS:
                    // Manejar otros procesos aquí
                    break;
                case NONE:
                default:
                    // No hay proceso especial activo, manejar comandos normales
                    if(
                        messageTextFromTelegram.equals(BotCommands.START_COMMAND.getCommand())
                        ||  messageTextFromTelegram.equals(BotLabels.SHOW_MAIN_SCREEN.getLabel())) {
                        
                        message = telegramBotHandler.sendMainMenu(chatId);
                        trySendMessage(message);
                    } else if(
                        messageTextFromTelegram.equals(BotLabels.LIST_ALL_TASKS.getLabel())
                        || messageTextFromTelegram.equals(BotCommands.LIST_ALL.getCommand())) {
                        message = telegramBotHandler.sendListAllTasksMenu(chatId);
                        trySendMessage(message);
                    } else if(
                        messageTextFromTelegram.equals(BotLabels.CREATE_NEW_TASK.getLabel())
                        || messageTextFromTelegram.equals(BotCommands.CREATE_TASK.getCommand())) {
                        message = new SendMessage();
                        message = taskCreationService.startTaskCreation(chatId, userId);
                        userState.setCurrentProcess(UserState.Process.TASK_CREATION);
         
                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            logger.error(e.getLocalizedMessage(), e);
                        }
                    } else if (messageTextFromTelegram.equals(BotLabels.BACKLOG.getLabel())){
                        //Recuperamos las tareas del backlog
                        message = telegramBotHandler.sendBacklogMenu(chatId, userId);
                        trySendMessage(message);
                    } else if (messageTextFromTelegram.equals(BotLabels.SPRINT.getLabel())){
                        //Recuperamos los sprints activos
                        sendMessage(chatId, "Active Sprint: " + sprintService.getActiveSprints().get(0).getName());
                    }else if (
                        messageTextFromTelegram.equals(BotLabels.CURRENT_SPRINT.getLabel())
                        || messageTextFromTelegram.equals(BotCommands.CURRENT_SPRINT.getCommand())
                    ){
                        //Recuperamos las tareas del sprint activo
                        message = telegramBotHandler.sendCurrentSprintMenu(chatId, userId);
                        trySendMessage(message);
                    } else if (messageTextFromTelegram.indexOf(BotLabels.START_TASK.getLabel()) != -1) {
                        String start = messageTextFromTelegram.split(BotLabels.DASH.getLabel())[0];
                        int taskId = Integer.parseInt(start);

                        sendStartTaskMessage(chatId, taskId);
                        message = telegramBotHandler.sendListAllTasksMenu(chatId);
                        trySendMessage(message);
                    } else if (messageTextFromTelegram.indexOf(BotLabels.DONE.getLabel()) != -1) {
                        String done = messageTextFromTelegram.split(BotLabels.DASH.getLabel())[0];
                        int taskId = Integer.parseInt(done);

                        message = new SendMessage();
                        message = taskCompletionService.startTaskCompletionProcess(chatId, taskId, userId);
                        userState.setCurrentProcess(UserState.Process.TASK_COMPLETION);

                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            logger.error(e.getLocalizedMessage(), e);
                        }
                    } else if(messageTextFromTelegram.equals(BotCommands.LOGOUT.getCommand())){
                        userState.setCurrentProcess(UserState.Process.EMAIL_VERIFICATION);
                        userState.setProcessState(null);
                        userId = null;
                        sendMessage(chatId, BotMessages.LOGOUT_SUCCESS.getMessage());
                    } else {
                        sendMessage(chatId, BotMessages.UNKOWN_COMMAND.getMessage());
                    }
            }

            
        }
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
    
    private void trySendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }


    private void sendStartTaskMessage(long chatId, int taskId) {
        taskService.putTaskStatus(taskId, "Started");
        
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Task #" + taskId + " started successfully.");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    //Llamadas a repository
	@Override
	public String getBotUsername() {		
		return botName;
	}

    //Gets
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    public List<Task> getTasksByUserId(int userId) {
        return taskService.getTasksByUserId(userId);
    }

    public List<Sprint> getActiveSprints() {
        return sprintService.getActiveSprints();
    }

    public Sprint getSprintById(int id) {
        return sprintService.getSprintById(id).orElse(null);
    }


    //Posts
    public Task createTask(int userId, Task task) {
        return taskService.createTask(userId, task);
    }

    //Puts
    public void assignTaskToSprint(int id, int sprintId) {
        taskService.assignTaskToSprint(id, sprintId);
    }
}
