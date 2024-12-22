package andrei.mishunin.aoc2024.day22;

import andrei.mishunin.aoc2024.tools.InputReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MonkeyMarket {
    private static final int SECRET_NUMBER_SEQUENCE_LIMIT = 2000;

    public static long get2000thSecretNumbers(String file) {
        List<String> input = InputReader.readAllLines(file);

        long sum = 0;
        for (String s : input) {
            sum += getNextNumberAfter2000thIterations(Long.parseLong(s));
        }

        return sum;
    }

    private static long getNextNumberAfter2000thIterations(long secret) {
        int iterations = SECRET_NUMBER_SEQUENCE_LIMIT;
        while (iterations-- > 0) {
            secret = generateNextNumber(secret);
        }
        return secret;
    }


    public static long solve2(String file) {
        List<String> input = InputReader.readAllLines(file);
        List<Map<PriceChanges, Integer>> bananasForSequences = new ArrayList<>(input.size());

        for (String s : input) {
            bananasForSequences.add(buildPriceChangeMap(Long.parseLong(s)));
        }

        Set<PriceChanges> allChanges = new HashSet<>();
        for (Map<PriceChanges, Integer> bananasForSequence : bananasForSequences) {
            allChanges.addAll(bananasForSequence.keySet());
        }

        long maxBananas = 0;
        for (PriceChanges change : allChanges) {
            long bananas = 0;
            for (Map<PriceChanges, Integer> bananasForSequence : bananasForSequences) {
                bananas += bananasForSequence.getOrDefault(change, 0);
            }
            if (maxBananas < bananas) {
                maxBananas = bananas;
            }
        }

        return maxBananas;
    }

    private static Map<PriceChanges, Integer> buildPriceChangeMap(long secret) {
        int[] values = new int[SECRET_NUMBER_SEQUENCE_LIMIT + 1];
        values[0] = (int) (secret % 10);
        for (int i = 1; i <= SECRET_NUMBER_SEQUENCE_LIMIT; i++) {
            secret = generateNextNumber(secret);
            values[i] = (int) (secret % 10);
        }

        int[] diff = new int[SECRET_NUMBER_SEQUENCE_LIMIT + 1];
        for (int i = 1; i <= SECRET_NUMBER_SEQUENCE_LIMIT; i++) {
            diff[i] = values[i] - values[i - 1];
        }

        Map<PriceChanges, Integer> map = new HashMap<>();
        for (int i = 4; i < values.length; i++) {
            PriceChanges priceChanges = new PriceChanges(diff[i - 3], diff[i - 2], diff[i - 1], diff[i]);
            map.putIfAbsent(priceChanges, values[i]);
        }
        return map;
    }

    private static long generateNextNumber(long secret) {
        long tmp = secret * 64;
        secret ^= tmp;
        secret %= 16777216;

        tmp = secret / 32;
        secret ^= tmp;
        secret %= 16777216;

        tmp = secret * 2048;
        secret ^= tmp;
        secret %= 16777216;

        return secret;
    }

    record PriceChanges(int i0, int i1, int i2, int i3) {
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(get2000thSecretNumbers("day22/test.txt"));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(get2000thSecretNumbers("day22/input.txt"));
        System.out.println("== TEST 2 ==");
        System.out.println(solve2("day22/test2.txt"));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(solve2("day22/input.txt"));
    }
}
