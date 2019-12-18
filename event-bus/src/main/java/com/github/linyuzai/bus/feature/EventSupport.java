package com.github.linyuzai.bus.feature;

import com.github.linyuzai.bus.core.EventSource;
import com.github.linyuzai.bus.feature.annotation.OnEvent;

public interface EventSupport extends SupportChecker<EventSource> {

    @Override
    default boolean isSupport(EventSource source) {
        Class<?> cls = getClass();
        OnEvent oe = cls.getAnnotation(OnEvent.class);
        if (oe == null) {
            return false;
        }
        Class<? extends EventSource>[] ess = oe.value();
        Class<? extends EventSource> target = source.getClass();
        for (Class<? extends EventSource> es : ess) {
            if (es.isAssignableFrom(target)) {
                return true;
            }
        }
        return false;
    }
}
