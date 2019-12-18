package com.github.linyuzai.bus.enhance.condition;

import com.github.linyuzai.bus.enhance.annotation.EventPublish;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

public interface EventPublishCondition {

    boolean matchCondition(EventPublish ep, MethodSignature methodSignature, Method method, Object[] args, Object returnValue);
}
