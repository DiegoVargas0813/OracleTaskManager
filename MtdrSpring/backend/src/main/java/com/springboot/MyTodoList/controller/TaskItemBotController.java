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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.service.TaskService;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.service.SprintService;

import com.springboot.MyTodoList.service.TaskCreationService;
import com.springboot.MyTodoList.service.TaskCompletionService;

import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.util.UserState;

import java.util.Map;

import io.swagger.models.Response;



//Nuestra clase TaskItemBotController extiende TelegramLongPollingBot para manejar las interacciones con el bot de Telegram
//y contiene métodos para enviar mensajes y manejar comandos de los usuarios.
public class TaskItemBotController extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(TaskItemBotController.class);
    private TaskCreationService taskCreationService;
    private TaskCompletionService taskCompletionService;
    private TaskService taskService;
    private SprintService sprintService;
    private String botName;


    private Map<Long, UserState> userStates = new HashMap<>();

    
    public TaskItemBotController(String botToken, String botName, TaskService taskService, SprintService sprintService) {
        super(botToken);
        logger.info("Bot Token: " + botToken);
        logger.info("Bot Name: " + botName);
        this.taskService = taskService;
        this.sprintService = sprintService;
        this.botName = botName;
        this.taskCreationService = new TaskCreationService(logger, taskService, sprintService);
        this.taskCompletionService = new TaskCompletionService(taskService);
    }

    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage() && update.getMessage().hasText()){
            //Recuperamos el texto del mensaje y el id del chat
            String messageTextFromTelegram = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            //Recuperamos si hay un estado de usuario
            UserState userState = userStates.getOrDefault(chatId, new UserState());
            userStates.put(chatId, userState);

            SendMessage message = new SendMessage();

            switch(userState.getCurrentProcess()) {
                case TASK_CREATION:
                    message = new SendMessage();
                    message = taskCreationService.handleTaskCreation(chatId, messageTextFromTelegram);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                    logger.error(e.getLocalizedMessage(), e);
                    }

                    if (message.getText().contains("Task saved successfully")) {
                        resetUserState(chatId);
                    }

                    break;
                case TASK_COMPLETION:
                    message = new SendMessage();
                    message = taskCompletionService.handleTaskCompletition(chatId, messageTextFromTelegram);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        logger.error(e.getLocalizedMessage(), e);
                    }

                    if (message.getText().contains("Task completion finished.")) {
                        resetUserState(chatId);
                    }
                    break;
                case OTHER_PROCESS:
                    // Manejar otros procesos aquí
                    break;
                case NONE:
                default:
                    // No hay proceso activo, continuar con el flujo normal
                    if(
                        messageTextFromTelegram.equals(BotCommands.START_COMMAND.getCommand())
                        ||  messageTextFromTelegram.equals(BotLabels.SHOW_MAIN_SCREEN.getLabel())) {
                        sendMainMenu(chatId);
                    } else if(messageTextFromTelegram.equals(BotLabels.LIST_ALL_TASKS.getLabel())){
                        sendListAllTasksMenu(chatId);
                    } else if(messageTextFromTelegram.equals(BotLabels.CREATE_NEW_TASK.getLabel())){
                        message = new SendMessage();
                        message = taskCreationService.startTaskCreation(chatId);
                        userState.setCurrentProcess(UserState.Process.TASK_CREATION);
         
                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            logger.error(e.getLocalizedMessage(), e);
                        }
                    } else if (messageTextFromTelegram.equals(BotLabels.BACKLOG.getLabel())){
                        //Recuperamos las tareas del backlog
                        sendBacklogMenu(chatId, 1);
                    } else if (messageTextFromTelegram.equals(BotLabels.SPRINT.getLabel())){
                        //Recuperamos los sprints activos
                        sendMessage(chatId, "Sprint activo: " + sprintService.getActiveSprints().get(0).getName());
                    }else if (messageTextFromTelegram.equals(BotLabels.CURRENT_SPRINT.getLabel())){
                        //Recuperamos las tareas del sprint activo
                        sendCurrentSprintMenu(chatId, 1);
                    } else if (messageTextFromTelegram.indexOf(BotLabels.START_TASK.getLabel()) != -1) {
                        String start = messageTextFromTelegram.split(BotLabels.DASH.getLabel())[0];
                        int taskId = Integer.parseInt(start);

                        sendStartTaskMessage(chatId, taskId);
                        sendListAllTasksMenu(chatId);
                    } else if (messageTextFromTelegram.indexOf(BotLabels.DONE.getLabel()) != -1) {
                        String done = messageTextFromTelegram.split(BotLabels.DASH.getLabel())[0];
                        int taskId = Integer.parseInt(done);

                        message = new SendMessage();
                        message = taskCompletionService.startTaskCompletionProcess(chatId, taskId);
                        userState.setCurrentProcess(UserState.Process.TASK_COMPLETION);

                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            logger.error(e.getLocalizedMessage(), e);
                        }
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
    }

    private void sendListAllTasksMenu(long chatId){
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

                
        try {
            execute(messageToTelegram);
        } catch (TelegramApiException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private void sendBacklogMenu(long chatId, int userId){
        //TODO: Cambiar el id de usuario por el id del usuario que ha iniciado sesion
        List<Task> tasks = taskService.getTasksByUserId(1);

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
/*             
            if(task.getSprint() != null){
                currentRow.add("Sprint: " + task.getSprint());
            } else {
                currentRow.add("Sprint: No sprint");
            }
             */
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

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private void sendCurrentSprintMenu(long chatId, int userId){
        //userId sera el id del usuario que ha iniciado sesion
        Sprint currentSprint = sprintService.getActiveSprints().get(0);
        List<Task> tasks = taskService.getTasksByUserIdAndSprintId(1, currentSprint.getId());

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow mainScreen = new KeyboardRow();
        mainScreen.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
        keyboard.add(mainScreen);

        //Iteramos sobre la lista de tareas y creamos un teclado para cada tarea
        for(Task task : tasks){
            KeyboardRow currentRow = new KeyboardRow();
            currentRow.add(task.getName());

            if(task.getStatus().equals("Not-started")){
                currentRow.add(task.getId() + BotLabels.DASH.getLabel() + BotLabels.START_TASK.getLabel());
                currentRow.add(task.getId() + BotLabels.DASH.getLabel() + BotLabels.DONE.getLabel());
            } else if (task.getStatus().equals("Started")){
                currentRow.add(task.getId() + BotLabels.DASH.getLabel() + BotLabels.DONE.getLabel());
            }

            keyboard.add(currentRow); 
        }

        //Setear teclado
        keyboardMarkup.setKeyboard(keyboard);

        //Crear mensaje
        SendMessage messageToTelegram = new SendMessage();
        messageToTelegram.setChatId(chatId);
        messageToTelegram.setText(BotLabels.LIST_ALL_TASKS.getLabel());
        messageToTelegram.setReplyMarkup(keyboardMarkup);

                
        try {
            execute(messageToTelegram);
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

    private void resetUserState(long chatId) {
        UserState userState = userStates.get(chatId);
        if (userState != null) {
            userState.setCurrentProcess(UserState.Process.NONE);
            userState.setProcessState(null);
        }
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
