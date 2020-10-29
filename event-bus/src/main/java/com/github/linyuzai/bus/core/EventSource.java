package com.github.linyuzai.bus.core;

import com.github.linyuzai.bus.exception.EventExceptionHandler;

public interface EventSource {

    default EventExceptionHandler getExceptionHandler() {
        return null;
    }
}
