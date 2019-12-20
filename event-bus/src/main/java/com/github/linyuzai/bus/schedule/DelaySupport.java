package com.github.linyuzai.bus.schedule;

import java.util.concurrent.TimeUnit;

public interface DelaySupport {

    long getDelay();

    TimeUnit getTimeUnit();
}
