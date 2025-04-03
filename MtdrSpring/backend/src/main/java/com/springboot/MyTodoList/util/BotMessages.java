package com.springboot.MyTodoList.util;

public enum BotMessages {
	// Saludo inicial
	HELLO_MYTODO_BOT(
	"Hi! I'm the Oracle Task Manager!\n Select any of the menu options to continue!:"),
	BOT_REGISTERED_STARTED("Bot registered and started succesfully!"),

	// Mensajes obsoletos
	ITEM_DONE("Item done! Select /todolist to return to the list of todo items, or /start to go to the main screen."), 
	ITEM_UNDONE("Item undone! Select /todolist to return to the list of todo items, or /start to go to the main screen."), 
	ITEM_DELETED("Item deleted! Select /todolist to return to the list of todo items, or /start to go to the main screen."),
	TYPE_NEW_TODO_ITEM("Type a new todo item below and press the send button (blue arrow) on the rigth-hand side."),
	NEW_ITEM_ADDED("New item added! Select /todolist to return to the list of todo items, or /start to go to the main screen."),
	BYE("Bye! Select /start to resume!"),
	TYPE_NEW_TASK("Escribe la nueva tarea a crear y presiona el boton de enviar (flecha azul) en la parte derecha."),

	// Mensajes de login
	LOGIN_SUCCESS("Email verified successfully. You can now use the bot."),
	LOGIN_FAILURE("Email not found. Please try again."),
	LOGIN_INVALID_FORMAT("Invalid email format. Please enter a valid email."),
	
	LOGOUT_SUCCESS("Logout successful."),

	// Mensajes de creacion de tareas
	ENTER_TASK_NAME("Please enter the task name:"),
	ENTER_TASK_DESCRIPTION("Now enter the task description:"),
	ENTER_STORY_POINTS("Enter the story points (a number):"),
	ENTER_ESTIMATED_HOURS("Enter estimated hours (a number):"),
	ENTER_SPRINT("Confirm the sprint for the task:"),
	FINISH_TASK_CREATION("Task creation finished. Use /listall to see all tasks."),
	ERROR_INVALID_NUMBER("Invalid input. Please enter a valid number."),

	//Mensaje de backlog
	BACKLOG_NOTICE("This menu is purely visual, to modify current tasks please use the 'Current Sprint' button, the /currentsprint command or /listall to return to the previous menu."),

	// Mensajes de completado de tareas
	ENTER_REAL_HOURS("Enter the real hours spent on the task:"),
	ENTER_CONFIRMATION("Once a task is marked as completed, it cannot be undone. Press the button to confirm \nHours Spent: "),
	FINISH_COMPLETION("Task completion finished. Use /listall to see all tasks."),
	CANCEL_COMPLETION("An error occurred while marking the task as completed. Please try again."),

	// Mensajes de error
	UNKOWN_COMMAND("Unknown command. Please use the /start command.");

	private String message;

	BotMessages(String enumMessage) {
		this.message = enumMessage;
	}

	public String getMessage() {
		return message;
	}

}
