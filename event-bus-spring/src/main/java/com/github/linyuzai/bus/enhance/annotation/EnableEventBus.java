package com.github.linyuzai.bus.enhance.annotation;

import com.github.linyuzai.bus.enhance.configuration.EventPublishAspect;
import com.github.linyuzai.bus.enhance.configuration.EventBusAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        EventPublishAspect.class,
        EventBusAutoConfiguration.class})
public @interface EnableEventBus {
}
