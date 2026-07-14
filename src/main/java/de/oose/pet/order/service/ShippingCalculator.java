package de.oose.pet.order.service;

import de.oose.pet.order.domain.Order;
import de.oose.pet.order.domain.ShippingRegion;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ShippingCalculator {

    // Freiversandschwelle in EUR (gilt nur für Inland, kein Express)
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("50.00");

    // Grundpreise nach Gewichtsklasse
    private static final BigDecimal RATE_STANDARD = new BigDecimal("3.90"); // bis 1 kg
    private static final BigDecimal RATE_MEDIUM   = new BigDecimal("5.90"); // bis 5 kg
    private static final BigDecimal RATE_HEAVY    = new BigDecimal("9.90"); // über 5 kg

    // Regionale Aufschläge
    private static final BigDecimal SURCHARGE_EU            = new BigDecimal("4.00");
    private static final BigDecimal SURCHARGE_INTERNATIONAL = new BigDecimal("12.00");

    // Express-Aufschlag
    private static final BigDecimal SURCHARGE_EXPRESS = new BigDecimal("5.00");

    /**
     * Berechnet die Versandkosten einer Bestellung.
     *
     * @param order        die Bestellung (Lieferregion und Artikel)
     * @param orderTotal   Warenwert nach Rabatten in EUR
     * @param express      {@code true} für Express-Versand
     * @return Versandkosten in EUR (0 bei Freiversand)
     */
    public BigDecimal calculate(Order order, BigDecimal orderTotal, boolean express) {
        if (orderTotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0
                && order.shippingRegion() == ShippingRegion.DOMESTIC
                && !express) {
            return BigDecimal.ZERO;
        }

        BigDecimal weight    = estimatedWeightGrams(order);
        BigDecimal base      = baseRate(weight);
        BigDecimal regional  = regionalSurcharge(order.shippingRegion());
        BigDecimal expressFee = express ? SURCHARGE_EXPRESS : BigDecimal.ZERO;

        return base.add(regional).add(expressFee);
    }

    // Vereinfachte Gewichtsschätzung: 250 g pro Artikel
    private BigDecimal estimatedWeightGrams(Order order) {
        int totalItems = order.items().stream()
                .mapToInt(i -> i.quantity())
                .sum();
        return BigDecimal.valueOf(totalItems * 250L);
    }

    private BigDecimal baseRate(BigDecimal weightGrams) {
        if (weightGrams.compareTo(BigDecimal.valueOf(1_000)) <= 0) return RATE_STANDARD;
        if (weightGrams.compareTo(BigDecimal.valueOf(5_000)) <= 0) return RATE_MEDIUM;
        return RATE_HEAVY;
    }

    private BigDecimal regionalSurcharge(ShippingRegion region) {
        return switch (region) {
            case DOMESTIC      -> BigDecimal.ZERO;
            case EU            -> SURCHARGE_EU;
            case INTERNATIONAL -> SURCHARGE_INTERNATIONAL;
        };
    }
}
