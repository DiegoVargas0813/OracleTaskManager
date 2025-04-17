package com.springboot.MyTodoList.handler;

import com.springboot.MyTodoList.util.UserState;

import java.util.HashMap;
import java.util.Map;

public class StateHandlerRegistry {
    private final Map<UserState.Process, StateHandler> handlers = new HashMap<>();

    public void registerHandler(UserState.Process process, StateHandler handler) {
        handlers.put(process, handler);
    }

    public StateHandler getHandler(UserState.Process process) {
        return handlers.getOrDefault(process, null);
    }
}