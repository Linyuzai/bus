package com.github.linyuzai.bus.strategy;

import com.github.linyuzai.bus.core.EventPublisher;
import com.github.linyuzai.bus.core.EventSource;
import com.github.linyuzai.bus.exception.EventBusException;
import com.github.linyuzai.bus.exception.EventExceptionHandler;
import com.github.linyuzai.bus.group.Group;
import com.github.linyuzai.bus.schedule.DelaySupport;
import com.github.linyuzai.bus.schedule.FixedDelaySupport;
import com.github.linyuzai.bus.schedule.FixedRateSupport;
import com.github.linyuzai.bus.schedule.metadata.Delay;
import com.github.linyuzai.bus.schedule.metadata.FixedDelay;
import com.github.linyuzai.bus.schedule.metadata.FixedRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ThreadPoolEventStrategy extends AbstractEventStrategy {

    private static final int DEFAULT_THREADS = 6;

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolEventStrategy.class);

    private ExecutorService eventExecutor;

    public ThreadPoolEventStrategy() {
        this(DEFAULT_THREADS);
    }

    public ThreadPoolEventStrategy(int nThreads) {
        this(Executors.newScheduledThreadPool(nThreads));
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

    public Logger getLogger() {
        return logger;
    }

    public void startLogger() {
        getLogger().info("Initializing Event Strategy: " + getClass().getSimpleName());
    }

    @Override
    public void start() {
        startLogger();
        if (eventExecutor == null) {
            throw new EventBusException("Event executor is null");
        }
    }

    @Override
    public void stop() {
        if (!eventExecutor.isShutdown()) {
            eventExecutor.shutdown();
        }
    }

    @Override
    public void publish(EventPublisher publisher, EventSource source, Object... args) {
        List<Object> scheduleArgs = filterScheduleArgs(args);
        List<Object> newArgs = new ArrayList<>(Arrays.asList(args));
        if (scheduleArgs.isEmpty()) {
            if (source instanceof DelaySupport) {
                long d = ((DelaySupport) source).getDelay();
                TimeUnit t = ((DelaySupport) source).getTimeUnit();
                Delay delay = new Delay(d, t);
                newArgs.add(delay);
                publish(publisher, source, newArgs.toArray());
            } else if (source instanceof FixedDelaySupport) {
                long i = ((FixedDelaySupport) source).getInitialDelay();
                long d = ((FixedDelaySupport) source).getDelay();
                TimeUnit t = ((FixedDelaySupport) source).getTimeUnit();
                FixedDelay fixedDelay = new FixedDelay(i, d, t);
                newArgs.add(fixedDelay);
                publish(publisher, source, newArgs.toArray());
            } else if (source instanceof FixedRateSupport) {
                long i = ((FixedRateSupport) source).getInitialDelay();
                long p = ((FixedRateSupport) source).getPeriod();
                TimeUnit t = ((FixedRateSupport) source).getTimeUnit();
                FixedRate fixedRate = new FixedRate(i, p, t);
                newArgs.add(fixedRate);
                publish(publisher, source, newArgs.toArray());
            } else {
                eventExecutor.execute(() -> publishWithHandleException(publisher, source, args));
            }
        } else {
            if (eventExecutor instanceof ScheduledExecutorService) {
                final Runnable runnable = () -> publishWithHandleException(publisher, source, args);
                ScheduledExecutorService ses = (ScheduledExecutorService) eventExecutor;
                for (Object scheduleArg : scheduleArgs) {
                    if (scheduleArg instanceof Delay) {
                        long d = ((Delay) scheduleArg).getDelay();
                        TimeUnit t = ((Delay) scheduleArg).getTimeUnit();
                        ses.schedule(runnable, d, t);
                    } else if (scheduleArg instanceof FixedDelay) {
                        long i = ((FixedDelay) scheduleArg).getInitialDelay();
                        long d = ((FixedDelay) scheduleArg).getDelay();
                        TimeUnit t = ((FixedDelay) scheduleArg).getTimeUnit();
                        ses.scheduleWithFixedDelay(runnable, i, d, t);
                    } else if (scheduleArg instanceof FixedRate) {
                        long i = ((FixedRate) scheduleArg).getInitialDelay();
                        long p = ((FixedRate) scheduleArg).getPeriod();
                        TimeUnit t = ((FixedRate) scheduleArg).getTimeUnit();
                        ses.scheduleAtFixedRate(runnable, i, p, t);
                    } else {
                        //can not happen
                        throw new EventBusException("Schedule type is not match");
                    }
                }
            } else {
                throw new EventBusException("Event executor not support schedule");
            }
        }
    }

    public Group groupArgs(Object... args) {
        List<Group> groups = Arrays.stream(args).filter(it -> it instanceof Group).map(Group.class::cast).collect(Collectors.toList());
        if (groups.isEmpty()) {
            return Group.DEFAULT;
        }
        if (groups.size() > 1) {
            throw new EventBusException("Multi groups is not supported");
        }
        return groups.get(0);
    }

    public List<Object> filterScheduleArgs(Object... args) {
        return Arrays.stream(args).filter(this::isArgSupport).collect(Collectors.toList());
    }

    public boolean isArgSupport(Object arg) {
        return arg instanceof Delay || arg instanceof FixedDelay || arg instanceof FixedRate;
    }

    public void publishWithHandleException(EventPublisher eventPublisher, EventSource source, Object... args) {
        try {
            eventPublisher.onPublish(source, groupArgs(args));
        } catch (Throwable e) {
            getExceptionHandler(source).handleException(e, source, eventPublisher, Thread.currentThread());
        }
    }

    private EventExceptionHandler getExceptionHandler(EventSource source) {
        EventExceptionHandler handler = source.getExceptionHandler();
        return handler == null ? getEventBus().getEventExceptionHandler() : handler;
    }
}
