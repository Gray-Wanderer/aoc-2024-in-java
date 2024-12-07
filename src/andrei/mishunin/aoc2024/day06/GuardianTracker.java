package andrei.mishunin.aoc2024.day06;

import andrei.mishunin.aoc2024.tools.InputReader;
import andrei.mishunin.aoc2024.tools.IntPair;
import andrei.mishunin.aoc2024.tools.MatrixUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GuardianTracker {
    private static final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
    private static final char[] DIRECTIONS_CHAR = {'^', '>', 'v', '<'};
    private static final char[] DIRECTIONS_MASK = {0B0001, 0B0010, 0B0100, 0B1000};

    public static long countVisitedCells(String file) {
        List<String> input = InputReader.readAllLines(file);
        char[][] map = input.stream().map(String::toCharArray).toArray(char[][]::new);

        IntPair start = getStartPoint(map);

        int visited = 1;
        int direction = 0;
        int i0 = start.i();
        int j0 = start.j();
        while (true) {
            int i1 = i0 + DIRECTIONS[direction][0];
            int j1 = j0 + DIRECTIONS[direction][1];

            if (!MatrixUtils.isIndexInMatrix(map, i1, j1)) {
                break;
            }

            if (map[i1][j1] == '#') {
                direction = (direction + 1) % 4;
            } else {
                i0 = i1;
                j0 = j1;
                if (map[i1][j1] == '.') {
                    visited++;
                }
                map[i1][j1] = DIRECTIONS_CHAR[direction];
            }
        }

        return visited;
    }

    public static long countLoopObstaclePositions(String file) {
        List<String> input = InputReader.readAllLines(file);
        char[][] map = input.stream().map(String::toCharArray).toArray(char[][]::new);

        IntPair start = getStartPoint(map);

        int obstaclesPositions = 0;
        int direction = 0;
        Set<IntPair> usedPoints = new HashSet<>();
        int i0 = start.i();
        int j0 = start.j();

        while (true) {
            int i1 = i0 + DIRECTIONS[direction][0];
            int j1 = j0 + DIRECTIONS[direction][1];

            if (!MatrixUtils.isIndexInMatrix(map, i1, j1)) {
                break;
            }

            if (map[i1][j1] == '#') {
                direction = (direction + 1) % 4;
            } else {
                IntPair point = new IntPair(i1, j1);

                if (!usedPoints.contains(point) && map[i1][j1] == '.') {
                    usedPoints.add(point);
                    map[i1][j1] = '#';

                    if (hasLoop(map, start.i(), start.j())) {
                        obstaclesPositions++;
                    }
                    map[i1][j1] = '.';
                }
                i0 = i1;
                j0 = j1;
                map[i1][j1] = DIRECTIONS_CHAR[direction];
            }
        }

        return obstaclesPositions;
    }

    private static IntPair getStartPoint(char[][] map) {
        int n = map.length;
        int m = map[0].length;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (map[i][j] == '^') {
                    return new IntPair(i, j);
                }
            }
        }
        return null;
    }

    private static boolean hasLoop(char[][] map, int i0, int j0) {
        int n = map.length;
        int m = map[0].length;
        int[][] visited = new int[n][m];
        int direction = 0;
        visited[i0][j0] |= DIRECTIONS_MASK[direction];

        while (true) {
            int i1 = i0 + DIRECTIONS[direction][0];
            int j1 = j0 + DIRECTIONS[direction][1];

            if (!MatrixUtils.isIndexInMatrix(map, i1, j1)) {
                return false;
            }

            if ((visited[i1][j1] & DIRECTIONS_MASK[direction]) != 0) {
                return true;
            }

            visited[i0][j0] |= DIRECTIONS_MASK[direction];

            if (map[i1][j1] == '#') {
                direction = (direction + 1) % 4;

                if ((visited[i0][j0] & DIRECTIONS_MASK[direction]) != 0) {
                    return true;
                }

                visited[i0][j0] |= DIRECTIONS_MASK[direction];
            } else {
                i0 = i1;
                j0 = j1;
                visited[i1][j1] |= DIRECTIONS_MASK[direction];
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(countVisitedCells("day06/test.txt"));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(countVisitedCells("day06/input.txt"));
        System.out.println("== TEST 2 ==");
        System.out.println(countLoopObstaclePositions("day06/test.txt"));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(countLoopObstaclePositions("day06/input.txt"));
    }
}
