package andrei.mishunin.aoc2024.day18;

import andrei.mishunin.aoc2024.tools.ArrayUtils;
import andrei.mishunin.aoc2024.tools.InputReader;
import andrei.mishunin.aoc2024.tools.MatrixUtils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class RAMRun {
    private static final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    public static int findSafePath(String file, int n, int dropped) {
        List<String> input = InputReader.readAllLines(file);
        boolean[][] matrix = new boolean[n + 1][n + 1];
        for (int i = 0; i < dropped; i++) {
            int[] droppedByte = ArrayUtils.toInt(input.get(i).split(","));
            matrix[droppedByte[1]][droppedByte[0]] = true;
        }

        return findSafePath(matrix, n);
    }

    public static int findSafePath(boolean[][] matrix, int n) {
        Deque<int[]> pathQueue = new ArrayDeque<>();
        pathQueue.offer(new int[]{0, 0, 0});
        matrix[0][0] = true;

        while (!pathQueue.isEmpty()) {
            int[] cur = pathQueue.poll();
            int i0 = cur[0];
            int j0 = cur[1];

            if (cur[0] == n && cur[1] == n) {
                return cur[2];
            }

            int nextStep = cur[2] + 1;

            for (int[] direction : DIRECTIONS) {
                int i = i0 + direction[0];
                int j = j0 + direction[1];

                if (MatrixUtils.isIndexInMatrix(matrix, i, j) && !matrix[i][j]) {
                    pathQueue.add(new int[]{i, j, nextStep});
                    matrix[i][j] = true;
                }
            }
        }

        return -1;
    }

    public static String findFirstBlockedCell(String file, int n, int minDropped) {
        List<String> input = InputReader.readAllLines(file);

        int maxDropped = input.size();

        while (minDropped < maxDropped) {
            int dropped = (maxDropped + minDropped) / 2;

            boolean[][] matrix = new boolean[n + 1][n + 1];
            for (int i = 0; i < dropped; i++) {
                int[] droppedByte = ArrayUtils.toInt(input.get(i).split(","));
                matrix[droppedByte[1]][droppedByte[0]] = true;
            }

            int path = findSafePath(matrix, n);
            if (path != -1) {
                minDropped = dropped + 1;
            } else {
                maxDropped = dropped;
            }
        }


        return input.get(minDropped - 1);
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(findSafePath("day18/test.txt", 6, 12));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(findSafePath("day18/input.txt", 70, 1024));
        System.out.println("== TEST 2 ==");
        System.out.println(findFirstBlockedCell("day18/test.txt", 6, 12));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(findFirstBlockedCell("day18/input.txt", 70, 1024));
    }
}
