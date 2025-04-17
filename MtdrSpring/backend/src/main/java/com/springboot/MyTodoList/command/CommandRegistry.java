package com.springboot.MyTodoList.command;

import java.util.Map;
import java.util.HashMap;

public class CommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();

    public void registerCommand(String commandText, Command command) {
        commands.put(commandText, command);
    }

    public Command getCommand(String commandText) {
        return commands.getOrDefault(commandText, null);
    }
}