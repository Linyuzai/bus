package com.github.linyuzai.bus.strategy;

import com.github.linyuzai.bus.core.EventPublisher;
import com.github.linyuzai.bus.core.EventSource;
import com.github.linyuzai.bus.sync.SyncSupport;

import java.util.List;

public class SyncEventStrategy extends StandardEventStrategy {

    public SyncEventStrategy() {
        super(null);
    }

    @Override
    public void start() {
        startLogger();
    }

    @Override
    public void publish(List<EventPublisher> publishers, EventSource source, Object... args) {
        super.publish(publishers, source, SyncSupport.FILTER);
    }
}
