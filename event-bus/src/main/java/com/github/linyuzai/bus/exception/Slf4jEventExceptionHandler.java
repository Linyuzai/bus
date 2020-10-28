package com.github.linyuzai.bus.exception;

import com.github.linyuzai.bus.core.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jEventExceptionHandler implements EventExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(Slf4jEventExceptionHandler.class);

    @Override
    public boolean handleException(Throwable e, EventSource source, Object object, Thread thread) {
        logger.error(source.getClass().getName(), e);
        return false;
    }
}
