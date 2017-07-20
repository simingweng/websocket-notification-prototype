package com.simon.notification.prototype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;

import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class WebsocketNotificationPrototypeApplication {
    private static final Logger logger = LoggerFactory.getLogger(WebsocketNotificationPrototypeApplication.class);
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private ScheduledFuture scheduledFuture;

    public static void main(String[] args) {
        SpringApplication.run(WebsocketNotificationPrototypeApplication.class, args);
    }

    @EventListener
    public void onBrokerAvailabilityChanged(BrokerAvailabilityEvent event) {
        if (event.isBrokerAvailable()) {
            scheduledFuture = Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
                logger.info("broadcasting messages");
                simpMessagingTemplate.convertAndSend("/topic/demotenant1", "Hello, demo tenant1, this is an event for you!");
                simpMessagingTemplate.convertAndSend("/topic/demotenant2", "Hello, demo tenant2, this is an event for you!");
            }, 0, 3, TimeUnit.SECONDS);
        } else {
            stopGreeting();
        }
    }

    @PreDestroy
    public void stopGreeting() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
    }

}
