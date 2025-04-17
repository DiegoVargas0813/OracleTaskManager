package com.springboot.MyTodoList.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.service.TaskCreationService.TaskStep;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.service.TaskService;

import java.util.Optional;

public class TaskCompletionService {
    private TaskService taskService;    
    private int userId;

    // Constructor
    public TaskCompletionService(TaskService taskService) {
        this.taskService = taskService;
    }

    // Pasos para marcar una tarea como completada
    public enum TaskStep {
        REAL_HOURS,CONFIRMATION, COMPLETED, CANCELED
    }

    // Estado de la creación de tareas
    public class TaskCompletionState {
        private TaskStep currentStep;
        private Task task;
        private int taskId;
    
        public TaskCompletionState() {
            this.currentStep = TaskStep.REAL_HOURS;
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

        public void setTask(Task task) {
            this.task = task;
        }

        public int getTaskId() {
            return taskId;
        }

        public void setTaskId(int taskId) {
            this.taskId = taskId;
        }
    }

    // Mapa para almacenar el estado de cada usuario
    private Map<Long, TaskCompletionState> userTaskCompletionStates = new HashMap<>();

    // Método para iniciar el proceso de creación de tareas
    public SendMessage startTaskCompletionProcess(long chatId, int taskId, int userId) {
        this.userId = userId;
        System.out.println("Task ID in service start: " + taskId);

        TaskCompletionState taskCompletionState = new TaskCompletionState();

        taskCompletionState.setTaskId(taskId);

        userTaskCompletionStates.put(chatId, taskCompletionState);

        Optional<Task> taskOptional = taskService.getTaskById(taskId);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        if (taskOptional.isPresent()) {
            message.setText(BotMessages.ENTER_REAL_HOURS.getMessage());

            //Removemos el teclado 
            ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
            keyboardRemove.setRemoveKeyboard(true);
            message.setReplyMarkup(keyboardRemove);

            return message;
        } else {
            message.setChatId(String.valueOf(chatId));
            message.setText("Task not found.");

            return message;
        }
    }

    public SendMessage handleTaskCompletition(long chatId, String userInput){
        TaskCompletionState state = userTaskCompletionStates.get(chatId);
        SendMessage message = new SendMessage();

        if (state == null){
            sendMessage(chatId, BotMessages.UNKOWN_COMMAND.getMessage());
            return message;
        }

        Task task = state.getTask();
        System.out.println("Task ID in service handle: " + state.getTaskId());
        TaskStep currentStep = state.getCurrentStep();

        switch (currentStep){
            case REAL_HOURS:
                try {
                    int realHours = Integer.parseInt(userInput);
                    state.setCurrentStep(TaskStep.CONFIRMATION);
                    message = sendMessage(chatId, BotMessages.ENTER_CONFIRMATION.getMessage() + realHours);
 
                    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                    List<KeyboardRow> keyboard = new ArrayList<>();
                    KeyboardRow row = new KeyboardRow();
                    row.add(String.valueOf(realHours) + BotLabels.DASH.getLabel() + BotLabels.CONFIRM.getLabel());
                    keyboard.add(row);

                    keyboardMarkup.setKeyboard(keyboard);
                   
                    message.setReplyMarkup(keyboardMarkup);

                    return message;
                } catch (NumberFormatException e) {
                    message = sendMessage(chatId, BotMessages.ERROR_INVALID_NUMBER.getMessage());
                }
                return message;
            case CONFIRMATION:
                if(userInput.indexOf(BotLabels.CONFIRM.getLabel()) != -1){
                    int taskId = state.getTaskId();
                    System.out.println("Task ID in service confirmation: " + taskId);
                    int realHours = Integer.parseInt(userInput.substring(0, userInput.indexOf(BotLabels.DASH.getLabel())));
                    taskService.putTaskRealHours(taskId, realHours);
                    taskService.putTaskStatus(taskId, "Complete");
                    state.setCurrentStep(TaskStep.COMPLETED);

                    message = sendMessage(chatId, BotMessages.FINISH_COMPLETION.getMessage());

                    //Removemos el teclado
                    ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
                    keyboardRemove.setRemoveKeyboard(true);
                    message.setReplyMarkup(keyboardRemove);

                    return message;
                } else {
                    state.setCurrentStep(TaskStep.CANCELED);
                    message = sendMessage(chatId, BotMessages.CANCEL_COMPLETION.getMessage());
                    return message;
                }
            case COMPLETED:
                message = sendMessage(chatId, "Task completion finished.");

            default:
                message = sendMessage(chatId, BotMessages.UNKOWN_COMMAND.getMessage());
                return message;
        }
    }

    private SendMessage sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        return message;
    }
}
