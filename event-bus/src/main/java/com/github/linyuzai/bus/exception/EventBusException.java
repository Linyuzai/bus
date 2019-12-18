package com.github.linyuzai.bus.exception;

public class EventBusException extends RuntimeException {

    public EventBusException(String message) {
        super(message);
    }

    public EventBusException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventBusException(Throwable cause) {
        super(cause);
    }
}
