package com.github.linyuzai.bus.feature;

import com.github.linyuzai.bus.core.EventSource;
import com.github.linyuzai.bus.feature.annotation.OnEvent;

public interface EventSupport extends SupportChecker<EventSource> {

    @Override
    default boolean isSupport(EventSource source) {
        return EventBusFeature.isSupport(this.getClass(), source.getClass());
    }
}
