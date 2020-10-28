package com.github.linyuzai.bus.enhance;

import com.github.linyuzai.bus.core.EventBus;
import com.github.linyuzai.bus.core.EventPublisher;
import com.github.linyuzai.bus.core.EventSubscriber;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class EventBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    private EventBus eventBus;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof EventSubscriber) {
            eventBus.register((EventSubscriber) bean);
        }
        return bean;
    }
}
