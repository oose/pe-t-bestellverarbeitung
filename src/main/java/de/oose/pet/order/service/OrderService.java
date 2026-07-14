package de.oose.pet.order.service;

import de.oose.pet.order.domain.Coupon;
import de.oose.pet.order.domain.Order;
import de.oose.pet.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository    orderRepository;
    private final DiscountCalculator discountCalculator;
    private final ShippingCalculator shippingCalculator;

    public OrderService(OrderRepository orderRepository,
                        DiscountCalculator discountCalculator,
                        ShippingCalculator shippingCalculator) {
        this.orderRepository    = orderRepository;
        this.discountCalculator = discountCalculator;
        this.shippingCalculator = shippingCalculator;
    }

    /**
     * Speichert eine Bestellung und gibt sie mit vergebener ID zurück.
     */
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    /**
     * Sucht eine Bestellung anhand ihrer ID.
     */
    public Optional<Order> findById(String id) {
        return orderRepository.findById(id);
    }

    /**
     * Berechnet den Gesamtbetrag einer Bestellung (Warenwert + Versand, nach Rabatten).
     *
     * @param order   die Bestellung
     * @param coupon  optionaler Gutschein ({@code null} erlaubt)
     * @param express ob Expresslieferung gewünscht ist
     * @return Gesamtbetrag in EUR
     */
    public BigDecimal calculateTotal(Order order, Coupon coupon, boolean express) {
        BigDecimal itemTotal = discountCalculator.calculateTotal(order, coupon);
        BigDecimal shipping  = shippingCalculator.calculate(order, itemTotal, express);
        return itemTotal.add(shipping);
    }
}
