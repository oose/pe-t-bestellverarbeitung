package de.oose.pet.order.domain;

import java.util.List;
import java.util.UUID;

/**
 * Eine Bestellung, bestehend aus einer oder mehreren Positionen.
 *
 * @param id             eindeutige Bestell-ID (UUID)
 * @param customerId     Kundennummer
 * @param items          bestellte Positionen (unveränderliche Kopie)
 * @param shippingRegion Lieferregion, bestimmt Versandkosten
 */
public record Order(
        String id,
        String customerId,
        List<OrderItem> items,
        ShippingRegion shippingRegion
) {
    public Order {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Bestellung muss mindestens eine Position enthalten");
        }
        items = List.copyOf(items);
    }

    /** Erzeugt eine neue Bestellung mit automatisch generierter ID. */
    public static Order create(String customerId,
                               List<OrderItem> items,
                               ShippingRegion shippingRegion) {
        return new Order(UUID.randomUUID().toString(), customerId, items, shippingRegion);
    }
}
