package com.github.linyuzai.bus;

public interface Bus<T, S extends Subscriber<T>, P extends Publisher<T>> {

    void initialize();

    void destroy();

    void register(S subscriber);

    void unregister(S subscriber);

    void register(P publisher);

    void unregister(P publisher);

    void publish(T source, Object... args);
}
