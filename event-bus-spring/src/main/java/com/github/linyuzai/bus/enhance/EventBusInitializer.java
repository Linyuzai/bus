package com.github.linyuzai.bus.enhance;

import com.github.linyuzai.bus.core.EventBus;
import com.github.linyuzai.bus.core.EventPublisher;
import com.github.linyuzai.bus.exception.EventExceptionHandler;
import com.github.linyuzai.bus.strategy.EventStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

public class EventBusInitializer implements ApplicationRunner, ApplicationListener<ContextClosedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(EventBusInitializer.class);

    @Autowired
    private EventBus eventBus;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private EventStrategy eventStrategy;

    @Autowired
    private EventExceptionHandler eventExceptionHandler;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        eventBus.setEventPublisher(eventPublisher);
        eventBus.setEventStrategy(eventStrategy);
        eventBus.setEventExceptionHandler(eventExceptionHandler);
        eventBus.initialize();
        logger.info("Event Bus start running");
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        eventBus.destroy();
        logger.info("Event Bus stop running");
    }
}
