package com.github.linyuzai.bus.strategy;

import com.github.linyuzai.bus.core.EventPublisher;
import com.github.linyuzai.bus.core.EventSource;
import com.github.linyuzai.bus.sync.SyncSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class CombinatorialEventStrategy extends ThreadPoolEventStrategy {

    private static final Logger logger = LoggerFactory.getLogger(CombinatorialEventStrategy.class);

    public CombinatorialEventStrategy() {
        super();
    }

    public CombinatorialEventStrategy(int nThreads) {
        super(nThreads);
    }

    public CombinatorialEventStrategy(ExecutorService executor) {
        super(executor);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void publish(List<EventPublisher> publishers, EventSource source, Object... args) {
        List<Object> syncArgs = filterSyncArgs(args);
        if (syncArgs.isEmpty()) {
            if (source instanceof SyncSupport) {
                publish(publishers, source, SyncSupport.FILTER);
            } else {
                super.publish(publishers, source, args);
            }
        } else {
            publishers.forEach(it -> publishWithHandleException(it, source));
        }
    }

    public List<Object> filterSyncArgs(Object... args) {
        return Arrays.stream(args).filter(this::isSyncSupport).collect(Collectors.toList());
    }

    public boolean isSyncSupport(Object arg) {
        return arg instanceof SyncSupport;
    }
}
