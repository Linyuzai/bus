package com.github.linyuzai.bus.strategy;

import com.github.linyuzai.bus.core.EventPublisher;
import com.github.linyuzai.bus.core.EventSource;
import com.github.linyuzai.bus.sync.SyncSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class StandardEventStrategy extends ThreadPoolEventStrategy {

    private static final Logger logger = LoggerFactory.getLogger(StandardEventStrategy.class);

    public StandardEventStrategy() {
        super();
    }

    public StandardEventStrategy(int nThreads) {
        super(nThreads);
    }

    public StandardEventStrategy(ExecutorService executor) {
        super(executor);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void publish(EventPublisher publisher, EventSource source, Object... args) {
        List<Object> syncArgs = filterSyncArgs(args);
        if (syncArgs.isEmpty()) {
            if (source instanceof SyncSupport) {
                List<Object> newArgs = new ArrayList<>(Arrays.asList(args));
                newArgs.add(SyncSupport.FILTER);
                publish(publisher, source, newArgs.toArray());
            } else {
                super.publish(publisher, source, args);
            }
        } else {
            publishWithHandleException(publisher, source, args);
        }
    }

    public List<Object> filterSyncArgs(Object... args) {
        return Arrays.stream(args).filter(this::isSyncSupport).collect(Collectors.toList());
    }

    public boolean isSyncSupport(Object arg) {
        return arg instanceof SyncSupport;
    }
}
