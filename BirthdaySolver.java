import java.util.*;

public class BirthdaySolver {

    // Anzahl der Tage in jedem Monat (2010 war kein Schaltjahr)
    private static final int[] DAYS_IN_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    // Name → verschlüsselter Code (Tag × Monat)
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

    // Produkt → alle (Tag, Monat)-Kombinationen
    private static final Map<Integer, List<int[]>> POSSIBILITIES_MAP = createPossibilitiesMap();

    // Name → mögliche Kombinationen
    private static final Map<String, List<int[]>> POSSIBLE_BIRTHDAYS = new HashMap<>();

    public static void main(String[] args) {
        // Mögliche Kombinationen für jedes Kind berechnen
        BIRTHDAY_CODES.forEach((name, product) -> 
            POSSIBLE_BIRTHDAYS.put(name, POSSIBILITIES_MAP.getOrDefault(product, List.of()))
        );

        // Kinder nach Anzahl ihrer Möglichkeiten sortieren
        List<String> names = new ArrayList<>(POSSIBLE_BIRTHDAYS.keySet());
        names.sort(Comparator.comparingInt(n -> POSSIBLE_BIRTHDAYS.get(n).size()));

        // Backtracking starten
        List<Map<String, int[]>> allSolutions = new ArrayList<>();
        findAllCombinations(names, 0, new HashSet<>(), new HashMap<>(), allSolutions);

        // Ausgabe
        printSolutions(allSolutions);
    }

    private static Map<Integer, List<int[]>> createPossibilitiesMap() {
        Map<Integer, List<int[]>> map = new HashMap<>();

        for (int month = 0; month < DAYS_IN_MONTH.length; month++) {
            for (int day = 1; day <= DAYS_IN_MONTH[month]; day++) {
                int product = day * (month + 1);
                map.computeIfAbsent(product, k -> new ArrayList<>())
                   .add(new int[]{day, month + 1});
            }
        }
        return map;
    }

    // Rekursive Suche nach allen gültigen Kombinationen
    private static void findAllCombinations(List<String> names, int index, Set<Integer> usedMonths,
                                            Map<String, int[]> current, List<Map<String, int[]>> allSolutions) {
        if (index == names.size()) {
            allSolutions.add(new HashMap<>(current));
            return;
        }

        String currentName = names.get(index);
        for (int[] combo : POSSIBLE_BIRTHDAYS.get(currentName)) {
            int month = combo[1];
            if (!usedMonths.contains(month)) {
                usedMonths.add(month);
                current.put(currentName, combo);

                findAllCombinations(names, index + 1, usedMonths, current, allSolutions);

                usedMonths.remove(month);
                current.remove(currentName);
            }
        }
    }

    private static void printSolutions(List<Map<String, int[]>> allSolutions) {
        System.out.println("Gefundene gültige Kombinationen (" + allSolutions.size() + "):");

        int index = 1;
        for (Map<String, int[]> solution : allSolutions) {
            System.out.println("\nLösung " + index++ + ":");
            System.out.printf("%-12s %-6s %-6s%n", "Name", "Tag", "Monat");
            System.out.println("------------------------");

            solution.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    int[] combo = entry.getValue();
                    System.out.printf("%-12s %2d     %2d%n", entry.getKey(), combo[0], combo[1]);
                });
        }
    }
}
