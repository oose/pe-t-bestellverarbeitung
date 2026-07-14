package de.oose.pet.order.service;

import de.oose.pet.order.domain.Coupon;
import de.oose.pet.order.domain.CouponType;
import de.oose.pet.order.domain.Order;
import de.oose.pet.order.domain.OrderItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class DiscountCalculator {

    private static final BigDecimal RATE_SMALL  = new BigDecimal("0.05");
    private static final BigDecimal RATE_LARGE  = new BigDecimal("0.10");
    private static final int        THRESHOLD_SMALL = 3;
    private static final int        THRESHOLD_LARGE = 5;

    public BigDecimal calculateTotal(Order order, Coupon coupon) {
        BigDecimal subtotal         = subtotal(order);
        BigDecimal quantityDiscount = quantityDiscount(order);

        if (coupon == null || !isApplicable(coupon, subtotal)) {
            return subtotal.subtract(quantityDiscount)
                           .setScale(2, RoundingMode.DOWN);
        }

        BigDecimal couponDiscount = couponDiscount(coupon, subtotal);

        if (coupon.exclusive()) {
            BigDecimal best = quantityDiscount.max(couponDiscount);
            return subtotal.subtract(best)
                           .setScale(2, RoundingMode.DOWN);
        }

        BigDecimal afterQuantity           = subtotal.subtract(quantityDiscount);
        BigDecimal stackedCouponDiscount   = couponDiscount(coupon, afterQuantity);
        return afterQuantity.subtract(stackedCouponDiscount)
                            .setScale(2, RoundingMode.DOWN);
    }

    BigDecimal subtotal(Order order) {
        return order.items().stream()
                .map(i -> i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    BigDecimal quantityDiscount(Order order) {
        return order.items().stream()
                .map(this::lineDiscount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal lineDiscount(OrderItem item) {
        BigDecimal lineTotal = item.unitPrice().multiply(BigDecimal.valueOf(item.quantity()));
        if (item.quantity() >= THRESHOLD_LARGE) {
            return lineTotal.multiply(RATE_LARGE);
        }
        if (item.quantity() >= THRESHOLD_SMALL) {
            return lineTotal.multiply(RATE_SMALL);
        }
        return BigDecimal.ZERO;
    }

    private boolean isApplicable(Coupon coupon, BigDecimal subtotal) {
        return !coupon.isExpired()
                && subtotal.compareTo(coupon.minimumOrderValue()) >= 0;
    }

    private BigDecimal couponDiscount(Coupon coupon, BigDecimal base) {
        return switch (coupon.type()) {
            case PERCENTAGE ->
                base.multiply(coupon.value())
                    .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
            case FIXED_AMOUNT ->
                coupon.value().min(base);
        };
    }
}
