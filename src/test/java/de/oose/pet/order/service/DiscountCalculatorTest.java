package de.oose.pet.order.service;

import de.oose.pet.order.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Starter-Tests für den DiscountCalculator.
 *
 * Diese Testklasse ist bewusst unvollständig.
 *
 * Übungsaufgabe: Erweitere die Tests, sodass alle fachlichen Regeln
 * des DiscountCalculators abgedeckt sind. Nutze dein LLM, um Testfälle
 * zu generieren – und bewerte anschließend, welche Fälle es gefunden hat
 * und welche es übersehen hat.
 *
 * Hinweise zu noch fehlenden Szenarien (nicht vollständig):
 *  - Mengensrabatt genau an der Grenze (2 Artikel, 3 Artikel, 4 Artikel, 5 Artikel)
 *  - Exklusiver Gutschein: Welcher Vorteil gewinnt?
 *  - Nicht-exklusiver Gutschein: Stapelreihenfolge korrekt?
 *  - Gutschein-Mindestbestellwert knapp nicht erfüllt
 *  - Abgelaufener Gutschein
 *  - Rundungsverhalten bei krummen Beträgen
 *  - Bestellung mit mehreren unterschiedlichen Positionen
 */
class DiscountCalculatorTest {

    private DiscountCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new DiscountCalculator();
    }

    // -------------------------------------------------------------------------
    // Kein Rabatt
    // -------------------------------------------------------------------------

    @Test
    void ohneRabatt_einzelnePosition_gibtWarenwertZurueck() {
        Order order = Order.create("k1", List.of(
                new OrderItem("p1", "Widget", 1, new BigDecimal("10.00"))
        ), ShippingRegion.DOMESTIC);

        BigDecimal total = calculator.calculateTotal(order, null);

        assertThat(total).isEqualByComparingTo("10.00");
    }

    // -------------------------------------------------------------------------
    // Mengenrabatt
    // -------------------------------------------------------------------------

    @Test
    void mengenrabatt_dreiGleicheArtikel_fuenfProzentRabatt() {
        Order order = Order.create("k1", List.of(
                new OrderItem("p1", "Widget", 3, new BigDecimal("10.00"))
        ), ShippingRegion.DOMESTIC);

        BigDecimal total = calculator.calculateTotal(order, null);

        // 3 × 10,00 = 30,00 − 5 % = 28,50
        assertThat(total).isEqualByComparingTo("28.50");
    }

    @Test
    void mengenrabatt_fuenfGleicheArtikel_zehnProzentRabatt() {
        Order order = Order.create("k1", List.of(
                new OrderItem("p1", "Widget", 5, new BigDecimal("10.00"))
        ), ShippingRegion.DOMESTIC);

        BigDecimal total = calculator.calculateTotal(order, null);

        // 5 × 10,00 = 50,00 − 10 % = 45,00
        assertThat(total).isEqualByComparingTo("45.00");
    }

    // -------------------------------------------------------------------------
    // Gutschein
    // -------------------------------------------------------------------------

    @Test
    void gutschein_prozentuell_wirdVomWarenwertAbgezogen() {
        Order order = Order.create("k1", List.of(
                new OrderItem("p1", "Widget", 1, new BigDecimal("100.00"))
        ), ShippingRegion.DOMESTIC);
        Coupon coupon = new Coupon("TEST10", CouponType.PERCENTAGE,
                new BigDecimal("10"), BigDecimal.ZERO, false,
                LocalDate.now().plusDays(30));

        BigDecimal total = calculator.calculateTotal(order, coupon);

        assertThat(total).isEqualByComparingTo("90.00");
    }

    // TODO: Weitere Testfälle hier ergänzen ↓
}
