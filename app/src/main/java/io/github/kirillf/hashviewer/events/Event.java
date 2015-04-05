package io.github.kirillf.hashviewer.events;

/**
 * Core services events
 */
public class Event {
    private EventType type;
    private String message;

    public Event(EventType type, String message) {
        this.type = type;
        this.message = message;
    }

    public EventType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public enum EventType {
        AUTHORIZE,
        NO_CONNECTION,
        DATA_RECEIVED,
        END_OF_DATA,
        ERROR,
        RESET
    }
}
