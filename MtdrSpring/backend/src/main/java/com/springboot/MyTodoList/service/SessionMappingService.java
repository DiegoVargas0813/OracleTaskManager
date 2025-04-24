package com.springboot.MyTodoList.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.util.BotHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SessionMappingService {
    // Mapeo de distintos tipos de ID a IDs de base de datos
    // Long chatId -> Map<String, Map<String, Integer>>
    // String type -> Map<String, Integer> (shortId -> dbId)
    // String shortId -> Integer dbId
    private final Map<Long, SessionData> sessionMappings = new HashMap<>();
    private static final long EXPIRATION_TIME_MS = 1000 * 60 * 30; // Media hora (30 minutos)
    private final MessagingService messagingService;
    
    public SessionMappingService(MessagingService messagingService) {
        this.messagingService = messagingService;

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        // Schedule a task to clean up expired sessions every 10 minutes
        scheduler.scheduleAtFixedRate(this::cleanupExpiredSessions, 10L, 10L, TimeUnit.MINUTES);
    }

    /**
     * Store a mapping for a specific type (e.g., tasks, users, sprints) in a chat session.
     * @param chatId The chat ID for the session.
     * @param type The type of mapping (e.g., "tasks", "users", "sprints").
     * @param items A map of short IDs to database IDs.
     */
    public void storeMapping(long chatId, String type, Map<String, Integer> items) {
        sessionMappings.putIfAbsent(chatId, new SessionData());
        SessionData sessionData = sessionMappings.get(chatId);
        sessionData.getMappings().put(type, items);
        sessionData.updateLastAccessTime();
    }

    /**
     * Retrieve the database ID for a given session-specific ID and type.
     * @param chatId The chat ID for the session.
     * @param type The type of mapping (e.g., "tasks", "users", "sprints").
     * @param shortId The session-specific ID.
     * @return The original database ID, or null if not found.
     */
    public Integer getOriginalId(long chatId, String type, String shortId) {
        SessionData sessionData = sessionMappings.get(chatId);
        if (sessionData != null) {
            sessionData.updateLastAccessTime();
            Map<String, Integer> mapping = sessionData.getMappings().get(type);
            return (mapping != null) ? mapping.get(shortId) : null;
        }
        return null;
    }

    /**
     * Remove all mappings for a given chat session.
     * @param chatId The chat ID for the session.
     */
    public void cleanupSession(long chatId) {
        sessionMappings.remove(chatId);
    }

    /**
     * Periodically clean up expired sessions.
     */
    private void cleanupExpiredSessions() {
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<Long, SessionData>> iterator = sessionMappings.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, SessionData> entry = iterator.next();
            if (currentTime - entry.getValue().getLastAccessTime() > EXPIRATION_TIME_MS) {
                iterator.remove();

                long chatId = entry.getKey();
                // Notify the user about the expired session
                notifyUser(chatId);
            }
        }
    }

    /**
     * Generate a mapping of short IDs to database IDs.
     * @param items A map of database IDs to names (or other descriptive values).
     * @return A map of short IDs to database IDs.
     */
    public Map<String, Integer> generateMapping(Map<Integer, String> items) {
        Map<String, Integer> mapping = new HashMap<>();
        int index = 1;
        for (Map.Entry<Integer, String> entry : items.entrySet()) {
            mapping.put(String.valueOf(index++), entry.getKey());
        }
        return mapping;
    }

    private void notifyUser(long chatId) {
        // Logica para notificar al usuario de una sesion de mapeos expirada
        SendMessage message = BotHelper.createMessageRemoveKeyboard(chatId, BotMessages.SESSION_EXPIRED.getMessage());
        messagingService.sendMessage(message);
    }

     // Inner class to store session data and last access time
     private static class SessionData {
        private final Map<String, Map<String, Integer>> mappings = new HashMap<>();
        private long lastAccessTime;

        public SessionData() {
            updateLastAccessTime();
        }

        public Map<String, Map<String, Integer>> getMappings() {
            return mappings;
        }

        public long getLastAccessTime() {
            return lastAccessTime;
        }

        public void updateLastAccessTime() {
            this.lastAccessTime = System.currentTimeMillis();
        }
    }
}