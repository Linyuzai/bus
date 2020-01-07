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

    @Override
    public void start() {
        logger.info("Initialize Event Strategy: ThreadPool");
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
        List<Object> filterArgs = filterArgs(args);
        if (filterArgs.isEmpty()) {
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
                for (Object filterArg : filterArgs) {
                    if (filterArg instanceof Delay) {
                        long d = ((Delay) filterArg).getDelay();
                        TimeUnit t = ((Delay) filterArg).getTimeUnit();
                        publishers.forEach(it ->
                                ses.schedule(() ->
                                        publishWithHandleException(it, source), d, t));
                    } else if (filterArg instanceof FixedDelay) {
                        long i = ((FixedDelay) filterArg).getInitialDelay();
                        long d = ((FixedDelay) filterArg).getDelay();
                        TimeUnit t = ((FixedDelay) filterArg).getTimeUnit();
                        publishers.forEach(it ->
                                ses.scheduleWithFixedDelay(() ->
                                        publishWithHandleException(it, source), i, d, t));
                    } else if (filterArg instanceof FixedRate) {
                        long i = ((FixedRate) filterArg).getInitialDelay();
                        long p = ((FixedRate) filterArg).getPeriod();
                        TimeUnit t = ((FixedRate) filterArg).getTimeUnit();
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

    private static List<Object> filterArgs(Object... args) {
        return Arrays.stream(args)
                .filter(it -> (it instanceof Delay || it instanceof FixedDelay || it instanceof FixedRate))
                .collect(Collectors.toList());
    }

    private void publishWithHandleException(EventPublisher eventPublisher, EventSource source) {
        try {
            eventPublisher.onPublish(source);
        } catch (Throwable e) {
            getEventExceptionHandler().handleException(Thread.currentThread(), eventPublisher, e);
        }
    }
}
