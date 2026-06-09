package io.namastack.sample.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.namastack.sample.model.Order;
import io.namastack.sample.model.OutboxRecord;
import io.namastack.sample.repository.OrderRepository;
import io.namastack.sample.repository.OutboxRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRecordRepository outboxRecordRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public String createOrder(String customerId, BigDecimal amount, boolean fail) {
        String orderId = UUID.randomUUID().toString();
        log.info("Creating order [{}]. Fail demo: {}", orderId, fail);

        // Save order to MongoDB
        Order order = Order.builder()
                .id(orderId)
                .customerId(customerId)
                .customerName("Demo Customer")
                .totalAmount(amount)
                .shippingAddress("Example Address")
                .status("PENDING")
                .notes(fail ? "Will trigger failure" : "Normal order")
                .build();
        orderRepository.save(order);

        // Create outbox record manually
        Map<String, Object> eventPayload = Map.of(
                "orderId", orderId,
                "customerId", customerId,
                "customerName", "Demo Customer",
                "totalAmount", amount,
                "shippingAddress", "Example Address",
                "shouldFail", fail
        );

        try {
            String payloadJson = objectMapper.writeValueAsString(eventPayload);

            OutboxRecord outboxRecord = OutboxRecord.builder()
                    .aggregateId(orderId)
                    .aggregateType("ORDER")
                    .eventType("ORDER_CREATED")
                    .payload(payloadJson)
                    .status(OutboxRecord.OutboxStatus.PENDING)
                    .retryCount(0)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxRecordRepository.save(outboxRecord);
            log.info("Outbox record created for order: {}", orderId);

        } catch (Exception e) {
            log.error("Failed to create outbox record for order: {}", orderId, e);
            throw new RuntimeException("Failed to persist outbox record", e);
        }

        return orderId;
    }
}
