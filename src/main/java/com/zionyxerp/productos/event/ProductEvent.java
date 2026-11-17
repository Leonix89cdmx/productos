package com.zionyxerp.productos.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.zionyxerp.productos.entity.Product;

public record ProductEvent(
    UUID eventId,               // id único del evento → para idempotencia (Fase 8)
    ProductEventType eventType,
    Instant occurredAt,
    Payload payload
) {
    public record Payload(Long productId, String name, String description, BigDecimal price, boolean active) {}

    public static ProductEvent of(ProductEventType type, Product p) {
        return new ProductEvent(
            UUID.randomUUID(),
            type,
            Instant.now(),
            new Payload(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.isActive())
        );
    }
}