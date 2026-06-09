package io.namastack.sample.controller;

import io.namastack.sample.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public String createOrder(@RequestParam String customerId, @RequestParam BigDecimal amount) {
        String id = orderService.createOrder(customerId, amount, false);
        return "Order created (will be processed via Outbox): " + id;
    }

    @PostMapping("/failed")
    public String createFailedOrder(@RequestParam String customerId, @RequestParam BigDecimal amount) {
        String id = orderService.createOrder(customerId, amount, true);
        return "Order with simulated failure (will retry via Outbox): " + id;
    }
}
