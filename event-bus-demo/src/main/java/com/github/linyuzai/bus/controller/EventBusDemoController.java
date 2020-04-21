package com.github.linyuzai.bus.controller;

import com.github.linyuzai.bus.core.EventBus;
import com.github.linyuzai.bus.enhance.annotation.EventPublish;
import com.github.linyuzai.bus.enhance.annotation.EventSchedule;
import com.github.linyuzai.bus.enhance.annotation.ScheduleDelay;
import com.github.linyuzai.bus.enhance.type.EventParamType;
import com.github.linyuzai.bus.enhance.type.OpportunityType;
import com.github.linyuzai.bus.event.TestEvent;
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

    @EventPublish(TestEvent.class)
    @GetMapping("/test1")
    public String testEventBus1(String test) {
        return test;
    }

    @GetMapping("/test2")
    public String testEventBus2(String test) {
        eventBus.publish(new TestEvent(test));
        return test;
    }

    @EventPublish(value = TestEvent.class,//事件类型
            paramType = EventParamType.DEFAULT,//默认注入方法入参
            conditionExpression = "_return == 0",//返回值=0发布事件
            creationOpportunity = OpportunityType.AFTER,//在方法最后生成事件
            conditionalOpportunity = OpportunityType.AFTER,//在方法最后判断条件
            publishOpportunity = OpportunityType.AFTER,//在方法最后发布事件
            schedule = @EventSchedule(delay = @ScheduleDelay(delay = 5, timeUnit = TimeUnit.SECONDS)))//延时5秒
    @GetMapping("/test3")
    public int testEventBus3(String test) {
        return 0;
    }
}
