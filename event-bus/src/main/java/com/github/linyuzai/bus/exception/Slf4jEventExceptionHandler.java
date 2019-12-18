package com.github.linyuzai.bus.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jEventExceptionHandler implements EventExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(Slf4jEventExceptionHandler.class);

    @Override
    public void handleException(Thread thread, Object object, Throwable e) {
        logger.error(thread.getName(), e);
    }
}
