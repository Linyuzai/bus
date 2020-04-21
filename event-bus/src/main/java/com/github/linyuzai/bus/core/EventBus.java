package com.github.linyuzai.bus.core;

import com.github.linyuzai.bus.Bus;
import com.github.linyuzai.bus.exception.EventBusException;
import com.github.linyuzai.bus.exception.EventExceptionHandler;
import com.github.linyuzai.bus.plugin.EventBusPlugin;
import com.github.linyuzai.bus.feature.SupportChecker;
import com.github.linyuzai.bus.strategy.EventStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class EventBus implements Bus<EventSource, EventSubscriber, EventPublisher> {

    private static final Logger logger = LoggerFactory.getLogger(EventBus.class);

    private List<EventSubscriber> subscribers = new CopyOnWriteArrayList<>();
    private List<EventPublisher> publishers = new CopyOnWriteArrayList<>();

    private EventStrategy eventStrategy;

    private EventExceptionHandler eventExceptionHandler;

    private boolean isInitialized = false;

    private EventPublisher publisher = new EventPublisher() {
        @Override
        public void onPublish(EventSource source) throws Throwable {
            subscribers.stream()
                    .filter(it -> filterWithHandleException(it, source))
                    .forEach(it -> onSubscribeWithHandleException(it, source));
        }

        @Override
        public boolean isSupport(EventSource source) {
            return true;
        }
    };

    public List<EventSubscriber> getSubscribers() {
        return subscribers;
    }

    public List<EventPublisher> getPublishers() {
        return publishers;
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

    @Override
    public synchronized void initialize() {
        if (!isInitialized) {
            logger.info("Initializing Event Bus");
            register(publisher);
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

    @Override
    public synchronized void destroy() {
        if (isInitialized) {
            logger.info("Destroy Event Bus");
            if (eventStrategy != null) {
                eventStrategy.stop();
                eventStrategy = null;
            }
        }
    }

    @Override
    public void register(EventSubscriber subscriber) {
        if (subscriber == null) {
            throw new EventBusException("Event Subscriber is null");
        }
        subscribers.add(subscriber);
        EventBusPlugin.add(subscriber.getClass());
    }

    @Override
    public void unregister(EventSubscriber subscriber) {
        if (subscriber == null) {
            throw new EventBusException("Event Subscriber is null");
        }
        subscribers.remove(subscriber);
        EventBusPlugin.remove(subscriber.getClass());
    }

    @Override
    public void register(EventPublisher publisher) {
        if (publisher == null) {
            throw new EventBusException("Event Publisher is null");
        }
        publishers.add(publisher);
        EventBusPlugin.add(publisher.getClass());
    }

    @Override
    public void unregister(EventPublisher publisher) {
        if (publisher == null) {
            throw new EventBusException("Event Publisher is null");
        }
        publishers.remove(publisher);
        EventBusPlugin.remove(publisher.getClass());
    }

    @Override
    public void publish(EventSource source, Object... args) {
        if (isInitialized) {
            if (source == null) {
                throw new EventBusException("Event source is null");
            }
            List<EventPublisher> eps = publishers.stream()
                    .filter(it -> filterWithHandleException(it, source))
                    .collect(Collectors.toList());
            eventStrategy.publish(eps, source, args);
        } else {
            throw new EventBusException("Event bus is not initialized");
        }
    }

    private boolean filterWithHandleException(SupportChecker<EventSource> supportChecker, EventSource source) {
        try {
            return supportChecker.isSupport(source);
        } catch (Throwable e) {
            eventExceptionHandler.handleException(e, source, supportChecker, Thread.currentThread());
            return false;
        }
    }

    private void onSubscribeWithHandleException(EventSubscriber eventSubscriber, EventSource source) {
        try {
            eventSubscriber.onSubscribe(source);
        } catch (Throwable e) {
            eventExceptionHandler.handleException(e, source, eventSubscriber, Thread.currentThread());
        }
    }
}
