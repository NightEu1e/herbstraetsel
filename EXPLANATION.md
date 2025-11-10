# Ausführliche Erklärung des Lösungswegs – Herbsträtsel 2025

### John Adept Ade & Uwe Alpha, 10a
---

Im Folgenden wird unser Lösungsweg für das Herbsträtsel 2025 detailliert erklärt.
Zur Lösung des Rätsels wurde von Johannes Ade ein kleines Java-Programm geschrieben, das den gesamten Prozess systematisch durchführt.
Die Schritte, die das Programm durchläuft, könnte man so auch **per Hand** umsetzen, um auf die richtige Lösung zu kommen. Das Programm erledigt dies jedoch wesentlich schneller und arbeitet fehlerfrei.

---

## ⚙️ Vorgehensweise im Programm

### 1️ Vorbereitung: Definition der Monatslängen

Da das Jahr 2010 kein Schaltjahr war, hat der Februar nur 28 Tage.
Diese Information wird als Array gespeichert:

```java
// Anzahl der Tage in jedem Monat (beginnend bei Januar)
private static final int[] DAYS_IN_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
```

Damit weiß das Programm später, bis zu welchem Tag es für den jeweiligen Monat die Produkte berechnen soll.

---

### 2️⃣ Eingabe der verschlüsselten Geburtstage

Die vorgegeben Codes aus der Aufgabenstellung werden als **Map** (Zuordnungstabelle) gespeichert:

```java
// Zuordnung: Name → verschlüsselter Geburtstags-Code (Tag × Monat)
private static final Map<String, Integer> BIRTHDAY_CODES = Map.ofEntries(
    Map.entry("Xenia", 5),
    Map.entry("Timo", 132),
    Map.entry("Veronika", 64),
    Map.entry("Thomas", 40),
    Map.entry("Viktoria", 228),
    Map.entry("Florian", 68),
    Map.entry("Mona", 180),
    Map.entry("Otto", 112),
    Map.entry("Hanna", 16),
    Map.entry("Theo", 65),
    Map.entry("Antonia", 33),
    Map.entry("David", 165)
);
```

Damit kennt das Programm für jedes Kind den Produkt-Code, der ausgerechnet werden muss.

---

### 3️⃣ Ermittlung aller möglichen Kombinationen

Nun werden **alle theoretisch möglichen Kombinationen** von Tag und Monat berechnet, die innerhalb des Jahres 2010 existieren.
Jede Kombination wird als Produkt gespeichert.
Wenn mehrere Kombinationen zum s
