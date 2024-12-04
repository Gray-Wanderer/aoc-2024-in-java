package andrei.mishunin.aoc2024.day04;

import andrei.mishunin.aoc2024.tools.InputReader;
import andrei.mishunin.aoc2024.tools.MatrixUtils;

import java.util.List;

public class CounterXMAS {
    private static final char[] XMAS = new char[]{'X', 'M', 'A', 'S'};
    private static final int[][] DIRECTIONS = {
            {0, 1}, {1, 0}, {0, -1}, {-1, 0},
            {1, 1}, {-1, -1}, {-1, 1}, {1, -1}
    };

    public static long countXMAS(String file) {
        List<String> input = InputReader.readAllLines(file);
        char[][] matrix = MatrixUtils.toMatrix(input);

        int n = matrix.length;
        int m = matrix[0].length;
        long countXMAS = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                countXMAS += countXMAS(matrix, i, j);
            }
        }

        return countXMAS;
    }

    private static int countXMAS(char[][] m, int i, int j) {
        int count = 0;
        for (int[] direction : DIRECTIONS) {
            boolean xmas = true;
            int i0 = i;
            int j0 = j;

            for (int k = 0; k < 4; k++) {
                if (!MatrixUtils.isIndexInMatrix(m, i0, j0) || m[i0][j0] != XMAS[k]) {
                    xmas = false;
                    break;
                }
                i0 += direction[0];
                j0 += direction[1];
            }

            if (xmas) {
                count++;
            }
        }
        return count;
    }

    public static long countCrossMAS(String file) {
        List<String> input = InputReader.readAllLines(file);
        char[][] matrix = MatrixUtils.toMatrix(input);

        int n = matrix.length;
        int m = matrix[0].length;
        long countCrossMAS = 0;
        for (int i = 1; i < n - 1; i++) {
            for (int j = 1; j < m - 1; j++) {
                if (isCrossMAS(matrix, i, j)) {
                    countCrossMAS++;
                }
            }
        }

        return countCrossMAS;
    }

    private static boolean isCrossMAS(char[][] m, int i, int j) {
        return m[i][j] == 'A' &&
                ((m[i - 1][j - 1] == 'M' && m[i + 1][j + 1] == 'S') || (m[i - 1][j - 1] == 'S' && m[i + 1][j + 1] == 'M')) &&
                ((m[i - 1][j + 1] == 'M' && m[i + 1][j - 1] == 'S') || (m[i - 1][j + 1] == 'S' && m[i + 1][j - 1] == 'M'));
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(countXMAS("day04/test.txt"));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(countXMAS("day04/input.txt"));
        System.out.println("== TEST 2 ==");
        System.out.println(countCrossMAS("day04/test.txt"));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(countCrossMAS("day04/input.txt"));
    }
}
