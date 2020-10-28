package com.github.linyuzai.bus.strategy;

import com.github.linyuzai.bus.core.EventBus;
import com.github.linyuzai.bus.core.EventPublisher;
import com.github.linyuzai.bus.core.EventSource;
import com.github.linyuzai.bus.exception.EventExceptionHandler;

import java.util.List;

public interface EventStrategy {

    void start();

    void stop();

    boolean publish(EventPublisher publisher, EventSource source, Object... args);

    void setEventBus(EventBus eventBus);

    void setEventExceptionHandler(EventExceptionHandler handler);
}
