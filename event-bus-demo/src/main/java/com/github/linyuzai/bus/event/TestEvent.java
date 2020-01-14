package com.github.linyuzai.bus.event;

import com.github.linyuzai.bus.core.EventSource;

public class TestEvent implements EventSource {

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
