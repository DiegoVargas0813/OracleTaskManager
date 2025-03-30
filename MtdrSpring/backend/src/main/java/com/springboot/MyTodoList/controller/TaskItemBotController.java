package com.springboot.MyTodoList.controller;

import java.security.Key;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;


import java.util.Map;

import io.swagger.models.Response;

//Nuestra clase TaskItemBotController extiende TelegramLongPollingBot para manejar las interacciones con el bot de Telegram
//y contiene métodos para enviar mensajes y manejar comandos de los usuarios.
public class TaskItemBotController extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(TaskItemBotController.class);
    private TaskService taskService;
    private String botName;
    
    public enum TaskStep {
        NAME, DESCRIPTION, STORY_POINTS, ESTIMATED_HOURS, COMPLETED
    }
    
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

    private Map<Long, TaskCreationState> userTaskStates = new HashMap<>();

    


    public TaskItemBotController(String botToken, String botName, TaskService taskService) {
        super(botToken);
        logger.info("Bot Token: " + botToken);
        logger.info("Bot Name: " + botName);
        this.taskService = taskService;
        this.botName = botName;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage() && update.getMessage().hasText()){

            String messageTextFromTelegram = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if(
                messageTextFromTelegram.equals(BotCommands.START_COMMAND.getCommand())
            ||  messageTextFromTelegram.equals(BotLabels.SHOW_MAIN_SCREEN.getLabel())) {
                
                SendMessage messageToTelegram = new SendMessage();

                messageToTelegram.setChatId(chatId);

                messageToTelegram.setText(BotMessages.HELLO_MYTODO_BOT.getMessage());

                //Crear teclado
                ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                List<KeyboardRow> keyboard = new ArrayList<>();

                KeyboardRow row = new KeyboardRow();
                row.add(BotLabels.LIST_ALL_TASKS.getLabel());
                keyboard.add(row);

                row = new KeyboardRow();
                row.add(BotLabels.CREATE_NEW_TASK.getLabel());
                keyboard.add(row);
                

                //Setear teclado
                keyboardMarkup.setKeyboard(keyboard);

                //Agregar teclado al mensaje
                messageToTelegram.setReplyMarkup(keyboardMarkup);

                //Enviar mensaje
                try {
                    execute(messageToTelegram);
                } catch (TelegramApiException e) {
                    logger.error(e.getLocalizedMessage(), e);
                }
            } else if(messageTextFromTelegram.equals(BotLabels.LIST_ALL_TASKS.getLabel())){
                //Obtenemos la lista de tareas
                List<Task> tasks = getTasksByUserId(1);
                ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                List<KeyboardRow> keyboard = new ArrayList<>();

                //Iteramos sobre la lista de tareas y creamos un teclado para cada tarea
                for(Task task : tasks){
                    KeyboardRow currentRow = new KeyboardRow();
                    currentRow.add(task.getName());
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
            } else if(messageTextFromTelegram.equals(BotLabels.CREATE_NEW_TASK.getLabel())){
                startTaskCreation(chatId);
            } else {
                handleTaskCreation(chatId, messageTextFromTelegram);
            }
        }
    }


    //Utilidades
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

    //Posts
    public Task createTask(int userId, Task task) {
        return taskService.createTask(userId, task);
    }

    //Puts
    public void assignTaskToSprint(int id, int sprintId) {
        taskService.assignTaskToSprint(id, sprintId);
    }
}
