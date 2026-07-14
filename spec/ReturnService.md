# Spezifikation: ReturnService

> Übungsaufgabe: Implementiere den `ReturnService` auf Basis dieser Spezifikation.
> Nutze dein LLM, um den Code zu generieren – und bewerte, wie nah das Ergebnis
> an den beschriebenen Regeln liegt. Was musste nachgebessert werden?

---

## Kontext

Bestellungen können vollständig oder teilweise retourniert werden.
Der `ReturnService` berechnet den Erstattungsbetrag und wirft eine Exception,
wenn die Rückgabefrist abgelaufen ist.

---

## Fachliche Regeln

### 1. Rückgabefrist

- Rücksendungen sind bis einschließlich **30 Tage** nach dem Bestelldatum möglich.
- Wird die Frist überschritten, wirft der Service eine `ReturnNotAllowedException`
  (checked Exception, eigene Klasse – muss ebenfalls implementiert werden).

### 2. Erstattungsbetrag

- Der Erstattungsbetrag entspricht dem **anteiligen Warenwert nach Rabatten**
  der retournierten Positionen.
- Anteil je Position = `(Positionswert / Gesamt-Warenwert) × bezahlter Warenwert`
- Der `originalTotal`-Parameter enthält den tatsächlich bezahlten Gesamtbetrag
  **inklusive** Versandkosten.

### 3. Versandkosten bei Retouren

- Versandkosten werden **nur erstattet**, wenn die gesamte Bestellung retourniert wird.
- Bei einer **Teilretoure**: Wenn der verbleibende Warenwert (nach Rabatten, geschätzt)
  die Freiversandschwelle von **50,00 EUR unterschreitet** und der ursprüngliche Versand
  kostenlos war, werden die regulären Standardversandkosten (**3,90 EUR**) vom
  Erstattungsbetrag abgezogen.
- War Versand bereits bezahlt, ändert sich an der Erstattung der Versandkosten nichts.

### 4. Gutscheine

- Gutscheincodes werden **nicht wieder aufgeladen**.
- Der Rabattanteil des Gutscheins wird proportional berücksichtigt
  (er ist bereits im `originalTotal` eingerechnet).

---

## Schnittstelle

```java
package de.oose.pet.order.service;

import de.oose.pet.order.domain.Order;
import de.oose.pet.order.domain.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ReturnService {

    /**
     * Verarbeitet eine Retoure und berechnet den Erstattungsbetrag.
     *
     * @param order          die ursprüngliche Bestellung
     * @param returnedItems  zurückgesendete Positionen (mit tatsächlich retournierter Menge)
     * @param originalTotal  tatsächlich bezahlter Gesamtbetrag in EUR (Ware + Versand, nach Rabatten)
     * @param orderDate      Datum der ursprünglichen Bestellung
     * @return               Erstattungsbetrag in EUR (>= 0, auf 2 Dezimalstellen gerundet)
     * @throws ReturnNotAllowedException wenn die 30-Tage-Rückgabefrist überschritten ist
     */
    public BigDecimal processReturn(Order order,
                                    List<OrderItem> returnedItems,
                                    BigDecimal originalTotal,
                                    LocalDate orderDate) throws ReturnNotAllowedException {
        // TODO: implementieren
        throw new UnsupportedOperationException("Noch nicht implementiert");
    }
}
```

---

## Beispiele

| Szenario                                    | Warenwert | Retourniert  | Versand bezahlt | Erstattung                              |
|---------------------------------------------|-----------|--------------|-----------------|----------------------------------------|
| Komplettretoure, Standardversand            | 30,00 €   | alles        | 3,90 €          | **33,90 €**                            |
| Komplettretoure, Freiversand                | 60,00 €   | alles        | 0,00 €          | **60,00 €**                            |
| Teilretoure, bleibt über 50 €               | 80,00 €   | 20,00 € Ware | 0,00 €          | **20,00 €**                            |
| Teilretoure, fällt unter 50 €               | 70,00 €   | 30,00 € Ware | 0,00 €          | **26,10 €** *(30,00 − 3,90 Nachberechnung)* |
| Retoure am Tag 30 (noch zulässig)           | beliebig  | beliebig     | –               | Erstattung wird berechnet              |
| Retoure am Tag 31 (Frist überschritten)     | beliebig  | beliebig     | –               | **ReturnNotAllowedException**          |

---

## Hinweise

- Der `ShippingCalculator` (im Package `service`) enthält bereits die Versandkostenlogik
  und kann wiederverwendet werden.
- Die Freiversandschwelle (50,00 EUR) und der Standardversandpreis (3,90 EUR) sind
  Konstanten – sie können intern fest codiert oder aus dem `ShippingCalculator` bezogen werden.
- Runde den Erstattungsbetrag mit `RoundingMode.DOWN` auf 2 Dezimalstellen.
