package andrei.mishunin.aoc2024.day05;

import andrei.mishunin.aoc2024.tools.ArrayUtils;
import andrei.mishunin.aoc2024.tools.InputReader;
import andrei.mishunin.aoc2024.tools.IntPair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SafetyManualSorter {
    public static IntPair fixOrderAndCount(String file) {
        List<String> input = InputReader.readAllLines(file);
        Map<Integer, Set<Integer>> pageOrders = new HashMap<>();

        for (int i = 0; i < 100; i++) {
            pageOrders.put(i, new HashSet<>());
        }

        Iterator<String> str = input.iterator();
        while (str.hasNext()) {
            String line = str.next();

            if (line.isEmpty()) {
                break;
            }

            String[] order = line.split("\\|");
            pageOrders.get(Integer.parseInt(order[0])).add(Integer.parseInt(order[1]));
        }

        int correctSum = 0;
        int reorderedSum = 0;
        while (str.hasNext()) {
            Integer[] pagesToUpdate = ArrayUtils.toInteger(str.next().split(","));

            if (isCorrectOrder(pagesToUpdate, pageOrders)) {
                correctSum += pagesToUpdate[pagesToUpdate.length / 2];
            } else {
                Arrays.sort(pagesToUpdate, (a, b) -> {
                    if (pageOrders.get(a).contains(b)) {
                        return 1;
                    } else if (pageOrders.get(b).contains(a)) {
                        return -1;
                    } else {
                        return 0;
                    }
                });

                reorderedSum += pagesToUpdate[pagesToUpdate.length / 2];
            }
        }

        return new IntPair(correctSum, reorderedSum);
    }

    private static boolean isCorrectOrder(Integer[] pages, Map<Integer, Set<Integer>> pageOrders) {
        boolean[] previousPages = new boolean[100];
        for (int page : pages) {
            for (Integer forbiddenPages : pageOrders.get(page)) {
                if (previousPages[forbiddenPages]) {
                    return false;
                }
            }
            previousPages[page] = true;
        }
        return true;
    }

    public static void main(String[] args) {
        IntPair test = fixOrderAndCount("day05/test.txt");
        IntPair input = fixOrderAndCount("day05/input.txt");

        System.out.println("== TEST 1 ==");
        System.out.println(test.i());
        System.out.println("== SOLUTION 1 ==");
        System.out.println(input.i());
        System.out.println("== TEST 2 ==");
        System.out.println(test.j());
        System.out.println("== SOLUTION 2 ==");
        System.out.println(input.j());
    }
}
