package com.github.linyuzai.bus.exception;

import com.github.linyuzai.bus.core.EventSource;

public interface EventExceptionHandler {

    void handleException(Throwable e, EventSource source, Object object, Thread thread);
}
