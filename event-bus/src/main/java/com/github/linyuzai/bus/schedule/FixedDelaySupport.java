package com.github.linyuzai.bus.schedule;

import java.util.concurrent.TimeUnit;

public interface FixedDelaySupport {

    long getInitialDelay();

    long getDelay();

    TimeUnit getTimeUnit();
}
