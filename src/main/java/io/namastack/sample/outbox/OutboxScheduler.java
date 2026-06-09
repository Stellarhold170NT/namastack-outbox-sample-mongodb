package io.namastack.sample.outbox;

import io.namastack.sample.model.OutboxRecord;
import io.namastack.sample.repository.OutboxRecordRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final OutboxRecordRepository outboxRecordRepository;
    private final OutboxProcessor outboxProcessor;

    @Value("${outbox.scheduler.batch-size:10}")
    private int batchSize;

    @Value("${outbox.scheduler.max-retries:10}")
    private int maxRetries;

    @PostConstruct
    public void init() {
        log.info("Custom Outbox Scheduler initialized (batchSize={}, maxRetries={})",
                batchSize, maxRetries);
    }

    @Scheduled(fixedDelayString = "${outbox.scheduler.poll-interval-ms:2000}")
    public void pollOutboxRecords() {
        log.debug("Polling for pending outbox records...");

        List<OutboxRecord> pendingRecords = outboxRecordRepository
                .findByStatusAndRetryCountLessThan(
                        OutboxRecord.OutboxStatus.PENDING, maxRetries);

        List<OutboxRecord> failedRecords = outboxRecordRepository
                .findByStatusAndRetryCountLessThan(
                        OutboxRecord.OutboxStatus.FAILED, maxRetries);

        int totalToProcess = Math.min(
                pendingRecords.size() + failedRecords.size(), batchSize);

        if (totalToProcess == 0) {
            return;
        }

        log.info("Found {} records to process (pending: {}, failed: {})",
                totalToProcess, pendingRecords.size(), failedRecords.size());

        // Process pending first, then failed (by creation order)
        for (OutboxRecord record : pendingRecords) {
            outboxProcessor.processRecord(record);
        }

        for (OutboxRecord record : failedRecords) {
            outboxProcessor.processRecord(record);
        }
    }
}
