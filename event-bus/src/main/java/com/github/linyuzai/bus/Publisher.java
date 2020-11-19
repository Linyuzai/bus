package com.github.linyuzai.bus;

import com.github.linyuzai.bus.group.Group;

public interface Publisher<T> {

    void onPublish(T source, Group group) throws Throwable;
}
