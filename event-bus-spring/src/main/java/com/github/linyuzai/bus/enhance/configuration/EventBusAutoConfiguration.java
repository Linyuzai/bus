package com.github.linyuzai.bus.enhance.configuration;

import com.github.linyuzai.bus.core.EventBus;
import com.github.linyuzai.bus.core.EventPublisher;
import com.github.linyuzai.bus.core.EventSubscriber;
import com.github.linyuzai.bus.enhance.condition.EventPublishCondition;
import com.github.linyuzai.bus.enhance.condition.EventPublishConditionImpl;
import com.github.linyuzai.bus.exception.EventExceptionHandler;
import com.github.linyuzai.bus.exception.Slf4jEventExceptionHandler;
import com.github.linyuzai.bus.publisher.DefaultEventPublisher;
import com.github.linyuzai.bus.strategy.EventStrategy;
import com.github.linyuzai.bus.strategy.StandardEventStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

@EnableConfigurationProperties(EventBusProperties.class)
public class EventBusAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(EventPublisher.class)
    public EventPublisher eventPublisher() {
        return new DefaultEventPublisher();
    }

    @Bean
    @ConditionalOnMissingBean(EventStrategy.class)
    public EventStrategy eventStrategy() {
        return new StandardEventStrategy();
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

    @Bean(initMethod = "initialize", destroyMethod = "destroy")
    @ConditionalOnMissingBean(EventBus.class)
    public EventBus eventBus(EventPublisher eventPublisher,
                             EventStrategy eventStrategy,
                             EventExceptionHandler eventExceptionHandler,
                             List<EventSubscriber> eventSubscribers) {
        EventBus bus = new EventBus();
        bus.setEventPublisher(eventPublisher);
        bus.setEventStrategy(eventStrategy);
        bus.setEventExceptionHandler(eventExceptionHandler);
        bus.register(eventSubscribers);
        return bus;
    }
}
