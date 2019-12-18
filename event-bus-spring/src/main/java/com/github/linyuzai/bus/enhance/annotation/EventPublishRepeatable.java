package com.github.linyuzai.bus.enhance.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EventPublishRepeatable {

    EventPublish[] value();
}
