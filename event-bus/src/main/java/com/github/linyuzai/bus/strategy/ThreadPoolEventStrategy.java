package com.github.linyuzai.bus.strategy;

import com.github.linyuzai.bus.core.EventPublisher;
import com.github.linyuzai.bus.core.EventSource;
import com.github.linyuzai.bus.exception.EventBusException;
import com.github.linyuzai.bus.schedule.DelaySupport;
import com.github.linyuzai.bus.schedule.FixedDelaySupport;
import com.github.linyuzai.bus.schedule.FixedRateSupport;
import com.github.linyuzai.bus.schedule.metadata.Delay;
import com.github.linyuzai.bus.schedule.metadata.FixedDelay;
import com.github.linyuzai.bus.schedule.metadata.FixedRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
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
        getLogger().info("Initialize Event Strategy: " + getClass().getSimpleName());
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
    public void publish(List<EventPublisher> publishers, EventSource source, Object... args) {
        List<Object> scheduleArgs = filterScheduleArgs(args);
        if (scheduleArgs.isEmpty()) {
            if (source instanceof DelaySupport) {
                long d = ((DelaySupport) source).getDelay();
                TimeUnit t = ((DelaySupport) source).getTimeUnit();
                Delay delay = new Delay(d, t);
                publish(publishers, source, delay);
            } else if (source instanceof FixedDelaySupport) {
                long i = ((FixedDelaySupport) source).getInitialDelay();
                long d = ((FixedDelaySupport) source).getDelay();
                TimeUnit t = ((FixedDelaySupport) source).getTimeUnit();
                FixedDelay fixedDelay = new FixedDelay(i, d, t);
                publish(publishers, source, fixedDelay);
            } else if (source instanceof FixedRateSupport) {
                long i = ((FixedRateSupport) source).getInitialDelay();
                long p = ((FixedRateSupport) source).getPeriod();
                TimeUnit t = ((FixedRateSupport) source).getTimeUnit();
                FixedRate fixedRate = new FixedRate(i, p, t);
                publish(publishers, source, fixedRate);
            } else {
                publishers.forEach(it -> eventExecutor.execute(() -> publishWithHandleException(it, source)));
            }
        } else {
            if (eventExecutor instanceof ScheduledExecutorService) {
                ScheduledExecutorService ses = (ScheduledExecutorService) eventExecutor;
                for (Object scheduleArg : scheduleArgs) {
                    if (scheduleArg instanceof Delay) {
                        long d = ((Delay) scheduleArg).getDelay();
                        TimeUnit t = ((Delay) scheduleArg).getTimeUnit();
                        publishers.forEach(it ->
                                ses.schedule(() ->
                                        publishWithHandleException(it, source), d, t));
                    } else if (scheduleArg instanceof FixedDelay) {
                        long i = ((FixedDelay) scheduleArg).getInitialDelay();
                        long d = ((FixedDelay) scheduleArg).getDelay();
                        TimeUnit t = ((FixedDelay) scheduleArg).getTimeUnit();
                        publishers.forEach(it ->
                                ses.scheduleWithFixedDelay(() ->
                                        publishWithHandleException(it, source), i, d, t));
                    } else if (scheduleArg instanceof FixedRate) {
                        long i = ((FixedRate) scheduleArg).getInitialDelay();
                        long p = ((FixedRate) scheduleArg).getPeriod();
                        TimeUnit t = ((FixedRate) scheduleArg).getTimeUnit();
                        publishers.forEach(it ->
                                ses.scheduleAtFixedRate(() ->
                                        publishWithHandleException(it, source), i, p, t));
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

    public List<Object> filterScheduleArgs(Object... args) {
        return Arrays.stream(args).filter(this::isArgSupport).collect(Collectors.toList());
    }

    public boolean isArgSupport(Object arg) {
        return arg instanceof Delay || arg instanceof FixedDelay || arg instanceof FixedRate;
    }

    public void publishWithHandleException(EventPublisher eventPublisher, EventSource source) {
        try {
            eventPublisher.onPublish(source);
        } catch (Throwable e) {
            getEventExceptionHandler().handleException(e, source, eventPublisher, Thread.currentThread());
        }
    }
}
