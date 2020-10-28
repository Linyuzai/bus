package com.github.linyuzai.bus;

public interface Subscriber<T> {

    boolean onSubscribe(T source) throws Throwable;
}
