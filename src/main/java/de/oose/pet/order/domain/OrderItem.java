package de.oose.pet.order.domain;

import java.math.BigDecimal;

/**
 * Eine einzelne Position innerhalb einer Bestellung.
 *
 * @param productId   eindeutige Produkt-ID
 * @param productName lesbarer Produktname
 * @param quantity    bestellte Menge (muss > 0 sein)
 * @param unitPrice   Einzelpreis in EUR (muss >= 0 sein)
 */
public record OrderItem(
        String productId,
        String productName,
        int quantity,
        BigDecimal unitPrice
) {
    public OrderItem {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Menge muss größer 0 sein, war: " + quantity);
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Einzelpreis darf nicht negativ sein, war: " + unitPrice);
        }
    }
}
