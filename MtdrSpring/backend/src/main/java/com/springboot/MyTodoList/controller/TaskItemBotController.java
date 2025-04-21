package com.springboot.MyTodoList.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.service.TaskService;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.service.UserService;
import com.springboot.MyTodoList.service.ManagerService;
import com.springboot.MyTodoList.service.SessionMappingService;
import com.springboot.MyTodoList.service.UserStateService;
import com.springboot.MyTodoList.service.TaskCreationService;
import com.springboot.MyTodoList.service.TaskCompletionService;

import com.springboot.MyTodoList.handler.*;
import com.springboot.MyTodoList.command.*;

import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.util.UserState;


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
    private ManagerService managerService;
    private UserStateService userStateService;

    // Handlers para el bot
    private TelegramBotHandler telegramBotHandler;

    // Variables para el bot
    private String botName;
    private Integer userId;

    // Variables para el manejo de estados de usuario
    private CommandRegistry commandRegistry;
    private StateHandlerRegistry stateHandlerRegistry;

    // Variables para manejar IDs de base de datos a IDs mas cortos.
    private SessionMappingService sessionMappingService;

    
    public TaskItemBotController(String botToken, String botName, TaskService taskService, SprintService sprintService, UserService userService, ManagerService managerService) {
        super(botToken);
        logger.info("Bot Token: " + botToken);
        logger.info("Bot Name: " + botName);
        this.taskService = taskService;
        this.sprintService = sprintService;
        this.userService = userService;
        this.managerService = managerService;
        this.botName = botName;
        this.taskCompletionService = new TaskCompletionService(taskService);
        this.userStateService = new UserStateService();
        this.sessionMappingService = new SessionMappingService();
        this.telegramBotHandler = new TelegramBotHandler(taskService, sprintService, userService, sessionMappingService);
        this.taskCreationService = new TaskCreationService(logger, taskService, sprintService, userService, sessionMappingService);

        //Creamos los distintos command registry.
        //Esta clase se encarga de registrar los comandos y sus respectivas clases que manejan la logica de cada comando.

        //Command registry
        this.commandRegistry = new CommandRegistry();
        commandRegistry.registerCommand(BotCommands.START_COMMAND.getCommand(), new StartCommand(telegramBotHandler));
        commandRegistry.registerCommand(BotLabels.SHOW_MAIN_SCREEN.getLabel(), new StartCommand(telegramBotHandler));

        //List All
        commandRegistry.registerCommand(BotCommands.LIST_ALL.getCommand(), new ListAllCommand(telegramBotHandler));
        commandRegistry.registerCommand(BotLabels.LIST_ALL_TASKS.getLabel(), new ListAllCommand(telegramBotHandler));

        //Create Task
        commandRegistry.registerCommand(BotCommands.CREATE_TASK.getCommand(), new CreateTaskCommand(taskCreationService, userStateService));
        commandRegistry.registerCommand(BotLabels.CREATE_NEW_TASK.getLabel(), new CreateTaskCommand(taskCreationService, userStateService));

        //Current Sprint
        commandRegistry.registerCommand(BotCommands.CURRENT_SPRINT.getCommand(), new CurrentSprintCommand(telegramBotHandler));
        commandRegistry.registerCommand(BotLabels.CURRENT_SPRINT.getLabel(), new CurrentSprintCommand(telegramBotHandler));

        //Backlog
        commandRegistry.registerCommand(BotLabels.BACKLOG.getLabel(), new BacklogCommand(telegramBotHandler));
        //commandRegistry.registerCommand(BotCommands.BACKLOG.getCommand(), new BacklogCommand(telegramBotHandler));

        //Mark task as Started
        commandRegistry.registerCommand(BotLabels.START_TASK.getLabel(), new StartTaskCommand(telegramBotHandler));

        //Mark task as Done
        commandRegistry.registerCommand(BotLabels.DONE.getLabel(), new CompleteTaskCommand(taskCompletionService, userStateService, sessionMappingService));

        //Logout
        commandRegistry.registerCommand(BotCommands.LOGOUT.getCommand(), new LogoutCommand(userStateService));

        //State handler registry
        this.stateHandlerRegistry = new StateHandlerRegistry();

        //Email verification state
        stateHandlerRegistry.registerHandler(UserState.Process.EMAIL_VERIFICATION, new EmailVerificationState(userService, userStateService, managerService));

        //Task creation state
        stateHandlerRegistry.registerHandler(UserState.Process.TASK_CREATION, new TaskCreationState(taskCreationService, userStateService));

        //Task completion state
        stateHandlerRegistry.registerHandler(UserState.Process.TASK_COMPLETION, new TaskCompletionState(taskCompletionService, userStateService));

    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            //Recuperamos el texto del mensaje y el id del chat
            String messageTextFromTelegram = update.getMessage().getText();
            String filteredCommand = null;
            long chatId = update.getMessage().getChatId();
            SendMessage message = new SendMessage();

            //Recuperamos si hay un estado de usuario
            UserState userState = userStateService.getUserState(chatId);
            userStateService.updateUserState(chatId, userState);
            StateHandler handler = stateHandlerRegistry.getHandler(userState.getCurrentProcess());


            if (handler != null) {
                System.out.println("Proceso encontrado!");
                message = handler.handle(chatId, messageTextFromTelegram, userId);
                trySendMessage(message);

                // If the user ID is set during email verification, update the controller's userId
                if (userState.getCurrentProcess() == UserState.Process.NONE && userState.getProcessState() != null) {
                    userId = (Integer) userState.getProcessState();
                }
            } else {
                // Si no hay un comando que involucre estados, ejecutamos comandos normales
                // Verficiamos si el comando es un mensaje compusto por un argumento y un comando. EJ: 12-DASH-START
                // Si el mensaje contiene un guion, lo separamos en dos partes
                if(messageTextFromTelegram.indexOf(BotLabels.DASH.getLabel()) != -1){
                    filteredCommand = messageTextFromTelegram.split(BotLabels.DASH.getLabel())[1];
                } else {
                    // Si no es un comando compuesto, lo dejamos como esta
                    filteredCommand = messageTextFromTelegram;
                }
                
                Command command = commandRegistry.getCommand(filteredCommand);

                // Ahora que determinamos el tipo de comando, delegamos la logica a la clase correspondiente
                // Por ejemplo, START y DONE deben partir el comando en la taskId y el comando
                // y luego ejecutar la logica correspondiente
                if(command != null) {
                    message = command.execute(chatId, messageTextFromTelegram, userId);
                    trySendMessage(message);
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

    //Llamadas a repository
	@Override
	public String getBotUsername() {		
		return botName;
	}
}
