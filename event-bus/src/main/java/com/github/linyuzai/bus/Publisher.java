package com.github.linyuzai.bus;

public interface Publisher<T> {

    void onPublish(T source) throws Throwable;
}
