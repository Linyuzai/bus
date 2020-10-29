package com.github.linyuzai.bus.strategy;

import com.github.linyuzai.bus.core.EventPublisher;
import com.github.linyuzai.bus.core.EventSource;
import com.github.linyuzai.bus.sync.SyncSupport;

public class SyncEventStrategy extends StandardEventStrategy {

    public SyncEventStrategy() {
        super(null);
    }

    @Override
    public void start() {
        startLogger();
    }

    @Override
    public void publish(EventPublisher publisher, EventSource source, Object... args) {
        super.publish(publisher, source, SyncSupport.FILTER);
    }
}
