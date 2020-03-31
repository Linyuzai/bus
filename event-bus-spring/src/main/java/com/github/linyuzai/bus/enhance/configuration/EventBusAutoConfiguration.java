package com.github.linyuzai.bus.enhance.configuration;

import com.github.linyuzai.bus.enhance.condition.EventPublishCondition;
import com.github.linyuzai.bus.enhance.condition.EventPublishConditionImpl;
import com.github.linyuzai.bus.exception.EventExceptionHandler;
import com.github.linyuzai.bus.exception.Slf4jEventExceptionHandler;
import com.github.linyuzai.bus.strategy.CombinatorialEventStrategy;
import com.github.linyuzai.bus.strategy.EventStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties(EventBusProperties.class)
public class EventBusAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(EventStrategy.class)
    public EventStrategy eventStrategy() {
        return new CombinatorialEventStrategy();
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
