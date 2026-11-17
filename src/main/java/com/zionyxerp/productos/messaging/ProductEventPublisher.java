package com.zionyxerp.productos.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import com.zionyxerp.productos.event.ProductEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProductEventPublisher {

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;
    private final String topic;

    public ProductEventPublisher(KafkaTemplate<String, ProductEvent> kafkaTemplate,
                                 @Value("${app.kafka.topics.product-events}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(ProductEvent event) {
        String key = event.payload().productId().toString();   // misma key → misma partición → orden

        kafkaTemplate.send(topic, key, event).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Error publicando {} del producto {}", event.eventType(), key, ex);
            } else {
                var meta = result.getRecordMetadata();
                log.info("Publicado {} producto={} partición={} offset={}",
                        event.eventType(), key, meta.partition(), meta.offset());
            }
        });
    }
}