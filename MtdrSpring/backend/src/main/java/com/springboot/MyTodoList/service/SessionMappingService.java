package com.springboot.MyTodoList.service;

import java.util.HashMap;
import java.util.Map;

public class SessionMappingService {
    // Map to store session-specific mappings for each chat and type
    private final Map<Long, Map<String, Map<String, Integer>>> sessionMappings = new HashMap<>();

    /**
     * Store a mapping for a specific type (e.g., tasks, users, sprints) in a chat session.
     * @param chatId The chat ID for the session.
     * @param type The type of mapping (e.g., "tasks", "users", "sprints").
     * @param items A map of short IDs to database IDs.
     */
    public void storeMapping(long chatId, String type, Map<String, Integer> items) {
        sessionMappings.putIfAbsent(chatId, new HashMap<>());
        sessionMappings.get(chatId).put(type, items);
    }

    /**
     * Retrieve the database ID for a given session-specific ID and type.
     * @param chatId The chat ID for the session.
     * @param type The type of mapping (e.g., "tasks", "users", "sprints").
     * @param shortId The session-specific ID.
     * @return The original database ID, or null if not found.
     */
    public Integer getOriginalId(long chatId, String type, String shortId) {
        Map<String, Map<String, Integer>> typeMappings = sessionMappings.get(chatId);
        if (typeMappings != null) {
            Map<String, Integer> mapping = typeMappings.get(type);
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
}