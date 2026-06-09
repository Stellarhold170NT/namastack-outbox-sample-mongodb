package io.namastack.sample.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "outbox_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxRecord {

    @Id
    private String id;

    @Indexed
    private String aggregateId;

    private String aggregateType;

    private String eventType;

    private String payload;

    @Indexed
    private OutboxStatus status;

    private int retryCount;

    private String lastError;

    private LocalDateTime createdAt;

    private LocalDateTime lastAttemptAt;

    private LocalDateTime processedAt;

    public enum OutboxStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
}
