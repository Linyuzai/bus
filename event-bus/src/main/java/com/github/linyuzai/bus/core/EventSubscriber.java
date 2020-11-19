package com.github.linyuzai.bus.core;

import com.github.linyuzai.bus.Subscriber;
import com.github.linyuzai.bus.feature.EventSupport;
import com.github.linyuzai.bus.group.GroupSupport;

public interface EventSubscriber extends Subscriber<EventSource>, EventSupport, GroupSupport {

}
