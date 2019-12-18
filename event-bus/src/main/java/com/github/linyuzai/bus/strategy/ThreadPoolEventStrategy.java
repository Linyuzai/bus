package com.github.linyuzai.bus.strategy;

import com.github.linyuzai.bus.core.EventPublisher;
import com.github.linyuzai.bus.core.EventSource;
import com.github.linyuzai.bus.exception.EventBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolEventStrategy extends AbstractEventStrategy {

    private static final int DEFAULT_THREADS = 6;

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolEventStrategy.class);

    private ExecutorService eventExecutor;

    public ThreadPoolEventStrategy() {
        this(DEFAULT_THREADS);
    }


    public ThreadPoolEventStrategy(int nThreads) {
        this(Executors.newFixedThreadPool(nThreads));
    }

    public ThreadPoolEventStrategy(ExecutorService executor) {
        this.eventExecutor = executor;
    }

    public ExecutorService getEventExecutor() {
        return eventExecutor;
    }

    public void setEventExecutor(ExecutorService eventExecutor) {
        this.eventExecutor = eventExecutor;
    }

    @Override
    public void initialize() {
        logger.info("Initialize Event Strategy: ThreadPool");
        if (eventExecutor == null) {
            throw new EventBusException("Event executor is null");
        }
    }

    @Override
    public void destroy() {
        if (!eventExecutor.isShutdown()) {
            eventExecutor.shutdown();
        }
    }

    @Override
    public void publish(EventSource source, List<EventPublisher> publishers) {
        publishers.forEach(it -> eventExecutor.execute(() -> publishWithHandleException(it, source)));
    }

    private void publishWithHandleException(EventPublisher eventPublisher, EventSource source) {
        try {
            eventPublisher.onPublish(source);
        } catch (Throwable e) {
            getEventExceptionHandler().handleException(Thread.currentThread(), eventPublisher, e);
        }
    }
}
