package com.github.linyuzai.bus;

public interface Bus<T> {

    void publish(T source, Object... args);
}
