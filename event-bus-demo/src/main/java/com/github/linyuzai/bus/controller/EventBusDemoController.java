package com.github.linyuzai.bus.controller;

import com.github.linyuzai.bus.core.EventBus;
import com.github.linyuzai.bus.enhance.annotation.EventPublish;
import com.github.linyuzai.bus.enhance.annotation.EventSchedule;
import com.github.linyuzai.bus.enhance.annotation.ScheduleDelay;
import com.github.linyuzai.bus.enhance.type.EventParamType;
import com.github.linyuzai.bus.enhance.type.OpportunityType;
import com.github.linyuzai.bus.event.TestEvent;
import com.github.linyuzai.bus.group.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/event-bus-demo")
public class EventBusDemoController {

    @Autowired
    private EventBus eventBus;

    @GetMapping("/test")
    public String testEventBus(String test) {
        System.out.println(Thread.currentThread() + " => publish");
        eventBus.publish(new TestEvent(test), Group.exclude());
        return test;
    }
}
