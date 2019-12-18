package com.github.linyuzai.bus;

import com.github.linyuzai.bus.enhance.annotation.EnableEventBus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableEventBus
@SpringBootApplication
public class EventBusDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventBusDemoApplication.class, args);
    }

}
