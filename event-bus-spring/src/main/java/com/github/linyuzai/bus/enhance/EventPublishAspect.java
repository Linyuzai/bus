package com.github.linyuzai.bus.enhance;

import com.github.linyuzai.bus.core.EventBus;
import com.github.linyuzai.bus.core.EventSource;
import com.github.linyuzai.bus.enhance.annotation.*;
import com.github.linyuzai.bus.enhance.condition.EventPublishCondition;
import com.github.linyuzai.bus.enhance.configuration.EventBusProperties;
import com.github.linyuzai.bus.enhance.type.EventParamType;
import com.github.linyuzai.bus.enhance.type.OpportunityType;
import com.github.linyuzai.bus.exception.EventBusException;
import com.github.linyuzai.bus.schedule.metadata.Delay;
import com.github.linyuzai.bus.schedule.metadata.FixedDelay;
import com.github.linyuzai.bus.schedule.metadata.FixedRate;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
public class EventPublishAspect implements Ordered {

    public static final Object ILLEGAL_RETURN_VALUE = new Object();

    //@Value("${event-bus.aspect.order:#{T(java.lang.Integer).MAX_VALUE}}")
    //private int order;

    @Autowired
    private EventBusProperties properties;

    @Autowired
    private EventPublishCondition condition;

    @Pointcut("@annotation(com.github.linyuzai.bus.enhance.annotation.EventPublish) " +
            "|| @annotation(com.github.linyuzai.bus.enhance.annotation.EventPublishRepeatable)")
    public void eventPublishPointcut() {

    }

    @Around("eventPublishPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        List<EventHolder> holders = new ArrayList<>();
        EventPublish ep = method.getAnnotation(EventPublish.class);
        if (ep != null) {
            holders.add(new EventHolder(ep));
        }
        EventPublishRepeatable epr = method.getAnnotation(EventPublishRepeatable.class);
        if (epr != null) {
            EventPublish[] eps = epr.value();
            holders.addAll(Arrays.stream(eps).map(EventHolder::new).collect(Collectors.toList()));
        }
        Object[] args = joinPoint.getArgs();
        Iterator<EventHolder> iterable = holders.iterator();
        while (iterable.hasNext()) {
            EventHolder holder = iterable.next();
            if (holder.isConditionalAtBefore) {
                holder.conditional = condition.matchCondition(holder.eventPublish,
                        methodSignature, method, args, ILLEGAL_RETURN_VALUE);
                if (!holder.conditional) {
                    iterable.remove();
                    continue;
                }
            }
            if (holder.isCreationAtBefore) {
                holder.eventSource = getEventSource(holder.eventPublish,
                        method, args, ILLEGAL_RETURN_VALUE);
            }
            if (holder.isPublishAtBefore) {
                if (holder.conditional == null) {
                    throw new EventBusException("Conditional opportunity must be 'BEFORE'");
                }
                if (holder.eventSource == null) {
                    throw new EventBusException("Creation opportunity must be 'BEFORE'");
                }
                EventBus.getInstance().publish(holder.eventSource, holder.schedules);
                iterable.remove();
            }
        }
        Object value = joinPoint.proceed();
        for (EventHolder holder : holders) {
            if (holder.conditional == null) {
                holder.conditional = condition.matchCondition(holder.eventPublish,
                        methodSignature, method, args, value);
                if (!holder.conditional) {
                    continue;
                }
            }
            if (holder.eventSource == null) {
                holder.eventSource = getEventSource(holder.eventPublish, method, args, value);
            }
            EventBus.getInstance().publish(holder.eventSource, holder.schedules);
        }
        return value;
    }

