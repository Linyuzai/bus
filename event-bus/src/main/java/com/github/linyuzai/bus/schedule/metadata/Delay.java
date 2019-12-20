package com.github.linyuzai.bus.schedule.metadata;

import java.util.concurrent.TimeUnit;

public class Delay {

    private long delay;

    private TimeUnit timeUnit;

    public Delay(long delay, TimeUnit timeUnit) {
        this.delay = delay;
        this.timeUnit = timeUnit;
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
