package com.github.linyuzai.bus.enhance.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bus.event")
public class EventBusProperties {

    private int aspectOrder = Integer.MAX_VALUE;

    public int getAspectOrder() {
        return aspectOrder;
    }

    public void setAspectOrder(int aspectOrder) {
        this.aspectOrder = aspectOrder;
    }
}
