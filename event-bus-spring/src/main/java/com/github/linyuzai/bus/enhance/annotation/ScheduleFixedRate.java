package com.github.linyuzai.bus.enhance.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

//@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ScheduleFixedRate {

    long initialDelay();

    long period();

    TimeUnit timeUnit();
}
