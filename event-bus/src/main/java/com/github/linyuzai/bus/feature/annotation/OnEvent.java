package com.github.linyuzai.bus.feature.annotation;

import com.github.linyuzai.bus.core.EventSource;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface OnEvent {

    Class<? extends EventSource>[] value();
}
