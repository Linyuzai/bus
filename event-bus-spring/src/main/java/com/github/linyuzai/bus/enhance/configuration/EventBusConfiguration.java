package com.github.linyuzai.bus.enhance.configuration;

import com.github.linyuzai.bus.enhance.condition.EventPublishCondition;
import com.github.linyuzai.bus.enhance.condition.EventPublishConditionImpl;
import com.github.linyuzai.bus.exception.EventExceptionHandler;
import com.github.linyuzai.bus.exception.Slf4jEventExceptionHandler;
import com.github.linyuzai.bus.strategy.EventStrategy;
import com.github.linyuzai.bus.strategy.ThreadPoolEventStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class EventBusConfiguration {

    @Bean
    @ConditionalOnMissingBean(EventStrategy.class)
    public EventStrategy eventStrategy() {
        return new ThreadPoolEventStrategy();
    }

    @Bean
    @ConditionalOnMissingBean(EventExceptionHandler.class)
    public EventExceptionHandler eventExceptionHandler() {
        return new Slf4jEventExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(EventPublishCondition.class)
    public EventPublishCondition eventPublishCondition() {
        return new EventPublishConditionImpl();
    }
}
