package com.github.linyuzai.bus.publisher;

import com.github.linyuzai.bus.core.EventBus;
import com.github.linyuzai.bus.core.EventPublisher;
import com.github.linyuzai.bus.core.EventSource;
import com.github.linyuzai.bus.core.EventSubscriber;
import com.github.linyuzai.bus.feature.SupportChecker;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultEventPublisher implements EventPublisher {

    private EventBus eventBus;

    @Override
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public boolean onPublish(EventSource source) throws Throwable {
        List<EventSubscriber> subscribers = eventBus.getSubscribers().stream()
                .filter(it -> filterWithHandleException(it, source)).collect(Collectors.toList());
        for (EventSubscriber subscriber : subscribers) {
            boolean continueSubscribe = onSubscribeWithHandleException(subscriber, source);
            if (!continueSubscribe) {
                return false;
            }
        }
        return true;
    }

    private boolean filterWithHandleException(SupportChecker<EventSource> supportChecker, EventSource source) {
        try {
            return supportChecker.isSupport(source);
        } catch (Throwable e) {
            eventBus.getEventExceptionHandler().handleException(e, source, supportChecker, Thread.currentThread());
            return false;
        }
    }

    private boolean onSubscribeWithHandleException(EventSubscriber eventSubscriber, EventSource source) {
        try {
            return eventSubscriber.onSubscribe(source);
        } catch (Throwable e) {
            return eventBus.getEventExceptionHandler().handleException(e, source, eventSubscriber, Thread.currentThread());
        }
    }
}
