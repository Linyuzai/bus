package com.github.linyuzai.bus.enhance.condition;

import com.github.linyuzai.bus.enhance.EventPublishAspect;
import com.github.linyuzai.bus.enhance.annotation.EventPublish;
import com.github.linyuzai.bus.exception.EventBusException;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class EventPublishConditionImpl implements EventPublishCondition {

    @Override
    public boolean matchCondition(EventPublish ep, MethodSignature methodSignature,
                                  Method method, Object[] args, Object returnValue) {
        String condition = ep.conditionExpression();
        if (condition.isEmpty()) {
            return true;
        }
        if (returnValue == EventPublishAspect.ILLEGAL_RETURN_VALUE && condition.contains("_return")) {
            throw new EventBusException("'_return' not found");
        }
        String[] parameterNames = methodSignature.getParameterNames();
        Parameter[] parameters = method.getParameters();
        Map<String, Object> env = new HashMap<>();
        for (int i = 0; i < parameterNames.length; i++) {
            env.put(parameterNames[i], args[i]);
            env.put(parameters[i].getName(), args[i]);
        }
        env.put("_return", returnValue);
        Expression expression = AviatorEvaluator.compile(condition, true);
        Object result = expression.execute(env);
        if (result instanceof Boolean) {
            return (Boolean) result;
        } else {
            throw new EventBusException("Expression '" + condition + "' is illegal");
        }
    }
}
