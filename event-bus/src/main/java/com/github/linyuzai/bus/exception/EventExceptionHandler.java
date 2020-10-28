package com.github.linyuzai.bus.exception;

import com.github.linyuzai.bus.core.EventSource;

public interface EventExceptionHandler {

    boolean handleException(Throwable e, EventSource source, Object object, Thread thread);
}
