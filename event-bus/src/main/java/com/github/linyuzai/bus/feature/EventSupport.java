package com.github.linyuzai.bus.feature;

import com.github.linyuzai.bus.core.EventSource;
import com.github.linyuzai.bus.plugin.EventBusPlugin;

public interface EventSupport extends SupportChecker<EventSource> {

    @Override
    default boolean isSupport(EventSource source) {
        return EventBusPlugin.isSupport(this.getClass(), source.getClass());
    }
}
