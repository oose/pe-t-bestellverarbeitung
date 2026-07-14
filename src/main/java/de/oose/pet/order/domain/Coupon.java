package de.oose.pet.order.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Ein Rabattgutschein mit optionaler Mindestbestellsumme und Ablaufdatum.
 *
 * @param code              Gutscheincode (z. B. "SOMMER10")
 * @param type              Art des Rabatts (Prozent oder Festbetrag)
 * @param value             Rabattwert: bei PERCENTAGE der Prozentsatz (z. B. 10 für 10 %),
 *                          bei FIXED_AMOUNT der Betrag in EUR
 * @param minimumOrderValue Mindestbestellwert in EUR; 0 bedeutet keine Einschränkung
 * @param exclusive         wenn {@code true}, ist dieser Gutschein nicht mit dem
 *                          Mengensrabatt kombinierbar – es gilt der jeweils größere Vorteil
 * @param expiryDate        letzter Gültigkeitstag (inklusive)
 */
public record Coupon(
        String code,
        CouponType type,
        BigDecimal value,
        BigDecimal minimumOrderValue,
        boolean exclusive,
        LocalDate expiryDate
) {
    /** @return {@code true} wenn das Ablaufdatum überschritten ist */
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }
}
