package com.springboot.MyTodoList.command;

import com.springboot.MyTodoList.service.KPIService;
import com.springboot.MyTodoList.service.SessionMappingService;
import com.springboot.MyTodoList.util.BotLabels;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class CheckKPIManagerCommand implements Command {
    private final KPIService kpiService;
    private final SessionMappingService sessionMappingService;

    public CheckKPIManagerCommand(KPIService kpiService, SessionMappingService sessionMappingService) {
        this.kpiService = kpiService;
        this.sessionMappingService = sessionMappingService;
    }

    @Override
    public SendMessage execute(long chatId, String messageText, int userId) {
        String shortId = messageText.split(BotLabels.DASH.getLabel())[0];
        Integer userDBId = sessionMappingService.getOriginalId(chatId, "users", shortId);


        String kpiReport = kpiService.calculateKPIsForUser(userDBId);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(kpiReport);

        return message;
    }
}