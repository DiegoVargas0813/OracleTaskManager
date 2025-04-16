package com.springboot.MyTodoList.service;

import java.util.HashMap;
import java.util.Map;

import com.springboot.MyTodoList.util.UserState;

public class UserStateService {
    private Map<Long, UserState> userStates = new HashMap<>();

    public UserState getUserState(long chatId) {
        return userStates.getOrDefault(chatId, new UserState());
    }

    public void updateUserState(long chatId, UserState userState) {
        userStates.put(chatId, userState);
    }

    public void resetUserState(long chatId) {
        UserState userState = userStates.get(chatId);
        if (userState != null) {
            userState.setCurrentProcess(UserState.Process.NONE);
            userState.setProcessState(null);
        }
    }
}
