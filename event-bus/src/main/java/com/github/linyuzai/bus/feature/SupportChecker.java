package com.github.linyuzai.bus.feature;

public interface SupportChecker<T> {

    default boolean isSupport(T source) {
        return true;
    }
}
