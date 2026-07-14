package de.oose.pet.order.repository;

import de.oose.pet.order.domain.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    /** Speichert eine Bestellung. Überschreibt vorhandene Einträge mit gleicher ID. */
    Order save(Order order);

    /** Liefert eine Bestellung anhand ihrer ID oder {@link Optional#empty()}. */
    Optional<Order> findById(String id);

    /** Liefert alle gespeicherten Bestellungen. */
    List<Order> findAll();
}
