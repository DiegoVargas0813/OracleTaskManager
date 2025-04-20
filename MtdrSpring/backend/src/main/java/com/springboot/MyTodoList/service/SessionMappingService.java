package com.springboot.MyTodoList.service;

import java.util.HashMap;
import java.util.Map;

public class SessionMappingService {
    // Map to store session-specific mappings for each chat
    private final Map<Long, Map<String, Integer>> sessionMappings = new HashMap<>();

    /**
     * Generate session-specific IDs for a list of database IDs.
     * @param chatId The chat ID for the session.
     * @param items A map of short IDs to database IDs.
     */
    public void storeMapping(long chatId, Map<String, Integer> items) {
        sessionMappings.put(chatId, items);
    }

    /**
     * Retrieve the database ID for a given session-specific ID.
     * @param chatId The chat ID for the session.
     * @param shortId The session-specific ID.
     * @return The original database ID, or null if not found.
     */
    public Integer getOriginalId(long chatId, String shortId) {
        Map<String, Integer> mapping = sessionMappings.get(chatId);
        return (mapping != null) ? mapping.get(shortId) : null;
    }

    /**
     * Remove all mappings for a given chat session.
     * @param chatId The chat ID for the session.
     */
    public void cleanupSession(long chatId) {
        sessionMappings.remove(chatId);
    }

    /**
     * Generate a mapping of short IDs to database IDs.
     * @param items A list of database IDs.
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
}