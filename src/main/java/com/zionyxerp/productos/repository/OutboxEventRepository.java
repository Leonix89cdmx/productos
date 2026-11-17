package com.zionyxerp.productos.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.zionyxerp.productos.entity.OutboxEvent;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    /**
     * Los pendientes más viejos primero (respeta el orden).
     * FOR UPDATE SKIP LOCKED: si hay 2 instancias de productos, cada una toma filas distintas
     * (las que otra ya bloqueó se saltan) → no envían el mismo evento al mismo tiempo.
     */
    @Query(value = """
            SELECT * FROM outbox_events
            WHERE published_at IS NULL
            ORDER BY created_at
            LIMIT 100
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<OutboxEvent> findPendingForUpdate();
}