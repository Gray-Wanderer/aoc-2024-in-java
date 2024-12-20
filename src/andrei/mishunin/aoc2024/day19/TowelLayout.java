package andrei.mishunin.aoc2024.day19;

import andrei.mishunin.aoc2024.tools.InputReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TowelLayout {
    private static Map<String, Long> MEMO = new HashMap<>();

    public static long layout(String file, TowelCounter towelCounter) {
        MEMO = new HashMap<>();

        List<String> input = InputReader.readAllLines(file);

        var iterator = input.iterator();
        String[] availablePatternsList = iterator.next().split(", *");

        Map<Character, List<String>> availablePatternsMap = new HashMap<>();
        availablePatternsMap.put('w', new ArrayList<>());
        availablePatternsMap.put('u', new ArrayList<>());
        availablePatternsMap.put('b', new ArrayList<>());
        availablePatternsMap.put('r', new ArrayList<>());
        availablePatternsMap.put('g', new ArrayList<>());

        for (String pattern : availablePatternsList) {
            availablePatternsMap.get(pattern.charAt(0)).add(pattern);
        }

        iterator.next();

        long count = 0L;
        while (iterator.hasNext()) {
            String targetPattern = iterator.next();
            count += towelCounter.count(availablePatternsMap, targetPattern);
        }


        return count;
    }

    private static long canLayoutPattern(Map<Character, List<String>> availablePatternsMap, String targetPattern) {
        if (targetPattern.isEmpty()) {
            return 1L;
        }

        if (MEMO.containsKey(targetPattern)) {
            return MEMO.get(targetPattern);
        }

        for (String pattern : availablePatternsMap.get(targetPattern.charAt(0))) {
            if (targetPattern.startsWith(pattern) && canLayoutPattern(availablePatternsMap, targetPattern.substring(pattern.length())) > 0) {
                MEMO.put(targetPattern, 1L);
                return 1L;
            }
        }
        return 0L;
    }

    private static long countLayoutPatterns(Map<Character, List<String>> availablePatternsMap, String targetPattern) {
        if (targetPattern.isEmpty()) {
            return 1;
        }

        if (MEMO.containsKey(targetPattern)) {
            return MEMO.get(targetPattern);
        }

        long count = 0;
        for (String pattern : availablePatternsMap.get(targetPattern.charAt(0))) {
            if (targetPattern.startsWith(pattern)) {
                count += countLayoutPatterns(availablePatternsMap, targetPattern.substring(pattern.length()));
            }
        }
        MEMO.put(targetPattern, count);

        return count;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(layout("day19/test.txt", TowelLayout::canLayoutPattern));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(layout("day19/input.txt", TowelLayout::canLayoutPattern));
        System.out.println("== TEST 2 ==");
        System.out.println(layout("day19/test.txt", TowelLayout::countLayoutPatterns));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(layout("day19/input.txt", TowelLayout::countLayoutPatterns));
    }

    @FunctionalInterface
    public interface TowelCounter {
        long count(Map<Character, List<String>> availablePatternsMap, String targetPattern);
    }
}
