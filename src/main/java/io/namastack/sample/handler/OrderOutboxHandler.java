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

    /**
     * Hàm xử lý chính - Demo cả Thành công và Exception
     */
    @OutboxHandler
    public void handleOrderCreated(OrderCreatedEvent event, OutboxRecordMetadata metadata) {
        log.info("--- [Outbox Worker] Đang quét bản ghi: {} ---", event.orderId());

        if (Boolean.TRUE.equals(event.fail())) {
            log.error("[DEMO LỖI] Đang ném Exception cho Order: {}", event.orderId());
            throw new RuntimeException("Lỗi hệ thống giả lập để demo Retry!");
        }

        log.info("[THÀNH CÔNG] Đã xử lý xong Order: {}", event.orderId());
    }

    /**
     * Hàm bắt lỗi (Fallback) - Chạy khi record đã thất bại vĩnh viễn (sau 3 lần retry)
     */
    @OutboxFallbackHandler
    public void handleFallback(OrderCreatedEvent event, OutboxFailureContext context) {
        log.error("!!! [FALLBACK] Xử lý thất bại vĩnh viễn bản ghi: {} !!!", event.orderId());
        log.error("Số lần đã thử: {}. Lý do lỗi: {}", 
            context.getFailureCount(), 
            context.getLastFailure() != null ? context.getLastFailure().getMessage() : "Unknown");
        
        // Tại đây bạn có thể gửi thông báo cho Admin hoặc đẩy vào Dead Letter Topic (DLT)
        log.warn("Đã lưu bản ghi lỗi vào hệ thống giám sát. Ép lỗi Fallback để demo PERMANENTLY_FAILED.");
        throw new RuntimeException("Fallback cũng thất bại!");
    }
}
