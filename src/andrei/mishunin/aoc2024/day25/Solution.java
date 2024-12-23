package andrei.mishunin.aoc2024.day25;

import andrei.mishunin.aoc2024.tools.InputReader;

import java.util.List;

public class Solution {
    public static long solve(String file) {
        List<String> input = InputReader.readAllLines(file);

        throw new RuntimeException();
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(solve("day25/test.txt"));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(solve("day25/input.txt"));
        System.out.println("== TEST 2 ==");
        System.out.println(solve("day25/test.txt"));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(solve("day25/input.txt"));
    }
}
