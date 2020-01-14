package com.github.linyuzai.bus.enhance.annotation;

import com.github.linyuzai.bus.enhance.EventBeanPostProcessor;
import com.github.linyuzai.bus.enhance.EventBusInitializer;
import com.github.linyuzai.bus.enhance.EventPublishAspect;
import com.github.linyuzai.bus.enhance.configuration.EventBusAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        EventBusInitializer.class,
        EventPublishAspect.class,
        EventBusAutoConfiguration.class,
        EventBeanPostProcessor.class})
public @interface EnableEventBus {
}
