package andrei.mishunin.aoc2024.day13;

import andrei.mishunin.aoc2024.tools.InputReader;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClawMachineTokenCounter {
    private static final Pattern BUTTON_PATTERN = Pattern.compile("Button .: X\\+(\\d+), Y\\+(\\d++)");
    private static final Pattern PRIZE_PATTERN = Pattern.compile("Prize: X=(\\d+), Y=(\\d++)");

    public static long getTokensCount(String file, long coordinateShift) {
        List<String> input = InputReader.readAllLines(file);
        long allPriseCosts = 0;
        for (int i = 0; i < input.size(); i += 4) {
            Matcher aButtonM = BUTTON_PATTERN.matcher(input.get(i));
            if (!aButtonM.find()) {
                throw new RuntimeException();
            }
            long[] a = new long[]{Long.parseLong(aButtonM.group(1)), Long.parseLong(aButtonM.group(2))};

            Matcher bButtonM = BUTTON_PATTERN.matcher(input.get(i + 1));
            if (!bButtonM.find()) {
                throw new RuntimeException();
            }
            long[] b = new long[]{Long.parseLong(bButtonM.group(1)), Long.parseLong(bButtonM.group(2))};

            Matcher priseM = PRIZE_PATTERN.matcher(input.get(i + 2));
            if (!priseM.find()) {
                throw new RuntimeException();
            }
            long[] prise = new long[]{coordinateShift + Long.parseLong(priseM.group(1)), coordinateShift + Long.parseLong(priseM.group(2))};

            long priseCost = prizeCost(a, b, prise);
            if (priseCost != -1) {
                allPriseCosts += priseCost;
            }
        }

        return allPriseCosts;
    }

    private static long prizeCost(long[] a, long[] b, long[] p) {
        long bPressed = (p[1] * a[0] - a[1] * p[0]) / (b[1] * a[0] - a[1] * b[0]);
        long aPressed = (p[1] * b[0] - b[1] * p[0]) / (a[1] * b[0] - a[0] * b[1]);

        if (aPressed >= 0 && bPressed >= 0) {
            long x = b[0] * bPressed + a[0] * aPressed;
            long y = b[1] * bPressed + a[1] * aPressed;
            if (p[0] == x && p[1] == y) {
                return bPressed + aPressed * 3;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(getTokensCount("day13/test.txt", 0L));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(getTokensCount("day13/input.txt", 0L));
        System.out.println("== TEST 2 ==");
        System.out.println(getTokensCount("day13/test.txt", 10000000000000L));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(getTokensCount("day13/input.txt", 10000000000000L));
    }
}
