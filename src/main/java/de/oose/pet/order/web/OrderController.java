package de.oose.pet.order.web;

import de.oose.pet.order.domain.Order;
import de.oose.pet.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Legt eine neue Bestellung an.
     * <p>
     * Beispiel-Request-Body:
     * <pre>
     * {
     *   "customerId": "kunde-42",
     *   "items": [
     *     { "productId": "p1", "productName": "Widget", "quantity": 3, "unitPrice": 12.50 }
     *   ],
     *   "shippingRegion": "DOMESTIC"
     * }
     * </pre>
     */
    @PostMapping
    public ResponseEntity<Order> create(@RequestBody Order order) {
        Order saved = orderService.save(Order.create(
                order.customerId(),
                order.items(),
                order.shippingRegion()
        ));
        return ResponseEntity.ok(saved);
    }

    /**
     * Liefert eine Bestellung anhand ihrer ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable String id) {
        return orderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Berechnet den Gesamtpreis einer Bestellung.
     *
     * @param id         Bestell-ID
     * @param express    optionaler Query-Parameter für Expressversand (default: false)
     */
    @GetMapping("/{id}/total")
    public ResponseEntity<Map<String, BigDecimal>> getTotal(
            @PathVariable String id,
            @RequestParam(defaultValue = "false") boolean express) {

        return orderService.findById(id)
                .map(order -> {
                    BigDecimal total = orderService.calculateTotal(order, null, express);
                    return ResponseEntity.ok(Map.of("total", total));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
