package io.namastack.sample.service;

import io.namastack.sample.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public String createOrder(String customerId, BigDecimal amount, boolean fail) {
        String orderId = UUID.randomUUID().toString();
        log.info("Creating order [{}]. Fail demo flag: {}", orderId, fail);

        OrderCreatedEvent event = new OrderCreatedEvent(
            orderId,
            customerId,
            "Demo Customer",
            amount,
            "Demo Address",
            Collections.emptyList(),
            fail
        );

        eventPublisher.publishEvent(event);
        return orderId;
    }
}