    private EventSource getEventSource(EventPublish ep, Method method, Object[] args, Object returnValue) throws Throwable {
        EventParamType ept = ep.paramType();
        if (ept == EventParamType.RETURN && returnValue == ILLEGAL_RETURN_VALUE) {
            throw new EventBusException("No return value, creation opportunity must be 'AFTER'");
        }
        Class<? extends EventSource> es = ep.value();
        Constructor<?> noArgsConstructor = null;
        Constructor<?> constructor = null;
        Constructor<?> returnConstructor = null;
        Constructor<?>[] constructors = es.getDeclaredConstructors();
        for (Constructor<?> c : constructors) {
            Parameter[] parameters = c.getParameters();
            if (parameters.length == 0) {
                noArgsConstructor = c;
            } else {
                if (parameters.length == args.length) {
                    Class<?>[] pcs = method.getParameterTypes();
                    boolean isAllSame = true;
                    for (int i = 0; i < pcs.length; i++) {
                        if (parameters[i].getType() != Object.class &&
                                parameters[i].getType() != pcs[i]) {
                            isAllSame = false;
                            break;
                        }
                    }
                    if (isAllSame) {
                        constructor = c;
                    }
                }
                if (parameters.length == 1) {
                    if (parameters[0].getType() == Object.class ||
                            parameters[0].getType() == method.getReturnType()) {
                        returnConstructor = c;
                    }
                }
            }
        }
        if (noArgsConstructor != null) {
            noArgsConstructor.setAccessible(true);
        }
        if (constructor != null) {
            constructor.setAccessible(true);
        }
        if (returnConstructor != null) {
            returnConstructor.setAccessible(true);
        }
        EventSource event;
        if (ept == EventParamType.DEFAULT) {
            if (noArgsConstructor != null && constructor != null) {
                if (Modifier.isPublic(constructor.getModifiers())) {
                    event = (EventSource) constructor.newInstance();
                } else {
                    if (Modifier.isPublic(noArgsConstructor.getModifiers())) {
                        event = (EventSource) noArgsConstructor.newInstance(args);
                    } else {
                        event = (EventSource) constructor.newInstance();
                    }
                }
            } else if (noArgsConstructor != null) {
                event = (EventSource) noArgsConstructor.newInstance();
            } else if (constructor != null) {
                event = (EventSource) constructor.newInstance(args);
            } else {
                throw new EventBusException("No constructor found of " + es.getName());
            }
        } else {
            if (returnConstructor != null) {
                event = (EventSource) returnConstructor.newInstance(returnValue);
            } else {
                throw new EventBusException("No constructor found of " + es.getName());
            }
        }
        return event;
    }

    @Override
    public int getOrder() {
        return properties.getAspectOrder();
    }

    private static class EventHolder {
        private EventPublish eventPublish;
        private EventSource eventSource;
        private Boolean conditional;
        private Object[] schedules;

        private boolean isCreationAtBefore;
        private boolean isConditionalAtBefore;
        private boolean isPublishAtBefore;

        EventHolder(EventPublish eventPublish) {
            this.eventPublish = eventPublish;
            this.isCreationAtBefore = eventPublish.creationOpportunity() == OpportunityType.BEFORE;
            this.isConditionalAtBefore = eventPublish.conditionalOpportunity() == OpportunityType.BEFORE;
            this.isPublishAtBefore = eventPublish.publishOpportunity() == OpportunityType.BEFORE;
            List<Object> scheduleList = new ArrayList<>();
            EventSchedule[] ess = eventPublish.schedule();
            for (EventSchedule es : ess) {
                ScheduleDelay[] sds = es.delay();
                for (ScheduleDelay sd : sds) {
                    scheduleList.add(new Delay(sd.delay(), sd.timeUnit()));
                }
                ScheduleFixedDelay[] sfs = es.fixedDelay();
                for (ScheduleFixedDelay sf : sfs) {
                    scheduleList.add(new FixedDelay(sf.initialDelay(), sf.delay(), sf.timeUnit()));
                }
                ScheduleFixedRate[] srs = es.fixedRate();
                for (ScheduleFixedRate sr : srs) {
                    scheduleList.add(new FixedRate(sr.initialDelay(), sr.period(), sr.timeUnit()));
                }
            }
            this.schedules = scheduleList.toArray();
        }
    }
}
