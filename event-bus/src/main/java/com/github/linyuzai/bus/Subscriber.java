package com.github.linyuzai.bus;

public interface Subscriber<T> {

    void onSubscribe(T source) throws Throwable;
}
