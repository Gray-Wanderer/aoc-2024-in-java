package andrei.mishunin.aoc2024.day03;

import andrei.mishunin.aoc2024.tools.InputReader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MullTiplier {
    public static long simpleMull(String file) {
        String memory = String.join("\n", InputReader.readAllLines(file));

        Matcher multMatcher = Pattern.compile("mul\\((\\d+),(\\d+)\\)").matcher(memory);

        long sum = 0;

        while (multMatcher.find()) {
            int num1 = Integer.parseInt(multMatcher.group(1));
            int num2 = Integer.parseInt(multMatcher.group(2));
            sum += num1 * num2;
        }

        return sum;
    }


    public static long restrictedMull(String file) {
        String memory = String.join("\n", InputReader.readAllLines(file));

        Matcher mulMatcher = Pattern.compile("mul\\((\\d+),(\\d+)\\)").matcher(memory);
        Matcher doMatcher = Pattern.compile("do\\(\\)").matcher(memory);
        Matcher dontMatcher = Pattern.compile("don't\\(\\)").matcher(memory);

        int doIndex = 0;
        int dontIndex = getOrDefault(dontMatcher, memory.length());

        long sum = 0;

        while (mulMatcher.find()) {
            while (mulMatcher.start() >= dontIndex) {
                while (doIndex < dontIndex) {
                    doIndex = getOrDefault(doMatcher, memory.length());
                }
                while (dontIndex < doIndex) {
                    dontIndex = getOrDefault(dontMatcher, memory.length());
                }
            }

            if (mulMatcher.start() < doIndex) {
                continue;
            }

            int num1 = Integer.parseInt(mulMatcher.group(1));
            int num2 = Integer.parseInt(mulMatcher.group(2));

            sum += num1 * num2;
        }

        return sum;
    }

    private static int getOrDefault(Matcher matcher, int defaultValue) {
        if (matcher.find()) {
            return matcher.end();
        } else {
            return defaultValue;
        }
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(simpleMull("day03/test.txt"));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(simpleMull("day03/input.txt"));
        System.out.println("== TEST 2 ==");
        System.out.println(restrictedMull("day03/test2.txt"));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(restrictedMull("day03/input.txt"));
    }
}
