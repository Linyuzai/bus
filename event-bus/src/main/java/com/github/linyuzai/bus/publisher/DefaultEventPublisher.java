package com.github.linyuzai.bus.publisher;

import com.github.linyuzai.bus.core.EventBus;
import com.github.linyuzai.bus.core.EventPublisher;
import com.github.linyuzai.bus.core.EventSource;
import com.github.linyuzai.bus.core.EventSubscriber;
import com.github.linyuzai.bus.exception.EventExceptionHandler;
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
    public void onPublish(EventSource source) throws Throwable {
        List<EventSubscriber> subscribers = eventBus.getSubscribers().stream()
                .filter(it -> filterWithHandleException(it, source)).collect(Collectors.toList());
        for (EventSubscriber subscriber : subscribers) {
            onSubscribeWithHandleException(subscriber, source);
        }
    }

    private boolean filterWithHandleException(SupportChecker<EventSource> supportChecker, EventSource source) {
        try {
            return supportChecker.isSupport(source);
        } catch (Throwable e) {
            getExceptionHandler(source).handleException(e, source, supportChecker, Thread.currentThread());
            return false;
        }
    }

    private void onSubscribeWithHandleException(EventSubscriber eventSubscriber, EventSource source) {
        try {
            eventSubscriber.onSubscribe(source);
        } catch (Throwable e) {
            getExceptionHandler(source).handleException(e, source, eventSubscriber, Thread.currentThread());
        }
    }

    private EventExceptionHandler getExceptionHandler(EventSource source) {
        EventExceptionHandler handler = source.getExceptionHandler();
        return handler == null ? eventBus.getEventExceptionHandler() : handler;
    }
}
