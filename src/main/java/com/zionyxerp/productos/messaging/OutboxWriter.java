package com.zionyxerp.productos.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.zionyxerp.productos.entity.OutboxEvent;
import com.zionyxerp.productos.event.ProductEvent;
import com.zionyxerp.productos.repository.OutboxEventRepository;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.json.JsonMapper;

/**
 * Guarda cada ProductEvent en la tabla outbox_events.
 *
 * @EventListener (no @TransactionalEventListener) se ejecuta AL MOMENTO del publishEvent(),
 * en el mismo hilo y DENTRO de la transacción de ProductService:
 *   producto + evento se confirman juntos, o se revierten juntos.
 */
@Slf4j
@Component
public class OutboxWriter {

    private final OutboxEventRepository repository;
    private final JsonMapper jsonMapper;      // Jackson 3 (lo configura Spring Boot)
    private final String topic;

    public OutboxWriter(OutboxEventRepository repository,
                        JsonMapper jsonMapper,
                        @Value("${app.kafka.topics.product-events}") String topic) {
        this.repository = repository;
        this.jsonMapper = jsonMapper;
        this.topic = topic;
    }

    @EventListener
    public void onProductEvent(ProductEvent event) {
        var outbox = new OutboxEvent(
                event.eventId(),                              // mismo id → inventario deduplica (Fase 8)
                event.payload().productId().toString(),       // key de Kafka
                topic,
                event.eventType().name(),
                jsonMapper.writeValueAsString(event));        // el JSON exacto que irá a Kafka
        repository.save(outbox);
        log.info("Evento {} del producto {} guardado en outbox", event.eventType(), outbox.getAggregateId());
    }
}