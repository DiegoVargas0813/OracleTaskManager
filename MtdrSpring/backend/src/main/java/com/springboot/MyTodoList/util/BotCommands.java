package com.springboot.MyTodoList.util;

public enum BotCommands {

	// Comandos basicos
	START_COMMAND("/start"), 
	HIDE_COMMAND("/hide"), 
	LOGOUT("/logout"),

	CANCEL("/cancel"),

	// Comandos de tareas
	CREATE_TASK("/createtask"),
	LIST_ALL("/listall"),
	CURRENT_SPRINT("/currentsprint"),

	//Obsoletos
	TODO_LIST("/todolist"),
	ADD_ITEM("/additem");

	private String command;

	BotCommands(String enumCommand) {
		this.command = enumCommand;
	}

	public String getCommand() {
		return command;
	}
}
