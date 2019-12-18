package com.github.linyuzai.bus.strategy;

import com.github.linyuzai.bus.core.EventBus;
import com.github.linyuzai.bus.core.EventPublisher;
import com.github.linyuzai.bus.core.EventSource;
import com.github.linyuzai.bus.exception.EventExceptionHandler;

import java.util.List;

public interface EventStrategy {

    void initialize();

    void destroy();

    void publish(EventSource source, List<EventPublisher> publishers);

    void setEventBus(EventBus eventBus);

    void setEventExceptionHandler(EventExceptionHandler handler);
}