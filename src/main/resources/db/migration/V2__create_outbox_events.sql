CREATE TABLE outbox_events (
    id            UUID         PRIMARY KEY,   -- = eventId del evento
    aggregate_id  VARCHAR(100) NOT NULL,      -- la key de Kafka (productId)
    topic         VARCHAR(150) NOT NULL,      -- a qué topic va
    event_type    VARCHAR(50)  NOT NULL,
    payload       TEXT         NOT NULL,      -- el JSON del evento, tal cual va a Kafka
    created_at    TIMESTAMPTZ  NOT NULL,
    published_at  TIMESTAMPTZ                 -- NULL = pendiente de enviar
);

-- Índice solo de los pendientes: el relay consulta esto cada segundo
CREATE INDEX idx_outbox_pending ON outbox_events (created_at) WHERE published_at IS NULL;