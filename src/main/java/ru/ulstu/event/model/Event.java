package ru.ulstu.event.model;

import java.sql.Timestamp;

public class Event {
    private String eventType;
    private Timestamp timestamp;

    public Event(String eventType, Timestamp timestamp) {
        this.eventType = eventType;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return eventType;
    }
}