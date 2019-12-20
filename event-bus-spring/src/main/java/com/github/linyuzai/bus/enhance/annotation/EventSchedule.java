package com.github.linyuzai.bus.enhance.annotation;

import java.lang.annotation.*;

//@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EventSchedule {
    ScheduleDelay[] delay() default {};

    ScheduleFixedDelay[] fixedDelay() default {};

    ScheduleFixedRate[] fixedRate() default {};
}
