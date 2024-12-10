package andrei.mishunin.aoc2024.day10;

import andrei.mishunin.aoc2024.tools.InputReader;
import andrei.mishunin.aoc2024.tools.IntPair;
import andrei.mishunin.aoc2024.tools.MatrixUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TrailEvaluator {
    private static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

    public static TrailStats evaluate(String file) {
        List<String> input = InputReader.readAllLines(file);
        char[][] map = MatrixUtils.toMatrix(input);

        int n = map.length;
        int m = map[0].length;
        int sumScores = 0;
        int sumRates = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (map[i][j] == '0') {
                    TrailStats stats = getTrailRating(map, i, j);
                    sumScores += stats.score;
                    sumRates += stats.rate;
                }
            }
        }

        return new TrailStats(sumScores, sumRates);
    }

    private static TrailStats getTrailRating(char[][] map, int i0, int j0) {
        int[][] ratings = new int[map.length][map[0].length];
        ratings[i0][j0] = 1;
        char step = '1';

        Set<IntPair> points = new HashSet<>();
        points.add(new IntPair(i0, j0));

        while (step <= '9' && !points.isEmpty()) {
            Set<IntPair> pointsNext = new HashSet<>();

            for (IntPair point : points) {
                for (int[] direction : DIRECTIONS) {
                    int i = point.i() + direction[0];
                    int j = point.j() + direction[1];
                    if (MatrixUtils.isIndexInMatrix(map, i, j) && map[i][j] == step) {
                        pointsNext.add(new IntPair(i, j));
                        ratings[i][j] += ratings[point.i()][point.j()];
                    }
                }
            }
            points = pointsNext;
            step++;
        }

        int rating = 0;
        for (IntPair point : points) {
            rating += ratings[point.i()][point.j()];
        }

        return new TrailStats(points.size(), rating);
    }

    public static void main(String[] args) {
        TrailStats testTrailStats = evaluate("day10/test.txt");
        TrailStats inputTrailStats = evaluate("day10/input.txt");

        System.out.println("== TEST 1 ==");
        System.out.println(testTrailStats.score());
        System.out.println("== SOLUTION 1 ==");
        System.out.println(inputTrailStats.score());
        System.out.println("== TEST 2 ==");
        System.out.println(testTrailStats.rate());
        System.out.println("== SOLUTION 2 ==");
        System.out.println(inputTrailStats.rate());
    }

    public record TrailStats(int score, int rate) {
    }
}
