package com.github.linyuzai.bus.event;

import com.github.linyuzai.bus.core.EventSource;
import com.github.linyuzai.bus.sync.SyncSupport;

public class TestEvent implements EventSource, SyncSupport {

    private String test;

    public TestEvent(String test) {
        this.test = test;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

}
