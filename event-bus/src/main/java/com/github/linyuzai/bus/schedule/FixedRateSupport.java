package com.github.linyuzai.bus.schedule;

import java.util.concurrent.TimeUnit;

public interface FixedRateSupport {

    long getInitialDelay();

    long getPeriod();

    TimeUnit getTimeUnit();
}
