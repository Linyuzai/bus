package com.github.linyuzai.bus.enhance;

import com.github.linyuzai.bus.core.EventBus;
import com.github.linyuzai.bus.core.EventPublisher;
import com.github.linyuzai.bus.core.EventSubscriber;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class EventBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof EventSubscriber) {
            EventBus.getInstance().register((EventSubscriber) bean);
        }
        if (bean instanceof EventPublisher) {
            EventBus.getInstance().register((EventPublisher) bean);
        }
        return bean;
    }
}
