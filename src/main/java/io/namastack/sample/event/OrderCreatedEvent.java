package io.namastack.sample.event;

import io.namastack.outbox.annotation.OutboxEvent;
import java.math.BigDecimal;
import java.util.List;

@OutboxEvent(
    key = "#this.orderId",
    context = {
        @OutboxEvent.OutboxContextEntry(key = "customerId", value = "#this.customerId"),
        @OutboxEvent.OutboxContextEntry(key = "shouldFail", value = "#this.fail.toString()")
    }
)
public record OrderCreatedEvent(
    String orderId,
    String customerId,
    String customerName,
    BigDecimal totalAmount,
    String shippingAddress,
    List<ItemInfo> items,
    Boolean fail // Used for failure demo
) {
    public record ItemInfo(String productId, String productName, int quantity, BigDecimal price) {}
}
