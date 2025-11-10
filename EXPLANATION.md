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
Wenn mehrere Kombinationen zum selben Produkt führen, werden **alle** in einer Liste gesammelt.

```java
// Map: Produkt → Liste aller (Tag, Monat)-Kombinationen, die dieses Produkt ergeben
private static final Map<Integer, List<int[]>> POSSIBILITIES_MAP = createPossibilitiesMap();
private static Map<Integer, List<int[]>> createPossibilitiesMap() {
    Map<Integer, List<int[]>> map = new HashMap<>();

    for (int m = 0; m < DAYS_IN_MONTH.length; m++) {      // für jeden Monat
        for (int d = 1; d <= DAYS_IN_MONTH[m]; d++) {     // für jeden möglichen Tag
            int product = d * (m + 1);                // (m+1) weil Monat bei 1 startet
            map.computeIfAbsent(product, k -> new ArrayList<>())
               .add(new int[]{d, m + 1});
        }
    }
    return map;
}
```

➡️ Beispiel:
Produkt = 40 → mögliche Kombinationen sind (10, 4) und (20, 2).
All diese Informationen werden in `POSSIBILITIES_MAP` gespeichert.

---

### 4️⃣ Zuweisung der Möglichkeiten zu den Kindern

Für jedes Kind werden nun alle Kombinationen herausgesucht, deren Produkt zu seinem Code aus der Aufgabenstellung passt.

```java
// Name → alle möglichen (Tag, Monat)-Kombinationen
public static final Map<String, List<int[]>> POSSIBLE_BIRTHDAYS = new HashMap<>();

BIRTHDAY_CODES.forEach((name, product) -> {
    POSSIBLE_BIRTHDAYS.put(name, POSSIBILITIES_MAP.getOrDefault(product, List.of()));
});
```

So entsteht eine Tabelle aller Kinder mit ihren jeweiligen Optionen.
Man könnte diese auch manuell aufschreiben, das Programm übernimmt aber die Denkarbeit.

---

### 5️⃣ Sortieren nach Schwierigkeit

Da sich für die Produktcodes einiger Kinder sehr viele Kombinationen ergeben und für andere nur wenige, werden sie nach der **Anzahl ihrer Optionen** sortiert.
So beginnt das Programm mit den Fällen, die am leichtesten zu lösen sind (also jenen, die die wenigsten Möglichkeiten haben).

```java
// Namen nach Anzahl der Möglichkeiten sortieren (kleinste zuerst)
List<String> names = new ArrayList<>(POSSIBLE_BIRTHDAYS.keySet());
names.sort(Comparator.comparingInt(n -> POSSIBLE_BIRTHDAYS.get(n).size()));
```

Diese Vorgehensweise ähnelt der Strategie, die man auch beim logischen Knobeln wählen würde: Zuerst die einfachen Fälle lösen, dann die schwierigeren. Wenn es Kinder mit Codes mit nur einer möglichen Kombination aus Tag und Monat gibt, vereinfacht das die Zuordnungen der weiteren Kombinationen drastisch, da in solch einem Fall der Monat aus der einzigen Möglichkeit diesem Kind zugeordnet werden muss, andernfalls wäre das Rätsel nicht lösbar (sofern jeder Monat nur einmal verwendet werden darf).

---

### 6️⃣ Die Kernidee: Backtracking

Der wichtigste Teil des Programms ist eine rekursive Methode namens `findAllCombinations`.
Sie probiert **systematisch alle Möglichkeiten** durch, bis nur noch eine (oder mehrere) gültige Zuordnungen übrigbleiben.
Dabei wird jedes Mal überprüft, ob ein Monat bereits vergeben ist.

```java
/**
 * Rekursive Suche nach allen gültigen Lösungen.
 * names        → Liste aller Kinder (nach Schwierigkeit (Anzahl Möglichkeiten) sortiert)
 * index        → aktuelles Kind, das gerade zugewiesen wird
 * usedMonths   → bereits vergebene Monate (keine Wiederholungen)
 * current      → aktuelle Teillösung
 * allSolutions → Liste aller vollständigen Lösungen
 */
private static void findAllCombinations(List<String> names, int index, Set<Integer> usedMonths,
                                        Map<String, int[]> current, List<Map<String, int[]>> allSolutions) {
    // Basisfall: alle Kinder zugewiesen → Lösung speichern
    if (index == names.size()) {
        allSolutions.add(new HashMap<>(current));  // tiefe Kopie speichern
        return;
    }

    String currentName = names.get(index);

    for (int[] combo : POSSIBLE_BIRTHDAYS.get(currentName)) {
        int month = combo[1];
        if (!usedMonths.contains(month)) {          // Monat noch frei?
            usedMonths.add(month);
            current.put(currentName, combo);

            // Nächstes Kind aufrufen (rekursiv)
            findAllCombinations(names, index + 1, usedMonths, current, allSolutions);

            // Rückgängigmachen (Backtracking)
            usedMonths.remove(month);
            current.remove(currentName);
        }
    }
}
```

Dieses Vorgehen nennt man **Backtracking**.
Das Programm testet einen möglichen Weg, und wenn sich später zeigt, dass er nicht funktioniert,
geht es automatisch „zurück“ und probiert eine andere Möglichkeit.
Ähnlich würde man es beim logischen Knobeln auf Papier auch machen.

---

### 7️⃣ Ausgabe und Sortierung der Ergebnisse

Nachdem alle Lösungen gefunden wurden, werden sie sortiert und ausgegeben:

```java
// Sortieren der gefundenen Lösungen nach Monats-/Tagesreihenfolge
allSolutions.sort(Comparator.comparing(sol ->
    sol.entrySet().stream()
       .sorted(Map.Entry.comparingByKey())
       .map(e -> e.getValue()[1] + "-" + e.getValue()[0])
       .reduce("", String::concat)
));

// Ausgabe
System.out.println("Gefundene gültige Kombinationen (" + allSolutions.size() + "):");
int index = 1;
for (Map<String, int[]> solution : allSolutions) {
    System.out.println("\nLösung " + index++ + ":");
    solution.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .forEach(entry -> {
            int[] combo = entry.getValue();
            System.out.printf("%-10s → Tag %2d, Monat %2d%n", entry.getKey(), combo[0], combo[1]);
        });
}
```

So sieht man am Ende alle möglichen Zuordnungen von Kindern zu Geburtstagen.

---

## 📊 Ergebnis

Das Programm gibt alle Kombinationen aus, bei denen:

* jede Person ihr korrektes Produkt (Tag × Monat) hat, **und**
* kein Monat doppelt vergeben wurde.

In den meisten Fällen ist die Lösung **eindeutig** – falls mehrere Lösungen möglich sind, werden alle angezeigt.

Hier ist die **Lösung** für die Konstellation aus der **Aufgabenstellung**:
| Name     | Tag | Monat |
| -------- | --- | ----- |
|Xenia|5|1|
|Timo|22|6|
|Veronika|8|8|
|Thomas|4|10|
|Viktoria|19|12|
|Florian|17|4|
|Mona|20|9|
|Otto|16|7|
|Hanna|8|2|
|Theo|13|5|
|Antonia|11|3|
|David|15|11|
