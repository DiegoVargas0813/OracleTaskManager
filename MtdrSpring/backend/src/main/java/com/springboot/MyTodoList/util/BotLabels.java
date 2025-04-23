package com.springboot.MyTodoList.util;

public enum BotLabels {
	
	// Manejo de menu
	SHOW_MAIN_SCREEN("Show Main Screen"), 
	HIDE_MAIN_SCREEN("Hide Main Screen"),

	// Labels de menu principal
	LIST_ALL_TASKS("List All Tasks"),
	CREATE_NEW_TASK("Create New Task 📝"),

	//Labels de menu de manager
	LIST_USERS("Check Users"),
	LIST_USER_TASKS("List Tasks"),

	//Labeles de menu de tareas
	START_TASK("Mark as started"),
	DONE("Mark as done"),
	IS_COMPLETED("Task is complete"),
	CONFIRM("CONFIRM"),
	
	//Sprint labels
	BACKLOG("Backlog"),
	CURRENT_SPRINT("Current Sprint 🕑"),
	SPRINT("SPRINT"),

	// Labels de utilidad
	DASH("-"),
	SPACE(" "),

	// Labals de servicio de KPIs
	CHECK_KPIS("Check KPIs");




	private String label;

	BotLabels(String enumLabel) {
		this.label = enumLabel;
	}

	public String getLabel() {
		return label;
	}

}
