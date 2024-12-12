package andrei.mishunin.aoc2024.day11;

import andrei.mishunin.aoc2024.tools.ArrayUtils;
import andrei.mishunin.aoc2024.tools.InputReader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StoneCounter {
    static Map<String, Map<Integer, Long>> TRANSFORM_COUNT = new HashMap<>();

    public static long countStones(String file, int iterations) {
        List<String> input = InputReader.readAllLines(file);
        String[] numberS = input.get(0).split(" ");

        long[] numbers = ArrayUtils.toLong(numberS);
        long count = 0L;
        for (long number : numbers) {
            count += transformCount(number, iterations);
        }

        return count;
    }


    private static long transformCount(Long number, int steps) {
        LinkedList<Pair> stack = new LinkedList<>();
        stack.push(new Pair(number, steps));

        while (!stack.isEmpty()) {
            Pair pair = stack.removeLast();

            String numberS = Long.toString(pair.num);
            Long memo = getFromMemo(pair.num, pair.step);
            if (memo != null) {
                continue;
            }

            if (pair.num == 0) {
                memo = getFromMemo(1L, pair.step - 1);
                if (memo == null) {
                    stack.addLast(pair);
                    stack.addLast(new Pair(1L, pair.step - 1));
                } else {
                    putToMemo(pair.num, pair.step, memo);
                }
            } else if (numberS.length() % 2 == 0) {
                long n1 = Long.parseLong(numberS.substring(0, numberS.length() / 2));
                long n2 = Long.parseLong(numberS.substring(numberS.length() / 2));
                Long memo1 = getFromMemo(n1, pair.step - 1);
                Long memo2 = getFromMemo(n2, pair.step - 1);
                if (memo1 != null && memo2 != null) {
                    putToMemo(pair.num, pair.step, memo1 + memo2);
                } else {
                    stack.addLast(pair);
                    if (memo1 == null) {
                        stack.addLast(new Pair(n1, pair.step - 1));
                    }
                    if (memo2 == null) {
                        stack.addLast(new Pair(n2, pair.step - 1));
                    }
                }
            } else {
                long nextNumber = pair.num * 2024;
                memo = getFromMemo(nextNumber, pair.step - 1);
                if (memo == null) {
                    stack.addLast(pair);
                    stack.addLast(new Pair(nextNumber, pair.step - 1));
                } else {
                    putToMemo(pair.num, pair.step, memo);
                }
            }
        }

        return getFromMemo(number, steps);
    }

    public static void putToMemo(Long number, int step, Long count) {
        String numberS = number.toString();
        TRANSFORM_COUNT.computeIfAbsent(numberS, s -> new HashMap<>())
                .put(step, count);
    }

    private static Long getFromMemo(Long number, int steps) {
        if (steps == 0) {
            return 1L;
        }

        String numberS = number.toString();
        var memo = TRANSFORM_COUNT.computeIfAbsent(numberS, s -> new HashMap<>());
        if (memo.containsKey(steps)) {
            return memo.get(steps);
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(countStones("day11/test.txt", 25));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(countStones("day11/input.txt", 25));
        System.out.println("== TEST 2 ==");
        System.out.println(countStones("day11/test.txt", 75));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(countStones("day11/input.txt", 75));
    }

    private record Pair(long num, int step) {
    }
}
