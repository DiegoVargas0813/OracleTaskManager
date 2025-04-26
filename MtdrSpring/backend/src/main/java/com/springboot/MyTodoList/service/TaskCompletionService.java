package com.springboot.MyTodoList.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.util.BotHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        TaskCompletionState taskCompletionState = new TaskCompletionState();

        taskCompletionState.setTaskId(taskId);

        userTaskCompletionStates.put(chatId, taskCompletionState);

        Optional<Task> taskOptional = taskService.getTaskById(taskId);
        SendMessage message;

        if (taskOptional.isPresent()) {
            message = BotHelper.createMessageRemoveKeyboard(chatId, BotMessages.ENTER_REAL_HOURS.getMessage());
            return message;
        } else {
            message = BotHelper.createMessage(chatId, BotMessages.TASK_NOT_FOUND.getMessage());
            return message;
        }
    }

    public SendMessage handleTaskCompletition(long chatId, String userInput){
        TaskCompletionState state = userTaskCompletionStates.get(chatId);
        SendMessage message;

        if (state == null){
            message = BotHelper.createMessage(chatId, BotMessages.UNKOWN_COMMAND.getMessage());
            return message;
        }
        // Obtener el paso actual del estado de la tarea
        TaskStep currentStep = state.getCurrentStep();

        // Manejar la entrada del usuario según el paso actual
        // Si el paso actual es REAL_HOURS, se espera que el usuario ingrese las horas reales
        // Si el paso actual es CONFIRMATION, se espera que el usuario confirme la tarea
        // Si el paso actual es COMPLETED, se muestra un mensaje de finalización
        switch (currentStep){
            case REAL_HOURS:
                try {
                    int realHours = Integer.parseInt(userInput);
                    state.setCurrentStep(TaskStep.CONFIRMATION);
 
                    List<KeyboardRow> keyboard = new ArrayList<>();

                    keyboard.add(BotHelper.createKeyboardRow(String.valueOf(realHours) + BotLabels.DASH.getLabel() + BotLabels.CONFIRM.getLabel()));                 

                    ReplyKeyboardMarkup keyboardMarkup = BotHelper.createKeyboard(keyboard);
                    message = BotHelper.createMessage(chatId, BotMessages.ENTER_CONFIRMATION.getMessage() + realHours, keyboardMarkup);

                    return message;

                } catch (NumberFormatException e) {
                    message = BotHelper.createMessage(chatId, BotMessages.ERROR_INVALID_NUMBER.getMessage());
                    return message;
                }
            case CONFIRMATION:
                if(userInput.indexOf(BotLabels.CONFIRM.getLabel()) != -1){
                    int taskId = state.getTaskId();
                    int realHours = Integer.parseInt(userInput.substring(0, userInput.indexOf(BotLabels.DASH.getLabel())));
                    
                    taskService.putTaskRealHours(taskId, realHours);
                    taskService.putTaskStatus(taskId, "Complete");
                    
                    state.setCurrentStep(TaskStep.COMPLETED);

                    message = BotHelper.createMessageRemoveKeyboard(chatId, BotMessages.FINISH_COMPLETION.getMessage());

                    return message;
                } else {
                    state.setCurrentStep(TaskStep.CANCELED);
                    message = BotHelper.createMessage(chatId, BotMessages.CANCEL_COMPLETION.getMessage());
                    return message;
                }
            case COMPLETED:
                message = BotHelper.createMessage(chatId, "Task completion finished.");
                return message;
            default:
                message = BotHelper.createMessage(chatId, BotMessages.UNKOWN_COMMAND.getMessage());
                return message;
        }
    }
}
