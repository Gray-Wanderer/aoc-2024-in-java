package andrei.mishunin.aoc2024.day12;

import andrei.mishunin.aoc2024.tools.InputReader;
import andrei.mishunin.aoc2024.tools.MatrixUtils;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class FencePriceCalculator {
    private static final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    public static int getPrice(String file, FenceCounter fenceCounter) {
        List<String> input = InputReader.readAllLines(file);
        char[][] map = MatrixUtils.toMatrix(input);
        int n = map.length;
        int m = map[0].length;
        int[][] markedAreas = new int[n][m];

        int areaIndex = 0;
        List<Integer> squares = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (markedAreas[i][j] == 0) {
                    squares.add(findSquare(map, markedAreas, i, j, ++areaIndex));
                }
            }
        }

        int cost = 0;
        int[] perimeters = fenceCounter.getFencesCount(markedAreas, areaIndex);
        for (int i = 0; i < areaIndex; i++) {
            cost += perimeters[i] * squares.get(i);
        }


        return cost;
    }

    private static int findSquare(char[][] map, int[][] visited, int i0, int j0, int areaNumber) {
        Deque<int[]> stack = new LinkedList<>();
        stack.push(new int[]{i0, j0});
        visited[i0][j0] = areaNumber;
        char plant = map[i0][j0];

        int square = 1;
        while (!stack.isEmpty()) {
            int size = stack.size();
            for (int k = 0; k < size; k++) {
                int[] cur = stack.pop();
                for (int[] direction : DIRECTIONS) {
                    int i = cur[0] + direction[0];
                    int j = cur[1] + direction[1];

                    if (MatrixUtils.isIndexInMatrix(map, i, j) && visited[i][j] == 0 && map[i][j] == plant) {
                        visited[i][j] = areaNumber;
                        stack.push(new int[]{i, j});
                        square++;
                    }
                }
            }
        }

        return square;
    }

    private static int[] getPerimeters(int[][] markedAreas, int areaCount) {
        int n = markedAreas.length;
        int m = markedAreas[0].length;
        int[] perimeters = new int[areaCount];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int areaNumber = markedAreas[i][j];

                for (int[] direction : DIRECTIONS) {
                    int i1 = i + direction[0];
                    int j1 = j + direction[1];

                    if (!MatrixUtils.isIndexInMatrix(markedAreas, i1, j1) || markedAreas[i1][j1] != areaNumber) {
                        perimeters[areaNumber - 1]++;
                    }
                }
            }
        }
        return perimeters;
    }

    private static int[] getSideCounts(int[][] markedAreas, int areaCount) {
        int n = markedAreas.length;
        int m = markedAreas[0].length;

        int[] sides = new int[areaCount];

        for (int i = 0; i < n; i++) {
            int prevArea = 0;
            boolean downSide = false;
            boolean upSide = false;
            for (int j = 0; j < m; j++) {
                int currentArea = markedAreas[i][j];

                if (!MatrixUtils.isIndexInMatrix(markedAreas, i + 1, j) || markedAreas[i + 1][j] != currentArea) {
                    if (currentArea != prevArea || !downSide) {
                        sides[currentArea - 1]++;
                        downSide = true;
                    }
                } else {
                    downSide = false;
                }

                if (!MatrixUtils.isIndexInMatrix(markedAreas, i - 1, j) || markedAreas[i - 1][j] != currentArea) {
                    if (currentArea != prevArea || !upSide) {
                        sides[currentArea - 1]++;
                        upSide = true;
                    }
                } else {
                    upSide = false;
                }

                prevArea = currentArea;
            }
        }

        for (int j = 0; j < m; j++) {
            int prevArea = 0;
            boolean rightSide = false;
            boolean leftSide = false;
            for (int i = 0; i < n; i++) {
                int currentArea = markedAreas[i][j];

                if (!MatrixUtils.isIndexInMatrix(markedAreas, i, j + 1) || markedAreas[i][j + 1] != currentArea) {
                    if (currentArea != prevArea || !rightSide) {
                        sides[currentArea - 1]++;
                        rightSide = true;
                    }
                } else {
                    rightSide = false;
                }
                if (!MatrixUtils.isIndexInMatrix(markedAreas, i, j - 1) || markedAreas[i][j - 1] != currentArea) {
                    if (currentArea != prevArea || !leftSide) {
                        sides[currentArea - 1]++;
                        leftSide = true;
                    }
                } else {
                    leftSide = false;
                }
                prevArea = currentArea;
            }
        }

        return sides;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(getPrice("day12/test.txt", FencePriceCalculator::getPerimeters));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(getPrice("day12/input.txt", FencePriceCalculator::getPerimeters));
        System.out.println("== TEST 2 ==");
        System.out.println(getPrice("day12/test.txt", FencePriceCalculator::getSideCounts));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(getPrice("day12/input.txt", FencePriceCalculator::getSideCounts));
    }

    public interface FenceCounter {
        int[] getFencesCount(int[][] markedAreas, int areaCount);
    }
}
