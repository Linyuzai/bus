package com.github.linyuzai.bus.core;

import com.github.linyuzai.bus.Bus;
import com.github.linyuzai.bus.exception.EventBusException;
import com.github.linyuzai.bus.exception.EventExceptionHandler;
import com.github.linyuzai.bus.plugin.EventBusPlugin;
import com.github.linyuzai.bus.strategy.EventStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus implements Bus<EventSource> {

    private static final Logger logger = LoggerFactory.getLogger(EventBus.class);

    private Collection<EventSubscriber> subscribers = new CopyOnWriteArrayList<>();

    private EventPublisher eventPublisher;

    private EventStrategy eventStrategy;

    private EventExceptionHandler eventExceptionHandler;

    private volatile boolean isInitialized = false;

    public Collection<EventSubscriber> getSubscribers() {
        return subscribers;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public EventStrategy getEventStrategy() {
        return eventStrategy;
    }

    public void setEventStrategy(EventStrategy eventStrategy) {
        if (eventStrategy == null) {
            throw new EventBusException("Event Strategy can not be null");
        }
        this.eventStrategy = eventStrategy;
        this.eventStrategy.setEventBus(this);
        if (this.eventExceptionHandler != null) {
            this.eventStrategy.setEventExceptionHandler(this.eventExceptionHandler);
        }
    }

    public EventExceptionHandler getEventExceptionHandler() {
        return eventExceptionHandler;
    }

    public void setEventExceptionHandler(EventExceptionHandler eventExceptionHandler) {
        if (eventExceptionHandler == null) {
            throw new EventBusException("Event Exception Handler can not be null");
        }
        this.eventExceptionHandler = eventExceptionHandler;
        if (this.eventStrategy != null) {
            eventStrategy.setEventExceptionHandler(eventExceptionHandler);
        }
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        eventPublisher.setEventBus(this);
        this.eventPublisher = eventPublisher;
    }

    public synchronized void initialize() {
        if (!isInitialized) {
            logger.info("Initializing Event Bus");
            if (eventPublisher == null) {
                throw new EventBusException("Event Publisher is null");
            }
            if (eventStrategy == null) {
                throw new EventBusException("Event Strategy is null");
            }
            if (eventExceptionHandler == null) {
                throw new EventBusException("Event Exception Handler is null");
            }
            eventStrategy.start();
            isInitialized = true;
        }
    }

    public synchronized void destroy() {
        if (isInitialized) {
            logger.info("Destroy Event Bus");
            if (eventStrategy != null) {
                eventStrategy.stop();
                eventStrategy = null;
            }
        }
    }

    public void register(Collection<? extends EventSubscriber> subscribers) {
        subscribers.forEach(this::register);
    }

    public void register(EventSubscriber subscriber) {
        if (subscriber == null) {
            throw new EventBusException("Event Subscriber is null");
        }
        subscribers.add(subscriber);
        EventBusPlugin.add(subscriber.getClass());
    }

    public void unregister(EventSubscriber subscriber) {
        if (subscriber == null) {
            throw new EventBusException("Event Subscriber is null");
        }
        subscribers.remove(subscriber);
        EventBusPlugin.remove(subscriber.getClass());
    }

    @Override
    public void publish(EventSource source, Object... args) {
        if (isInitialized) {
            if (source == null) {
                throw new EventBusException("Event source is null");
            }
            eventStrategy.publish(eventPublisher, source, args);
        } else {
            throw new EventBusException("Event bus is not initialized");
        }
    }
}
