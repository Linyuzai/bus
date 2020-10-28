package com.github.linyuzai.bus;

public interface Publisher<T> {

    boolean onPublish(T source) throws Throwable;
}
