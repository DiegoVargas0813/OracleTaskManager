package com.springboot.MyTodoList.util;

public enum BotLabels {
	
	SHOW_MAIN_SCREEN("Show Main Screen"), 
	HIDE_MAIN_SCREEN("Hide Main Screen"),
	LIST_ALL_ITEMS("List All Items"), 
	LIST_ALL_TASKS("List All Tasks"),
	CREATE_NEW_TASK("Create New Task"),
	ADD_NEW_ITEM("Add New Item"),
	START("START"),
	DONE("DONE"),
	UNDO("UNDO"),
	DELETE("DELETE"),
	MY_TODO_LIST("MY TODO LIST"),
	//Sprint labels
	BACKLOG("BACKLOG"),
	CURRENT_SPRINT("CURRENT SPRINT"),
	SPRINT("SPRINT"),
	DASH("-");





	private String label;

	BotLabels(String enumLabel) {
		this.label = enumLabel;
	}

	public String getLabel() {
		return label;
	}

}
