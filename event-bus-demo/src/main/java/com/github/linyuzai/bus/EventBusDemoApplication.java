package com.github.linyuzai.bus;

import com.github.linyuzai.bus.enhance.annotation.EnableEventBus;
import com.github.linyuzai.bus.enhance.configuration.EventBusProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableEventBus
@SpringBootApplication
public class EventBusDemoApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(EventBusDemoApplication.class, args);
    }

    @Autowired
    private EventBusProperties properties;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        //System.out.println(properties.getAspectOrder());
    }
}
