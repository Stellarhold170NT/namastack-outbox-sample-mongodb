package io.namastack.sample.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.namastack.sample.model.OutboxRecord;
import io.namastack.sample.repository.OutboxRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxProcessor {

    private final OutboxRecordRepository outboxRecordRepository;
    private final ObjectMapper objectMapper;

    public void processRecord(OutboxRecord record) {
        try {
            log.info("Processing outbox record: {} (eventType: {})", record.getId(), record.getEventType());

            // Mark as processing
            record.setStatus(OutboxRecord.OutboxStatus.PROCESSING);
            record.setLastAttemptAt(LocalDateTime.now());
            outboxRecordRepository.save(record);

            // Deserialize payload
            Map<String, Object> payload = objectMapper.readValue(record.getPayload(), Map.class);

            // Process based on event type
            switch (record.getEventType()) {
                case "ORDER_CREATED" -> processOrderCreated(payload);
                default -> log.warn("Unknown event type: {}", record.getEventType());
            }

            // Mark as completed
            record.setStatus(OutboxRecord.OutboxStatus.COMPLETED);
            record.setProcessedAt(LocalDateTime.now());
            outboxRecordRepository.save(record);

            log.info("Successfully processed outbox record: {}", record.getId());

        } catch (Exception e) {
            log.error("Failed to process outbox record: {}", record.getId(), e);
            record.setStatus(OutboxRecord.OutboxStatus.FAILED);
            record.setRetryCount(record.getRetryCount() + 1);
            record.setLastError(e.getMessage());
            record.setLastAttemptAt(LocalDateTime.now());
            outboxRecordRepository.save(record);
        }
    }

    private void processOrderCreated(Map<String, Object> payload) {
        String orderId = (String) payload.get("orderId");
        String customerId = (String) payload.get("customerId");
        Number amount = (Number) payload.get("totalAmount");
        Boolean shouldFail = (Boolean) payload.getOrDefault("shouldFail", false);

        log.info("Handling ORDER_CREATED event for order: {}", orderId);

        if (Boolean.TRUE.equals(shouldFail)) {
            log.error("Simulated failure for order: {}", orderId);
            throw new RuntimeException("Simulated system error for retry demonstration!");
        }

        log.info("Order {} processed successfully. Customer: {}, Amount: {}",
                orderId, customerId, amount);
    }

    public boolean shouldRetry(OutboxRecord record, int maxRetries) {
        return record.getRetryCount() < maxRetries;
    }
}
