package com.github.linyuzai.bus.core;

import com.github.linyuzai.bus.Publisher;
import com.github.linyuzai.bus.feature.EventSupport;

public interface EventPublisher extends Publisher<EventSource>, EventSupport {

}
