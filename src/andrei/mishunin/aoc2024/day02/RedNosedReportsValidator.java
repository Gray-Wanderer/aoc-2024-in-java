package andrei.mishunin.aoc2024.day02;

import andrei.mishunin.aoc2024.tools.ArrayUtils;
import andrei.mishunin.aoc2024.tools.InputReader;

import java.util.List;

public class RedNosedReportsValidator {
    public static long safeReportCount(String file, boolean tolerate) {
        List<String> input = InputReader.readAllLines(file);
        int safeCount = 0;

        for (String reportLine : input) {
            int[] data = ArrayUtils.toInt(reportLine.split(" +"));

            if (isReportSafe(data, tolerate)) {
                safeCount++;
            }
        }
        return safeCount;
    }

    private static boolean isReportSafe(int[] data, boolean tolerate) {
        int n = data.length - 1;

        if (data[0] > data[n / 2] && data[n / 2 - 1] > data[n]) {
            ArrayUtils.reverse(data);
        }

        for (int i = 1; i < data.length; i++) {
            int d1 = data[i - 1];
            int d2 = data[i];
            int diff = d2 - d1;

            if (isDiffSafe(diff)) {
                continue;
            }

            if (!tolerate) {
                return false;
            }

            if (i > 1) {
                // Trying to exclude [i-1]
                int d0 = data[i - 2];
                diff = d2 - d0;

                if (isDiffSafe(diff)) {
                    tolerate = false;
                    continue;
                }
            } else {
                // first value check
                int d3 = data[i + 1];
                diff = d3 - d2;
                if (isDiffSafe(diff)) {
                    tolerate = false;
                    continue;
                }
            }

            if (i < n) {
                // Trying to exclude [i]
                int d3 = data[i + 1];
                diff = d3 - d1;
                if (isDiffSafe(diff)) {
                    tolerate = false;
                    i++;
                    continue;
                }
            } else {
                // last value check
                tolerate = false;
                continue;
            }
            return false;
        }

        return true;
    }

    private static boolean isDiffSafe(int diff) {
        return diff >= 1 && diff <= 3;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(safeReportCount("day02/test.txt", false));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(safeReportCount("day02/input.txt", false));
        System.out.println("== TEST 2 ==");
        System.out.println(safeReportCount("day02/test.txt", true));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(safeReportCount("day02/input.txt", true));
    }
}
