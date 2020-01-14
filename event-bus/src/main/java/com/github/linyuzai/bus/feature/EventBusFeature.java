package com.github.linyuzai.bus.feature;

import com.github.linyuzai.bus.core.EventSource;
import com.github.linyuzai.bus.feature.annotation.OnEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class EventBusFeature {

    private static Map<String, Collection<Class<? extends EventSource>>> features = new ConcurrentHashMap<>();

    public static void add(Class<?> cls) {
        if (cls == null) {
            return;
        }
        OnEvent oe = cls.getAnnotation(OnEvent.class);
        if (oe == null) {
            return;
        }
        Class<? extends EventSource>[] ess = oe.value();
        List<Class<? extends EventSource>> esl = Arrays.asList(ess);
        if (oe.inherited()) {
            features.put(cls.getName(), esl);
        } else {
            features.put(cls.getName(), new HashSet<>(esl));
        }
    }

    public static void remove(Class<?> cls) {
        if (cls == null) {
            return;
        }
        features.remove(cls.getName());
    }

    public static boolean isSupport(Class<?> cls, Class<? extends EventSource> target) {
        if (cls == null) {
            return false;
        }
        Collection<Class<? extends EventSource>> esc = features.get(cls.getName());
        if (esc == null) {
            return false;
        }
        if (esc instanceof List) {
            for (Class<? extends EventSource> es : esc) {
                if (es.isAssignableFrom(target)) {
                    return true;
                }
            }
        } else if (esc instanceof Set) {
            return esc.contains(target);
        }
        return false;
    }
}
