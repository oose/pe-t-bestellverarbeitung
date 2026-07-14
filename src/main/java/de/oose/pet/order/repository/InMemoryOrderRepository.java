package de.oose.pet.order.repository;

import de.oose.pet.order.domain.Order;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-sichere In-Memory-Implementierung des {@link OrderRepository}.
 * Daten gehen beim Neustart der Anwendung verloren.
 *
 * Übungs-Hinweis (Agenten-Übung): Ersetze diese Klasse durch eine
 * Spring-Data-JPA-Implementierung mit H2. Die Schnittstelle {@link OrderRepository}
 * bleibt dabei unverändert.
 */
@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, Order> store = new ConcurrentHashMap<>();

    @Override
    public Order save(Order order) {
        store.put(order.id(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Order> findAll() {
        return List.copyOf(store.values());
    }
}
