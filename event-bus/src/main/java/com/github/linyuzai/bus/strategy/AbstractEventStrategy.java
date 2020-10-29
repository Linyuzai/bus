package com.github.linyuzai.bus.strategy;

import com.github.linyuzai.bus.core.EventBus;
import com.github.linyuzai.bus.exception.EventExceptionHandler;

public abstract class AbstractEventStrategy implements EventStrategy {

    private EventBus eventBus;

    @Override
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
