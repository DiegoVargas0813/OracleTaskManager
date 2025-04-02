package com.springboot.MyTodoList.util;

public enum BotCommands {

	// Comandos basicos
	START_COMMAND("/start"), 
	HIDE_COMMAND("/hide"), 
	LOGOUT("/logout"),

	// Comandos de sprints
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
