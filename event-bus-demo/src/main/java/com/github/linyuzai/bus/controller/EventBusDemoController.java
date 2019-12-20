package com.github.linyuzai.bus.controller;

import com.github.linyuzai.bus.core.EventBus;
import com.github.linyuzai.bus.enhance.annotation.EventPublish;
import com.github.linyuzai.bus.event.TestEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event-bus-demo")
public class EventBusDemoController {

    @EventPublish(TestEvent.class)
    @GetMapping("/test")
    public String testEventBus1(String test) {
        return test;
    }

    @GetMapping("/test")
    public String testEventBus2(String test) {
        EventBus.getInstance().publish(new TestEvent(test));
        return test;
    }
}
