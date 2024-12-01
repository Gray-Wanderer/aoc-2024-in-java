package andrei.mishunin.aoc2024.day01;


import andrei.mishunin.aoc2024.tools.InputReader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListComparator {
    public static int calcDistances(String file) {
        List<String> input = InputReader.readAllLines(file);

        int n = input.size();
        int[] listNumbers1 = new int[n];
        int[] listNumbers2 = new int[n];

        int i = 0;
        for (String s : input) {
            String[] numbers = s.split(" +");
            listNumbers1[i] = Integer.parseInt(numbers[0]);
            listNumbers2[i] = Integer.parseInt(numbers[1]);
            i++;
        }

        Arrays.sort(listNumbers1);
        Arrays.sort(listNumbers2);

        int sumDistances = 0;
        for (i = 0; i < n; i++) {
            sumDistances += Math.abs(listNumbers1[i] - listNumbers2[i]);
        }

        return sumDistances;
    }

    public static int calcSimilarityScore(String file) {
        List<String> input = InputReader.readAllLines(file);

        int n = input.size();
        int[] listNumbers1 = new int[n];
        Map<Integer, Integer> numberFrequency2 = new HashMap<>();

        int i = 0;
        for (String s : input) {
            String[] numbers = s.split(" +");
            listNumbers1[i++] = Integer.parseInt(numbers[0]);
            numberFrequency2.compute(Integer.parseInt(numbers[1]), (k, v) -> v == null ? 1 : v + 1);
        }

        int similarityScore = 0;
        for (i = 0; i < n; i++) {
            similarityScore += listNumbers1[i] * numberFrequency2.getOrDefault(listNumbers1[i], 0);
        }

        return similarityScore;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(calcDistances("day01/test.txt"));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(calcDistances("day01/input.txt"));
        System.out.println("== TEST 2 ==");
        System.out.println(calcSimilarityScore("day01/test.txt"));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(calcSimilarityScore("day01/input.txt"));
    }
}
