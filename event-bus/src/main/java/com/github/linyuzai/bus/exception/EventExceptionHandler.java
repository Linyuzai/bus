package com.github.linyuzai.bus.exception;

public interface EventExceptionHandler {

    void handleException(Thread thread, Object object, Throwable e);
}
