package com.springboot.MyTodoList.util;

public enum BotMessages {
	// Saludo inicial
	HELLO_MYTODO_BOT(
	"Hi! I'm the Oracle Task Manager!\n Select any of the menu options to continue!:"),
	BOT_REGISTERED_STARTED("Bot registered and started succesfully!"),

	// Mensajes de login
	LOGIN_SUCCESS("Email verified successfully ✅. You can now use the bot. \nUse /start to see the main menu."),
	LOGIN_FAILURE("Hi! I'm the Oracle Task Manager! Before you start using me, please enter your email address to verify your identity."),
	LOGIN_INVALID_FORMAT("Invalid email format. Please enter a valid email."),
	
	LOGOUT_SUCCESS("Logout successful."),

	// Mensajes de creacion de tareas
	ENTER_TASK_ASSIGNEE("Please enter the task assignee: "),
	ENTER_TASK_NAME("Please enter the task name:"),
	ENTER_TASK_DESCRIPTION("Now enter the task description:"),
	ENTER_STORY_POINTS("Enter the story points (a number):"),
	ENTER_ESTIMATED_HOURS("Enter estimated hours (a number):"),
	ENTER_SPRINT("Confirm the sprint for the task:"),
	FINISH_TASK_CREATION("Task creation finished. Use /start to go back to the menu."),
	ERROR_INVALID_NUMBER("Invalid input. Please enter a valid number. If you are unsure about the hours, use the /cancel command."),

	//Mensaje de backlog
	BACKLOG_NOTICE("This menu is purely visual ❗, to modify current tasks please use the 'Current Sprint' button, the /currentsprint command or /listall to return to the previous menu."),

	// Mensajes de completado de tareas
	ENTER_REAL_HOURS("Enter the real hours spent on the task:"),
	ENTER_CONFIRMATION("WARNING ⚠️: Once a task is marked as completed, it cannot be undone. Press the button to confirm or /cancel to stop the creation.\nAny other input beyond the button will cancel the process. \nHours Spent: "),
	FINISH_COMPLETION("Task completion finished ✅. Use /start to go back to the main menu."),
	CANCEL_COMPLETION("An error occurred while marking the task as completed. Please try again."),

	TASK_NOT_FOUND("Task not found."),
	// Mensajes de error
	UNKOWN_COMMAND("Unknown command. Please use the /start command to access the main menu."),

	//Mensajes de sesion de mapeos
	SESSION_EXPIRED("The current session and its related listing have expired, please use the /start command to access the main menu."),;

	private String message;

	BotMessages(String enumMessage) {
		this.message = enumMessage;
	}

	public String getMessage() {
		return message;
	}

}
