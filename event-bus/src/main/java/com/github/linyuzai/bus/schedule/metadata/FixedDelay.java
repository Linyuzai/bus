package com.github.linyuzai.bus.schedule.metadata;

import java.util.concurrent.TimeUnit;

public class FixedDelay {

    private long initialDelay;

    private long delay;

    private TimeUnit timeUnit;

    public FixedDelay(long initialDelay, long delay, TimeUnit timeUnit) {
        this.initialDelay = initialDelay;
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    public long getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }
}
