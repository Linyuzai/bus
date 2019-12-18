package com.github.linyuzai.bus.enhance.annotation;

import com.github.linyuzai.bus.core.EventSource;
import com.github.linyuzai.bus.enhance.type.EventParamType;
import com.github.linyuzai.bus.enhance.type.OpportunityType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(value = EventPublishRepeatable.class)
public @interface EventPublish {

    /**
     * @return 事件类
     */
    Class<? extends EventSource> value();

    /**
     * @return 条件表达式
     */
    String conditionExpression() default "";

    /**
     * @return 创建事件在方法执行前还是执行后
     */
    OpportunityType creationOpportunity() default OpportunityType.BEFORE;

    /**
     * @return 条件表达式处理在方法执行前还是执行后
     */
    OpportunityType conditionalOpportunity() default OpportunityType.BEFORE;

    /**
     * @return 发布事件在方法执行前还是执行后
     */
    OpportunityType publishOpportunity() default OpportunityType.AFTER;

    /**
     * @return 创建事件时注入方法入参还是返回值
     */
    EventParamType paramType() default EventParamType.DEFAULT;
}
