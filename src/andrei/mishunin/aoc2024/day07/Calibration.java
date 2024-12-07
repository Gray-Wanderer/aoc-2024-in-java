package andrei.mishunin.aoc2024.day07;

import andrei.mishunin.aoc2024.tools.ArrayUtils;
import andrei.mishunin.aoc2024.tools.InputReader;

import java.util.List;

public class Calibration {
    public static long calibrate(String file, EquationsSolver equationsSolver) {
        List<String> input = InputReader.readAllLines(file);

        long sum = 0;
        for (String line : input) {
            String[] split = line.split(": ");
            long target = Long.parseLong(split[0]);
            long[] values = ArrayUtils.toLong(split[1].split(" "));

            if (equationsSolver.canSolve(target, values, values.length - 1)) {
                sum += target;
            }
        }

        return sum;
    }

    private static boolean canSolve(long target, long[] values, int i) {
        if (i == 0) {
            return target == values[0];
        }

        if (target % values[i] == 0) {
            if (canSolve(target / values[i], values, i - 1)) {
                return true;
            }
        }

        if (target > values[i]) {
            return canSolve(target - values[i], values, i - 1);
        }

        return false;
    }

    private static boolean canSolveWithConcatenation(long target, long[] values, int i) {
        if (i == 0) {
            return target == values[0];
        }

        if (target % values[i] == 0) {
            if (canSolveWithConcatenation(target / values[i], values, i - 1)) {
                return true;
            }
        }

        if (target > values[i]) {
            if (canSolveWithConcatenation(target - values[i], values, i - 1)) {
                return true;
            }
        }

        String targetStr = String.valueOf(target);
        String valueStr = Long.toString(values[i]);
        if (targetStr.length() != valueStr.length() && targetStr.endsWith(valueStr)) {
            return canSolveWithConcatenation(
                    Long.parseLong(targetStr.substring(0, targetStr.length() - valueStr.length())),
                    values, i - 1
            );
        }

        return false;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(calibrate("day07/test.txt", Calibration::canSolve));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(calibrate("day07/input.txt", Calibration::canSolve));
        System.out.println("== TEST 2 ==");
        System.out.println(calibrate("day07/test.txt", Calibration::canSolveWithConcatenation));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(calibrate("day07/input.txt", Calibration::canSolveWithConcatenation));
    }

    @FunctionalInterface
    public interface EquationsSolver {
        boolean canSolve(long target, long[] values, int i);
    }
}
