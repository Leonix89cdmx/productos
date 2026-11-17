package com.zionyxerp.productos.messaging;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.zionyxerp.productos.entity.OutboxEvent;
import com.zionyxerp.productos.repository.OutboxEventRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Lee outbox_events pendientes y los publica en Kafka.
 * Si Kafka está caído, no pasa nada: los eventos siguen en la tabla y se reintentan en el siguiente ciclo.
 */
@Slf4j
@Component
public class OutboxRelay {

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxRelay(OutboxEventRepository repository, ProducerFactory<?, ?> producerFactory) {
        this.repository = repository;
        // El payload YA es JSON → se manda como texto (StringSerializer), sin volver a serializarlo.
        // Reutilizamos la config de Boot (bootstrap-servers, acks=all, idempotencia...).
        this.kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(
                producerFactory.getConfigurationProperties(), new StringSerializer(), new StringSerializer()));
    }

    @Scheduled(fixedDelay = 1000)   // 1 s después de terminar el ciclo anterior
    @Transactional
    public void publishPending() {
        List<OutboxEvent> pending = repository.findPendingForUpdate();
        int sent = 0;

        for (OutboxEvent event : pending) {
            try {
                // .get(): esperamos la confirmación de Kafka ANTES de marcarlo como publicado
                var meta = kafkaTemplate.send(event.getTopic(), event.getAggregateId(), event.getPayload())
                        .get(10, TimeUnit.SECONDS)
                        .getRecordMetadata();

                event.markPublished();   // dirty checking: se guarda al commit
                sent++;
                log.info("Outbox → Kafka: {} producto={} partición={} offset={}",
                        event.getEventType(), event.getAggregateId(), meta.partition(), meta.offset());

            } catch (Exception ex) {
                if (ex instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                // Paramos en el primer error para NO saltarnos eventos (se respeta el orden).
                log.warn("Kafka no disponible: {} evento(s) pendiente(s), se reintenta en el siguiente ciclo ({})",
                        pending.size() - sent, ex.getMessage());
                return;
            }
        }
    }
}