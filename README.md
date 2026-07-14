# PE-T Beispielprojekt – Bestellverarbeitung

Kleines Spring-Boot-Backend zur Verarbeitung von Online-Bestellungen.
Dient als gemeinsame Codebasis für alle Tag-2-Übungen im Seminar
**Prompt Engineering für Entwickler (PE-T)**.

---

## Schnellstart

```bash
# 1. Tests ausführen
./mvnw test

# 2. Anwendung starten (Port 8080)
./mvnw spring-boot:run

# 3. Ersten Aufruf testen
curl -s http://localhost:8080/orders | head
```

Voraussetzung: JDK 21 muss installiert und `JAVA_HOME` gesetzt sein.
Maven-Wrapper (`mvnw`) lädt alle Abhängigkeiten beim ersten Aufruf herunter.

---

## Projektstruktur

```
src/main/java/de/oose/pet/order/
  domain/       Domänenmodell (Java 21 Records)
    Order.java          Bestellung
    OrderItem.java      Bestellposition
    Coupon.java         Rabattgutschein
    CouponType.java     Enum: PERCENTAGE | FIXED_AMOUNT
    ShippingRegion.java Enum: DOMESTIC | EU | INTERNATIONAL

  service/      Geschäftslogik
    DiscountCalculator.java   Rabatt- und Gutscheinberechnung  ← Kern-Übungsklasse
    ShippingCalculator.java   Versandkostenberechnung
    OrderService.java         Orchestrierung

  repository/   Datenhaltung
    OrderRepository.java          Interface
    InMemoryOrderRepository.java  In-Memory-Implementierung

  web/          REST-API
    OrderController.java   3 Endpunkte (POST /orders, GET /orders/{id}, GET /orders/{id}/total)

src/test/
  DiscountCalculatorTest.java   Starter-Tests (bewusst unvollständig)

spec/
  ReturnService.md   Spezifikation für Übung 4
```

---

## REST-Endpunkte

| Methode | Pfad                     | Beschreibung                            |
|---------|--------------------------|-----------------------------------------|
| POST    | `/orders`                | Neue Bestellung anlegen                 |
| GET     | `/orders/{id}`           | Bestellung abrufen                      |
| GET     | `/orders/{id}/total`     | Gesamtpreis berechnen (`?express=true`) |

---

