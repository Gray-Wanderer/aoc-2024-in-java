package andrei.mishunin.aoc2024.day20;

import andrei.mishunin.aoc2024.tools.InputReader;
import andrei.mishunin.aoc2024.tools.MatrixUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RaceCondition {
    private static final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    public static long topRoutesWithCheating(String file, int cheatTime, int threshold, boolean debug) {
        List<String> input = InputReader.readAllLines(file);
        char[][] map = MatrixUtils.toMatrix(input);
        int n = map.length;
        int m = map[0].length;
        int[][] route = new int[n][m];
        for (int[] row : route) {
            Arrays.fill(row, -1);
        }

        int[] current = findStart(map);
        route[current[0]][current[1]] = 0;
        int step = 1;

        List<int[]> path = new ArrayList<>();
        path.add(current);
        while (map[current[0]][current[1]] != 'E') {
            for (int[] direction : DIRECTIONS) {
                int i = current[0] + direction[0];
                int j = current[1] + direction[1];
                if (map[i][j] != '#' && route[i][j] == -1) {
                    route[i][j] = step++;
                    current = new int[]{i, j};
                    path.add(current);
                    break;
                }
            }
        }

        Map<Integer, Integer> cheats = new TreeMap<>();
        for (int[] point : path) {
            int i0 = point[0];
            int j0 = point[1];
            for (int iCheat = -cheatTime; iCheat <= cheatTime; iCheat++) {
                int iCheatAbs = Math.abs(iCheat);
                for (int jCheat = iCheatAbs - cheatTime; jCheat <= cheatTime - iCheatAbs; jCheat++) {
                    int cheatSteps = iCheatAbs + Math.abs(jCheat);
                    int i = i0 + iCheat;
                    int j = j0 + jCheat;
                    if (MatrixUtils.isIndexInMatrix(route, i, j)) {
                        int cheat = route[i][j] - route[i0][j0] - cheatSteps;
                        if (cheat >= threshold) {
                            cheats.compute(cheat, (k, v) -> v == null ? 1 : v + 1);
                        }
                    }
                }
            }
        }

        if (debug) {
            cheats.forEach((k, v) -> System.out.println(v + " save " + k + " pico"));
        }


        return cheats.values().stream().mapToInt(v -> v).sum();
    }

    private static int[] findStart(char[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 'S') {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(topRoutesWithCheating("day20/test.txt", 2, 1, true));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(topRoutesWithCheating("day20/input.txt", 2, 100, false));
        System.out.println("== TEST 2 ==");
        System.out.println(topRoutesWithCheating("day20/test.txt", 20, 32, true));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(topRoutesWithCheating("day20/input.txt", 20, 100, false));
    }
}
