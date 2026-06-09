package io.namastack.sample.repository;

import io.namastack.sample.model.OutboxRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRecordRepository extends MongoRepository<OutboxRecord, String> {

    List<OutboxRecord> findByStatusAndRetryCountLessThan(
            OutboxRecord.OutboxStatus status, int maxRetries);

    List<OutboxRecord> findByStatusOrderByCreatedAtAsc(OutboxRecord.OutboxStatus status);
}
