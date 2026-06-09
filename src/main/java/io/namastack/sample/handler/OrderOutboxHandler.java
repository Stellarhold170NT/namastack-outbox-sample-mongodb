package io.namastack.sample.handler;

import io.namastack.outbox.handler.OutboxFailureContext;
import io.namastack.outbox.handler.OutboxRecordMetadata;
import io.namastack.outbox.annotation.OutboxFallbackHandler;
import io.namastack.outbox.annotation.OutboxHandler;
import io.namastack.sample.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderOutboxHandler {

    @OutboxHandler
    public void handleOrderCreated(OrderCreatedEvent event, OutboxRecordMetadata metadata) {
        log.info("--- [Outbox Worker] Processing record: {} ---", event.orderId());

        if (Boolean.TRUE.equals(event.fail())) {
            log.error("[DEMO FAIL] Throwing exception for Order: {}", event.orderId());
            throw new RuntimeException("Simulated failure for demo Retry!");
        }

        log.info("[SUCCESS] Order processed: {}", event.orderId());
    }

    @OutboxFallbackHandler
    public void handleFallback(OrderCreatedEvent event, OutboxFailureContext context) {
        log.error("!!! [FALLBACK] Permanently failed for record: {} !!!", event.orderId());
        log.error("Failure count: {}. Last error: {}",
            context.getFailureCount(),
            context.getLastFailure() != null ? context.getLastFailure().getMessage() : "Unknown");
        log.warn("Record logged to monitoring. Fallback completed with PERMANENTLY_FAILED.");
        throw new RuntimeException("Fallback also failed!");
    }
}
