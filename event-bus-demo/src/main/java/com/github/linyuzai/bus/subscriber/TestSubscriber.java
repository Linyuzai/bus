package com.github.linyuzai.bus.subscriber;

import com.github.linyuzai.bus.core.EventSource;
import com.github.linyuzai.bus.core.EventSubscriber;
import com.github.linyuzai.bus.event.TestEvent;
import com.github.linyuzai.bus.feature.annotation.OnEvent;
import org.springframework.stereotype.Component;

@Component
@OnEvent(TestEvent.class)
public class TestSubscriber implements EventSubscriber {
    @Override
    public void onSubscribe(EventSource eventSource) {
        System.out.println(((TestEvent) eventSource).getTest());
    }
}
