package andrei.mishunin.aoc2024.day08;

import andrei.mishunin.aoc2024.tools.InputReader;
import andrei.mishunin.aoc2024.tools.IntPair;
import andrei.mishunin.aoc2024.tools.MatrixUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResonantCollinearity {
    public static long countAntinodes(String file, AntinodeSearcher calculator) {
        List<String> input = InputReader.readAllLines(file);
        char[][] matrix = MatrixUtils.toMatrix(input);
        int n = matrix.length;
        int m = matrix[0].length;

        boolean[][] antinodes = new boolean[n][m];
        Map<Character, List<IntPair>> antennasPositions = new HashMap<>();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                char antenna = matrix[i][j];
                if (antenna == '.') {
                    continue;
                }

                IntPair node1 = new IntPair(i, j);
                List<IntPair> antennas = antennasPositions.computeIfAbsent(antenna, k -> new ArrayList<>());

                for (IntPair coordinate : antennas) {
                    calculator.search(node1, coordinate, antinodes);
                }

                antennas.add(node1);
            }
        }

        int antinodesCount = 0;
        for (boolean[] row : antinodes) {
            for (boolean b : row) {
                if (b) {
                    antinodesCount++;
                }
            }
        }

        return antinodesCount;
    }

    private static void searchAntinodes(IntPair antenna1, IntPair antenna2, boolean[][] antinodes) {
        int deltaI = antenna1.i() - antenna2.i();
        int deltaJ = antenna1.j() - antenna2.j();
        int i = antenna1.i() + deltaI;
        int j = antenna1.j() + deltaJ;

        if (MatrixUtils.isIndexInMatrix(antinodes, i, j)) {
            antinodes[i][j] = true;
        }

        i = antenna2.i() - deltaI;
        j = antenna2.j() - deltaJ;
        if (MatrixUtils.isIndexInMatrix(antinodes, i, j)) {
            antinodes[i][j] = true;
        }
    }

    private static void searchResonantHarmonicsAntinodes(IntPair antenna1, IntPair antenna2, boolean[][] antinodes) {
        int deltaI = antenna1.i() - antenna2.i();
        int deltaJ = antenna1.j() - antenna2.j();

        int i = antenna1.i();
        int j = antenna1.j();
        while (MatrixUtils.isIndexInMatrix(antinodes, i, j)) {
            antinodes[i][j] = true;
            i += deltaI;
            j += deltaJ;
        }

        i = antenna1.i();
        j = antenna1.j();
        while (MatrixUtils.isIndexInMatrix(antinodes, i, j)) {
            antinodes[i][j] = true;
            i -= deltaI;
            j -= deltaJ;
        }
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(countAntinodes("day08/test.txt", ResonantCollinearity::searchAntinodes));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(countAntinodes("day08/input.txt", ResonantCollinearity::searchAntinodes));
        System.out.println("== TEST 2 ==");
        System.out.println(countAntinodes("day08/test.txt", ResonantCollinearity::searchResonantHarmonicsAntinodes));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(countAntinodes("day08/input.txt", ResonantCollinearity::searchResonantHarmonicsAntinodes));
    }

    public interface AntinodeSearcher {
        void search(IntPair node1, IntPair node2, boolean[][] antinodes);
    }
}
