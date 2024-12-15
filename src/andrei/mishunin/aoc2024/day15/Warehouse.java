package andrei.mishunin.aoc2024.day15;

import andrei.mishunin.aoc2024.tools.InputReader;
import andrei.mishunin.aoc2024.tools.MatrixUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Warehouse {
    private static final int[][] DIRECTIONS;

    static {
        DIRECTIONS = new int[256][];
        DIRECTIONS['^'] = new int[]{-1, 0};
        DIRECTIONS['v'] = new int[]{1, 0};
        DIRECTIONS['<'] = new int[]{0, -1};
        DIRECTIONS['>'] = new int[]{0, 1};
    }

    public static long getGPSHashAfterMoving(String file, boolean wideMap) {
        List<String> input = InputReader.readAllLines(file);
        Iterator<String> iterator = input.iterator();

        char[][] map = wideMap ? readWideMap(iterator) : readMap(iterator);

        List<String> moves = new ArrayList<>();
        while (iterator.hasNext()) {
            moves.add(iterator.next());
        }

        int[] robot = getRobotPosition(map);
        assert robot != null;

        for (String move : moves) {
            for (char directionC : move.toCharArray()) {
                if (wideMap) {
                    moveRobotOnWideMap(map, robot, directionC);
                } else {
                    moveRobot(map, robot, directionC);
                }
            }
        }

        long result = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == '[' || map[i][j] == 'O') {
                    result += 100L * i + j;
                }
            }
        }

        return result;
    }

    private static char[][] readMap(Iterator<String> iterator) {
        List<String> mapLines = new ArrayList<>();
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.isEmpty()) {
                break;
            }
            mapLines.add(line);
        }
        return MatrixUtils.toMatrix(mapLines);
    }

    private static char[][] readWideMap(Iterator<String> iterator) {
        List<String> mapLines = new ArrayList<>();
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.isEmpty()) {
                break;
            }
            StringBuilder builder = new StringBuilder(line.length() * 2);
            for (char c : line.toCharArray()) {
                if (c == '@') {
                    builder.append(c).append('.');
                } else if (c == 'O') {
                    builder.append('[').append(']');
                } else {
                    builder.append(c).append(c);
                }
            }
            mapLines.add(builder.toString());
        }
        return MatrixUtils.toMatrix(mapLines);
    }

    private static int[] getRobotPosition(char[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == '@') {
                    map[i][j] = '.';
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    private static void moveRobot(char[][] map, int[] robot, char directionC) {
        int[] direction = DIRECTIONS[directionC];
        int emptyI = robot[0] + direction[0];
        int emptyJ = robot[1] + direction[1];
        while (map[emptyI][emptyJ] != '.' && map[emptyI][emptyJ] != '#') {
            emptyI += direction[0];
            emptyJ += direction[1];
        }
        if (map[emptyI][emptyJ] == '#') {
            return;
        }

        int[] reverseDirections = new int[]{direction[0] * -1, direction[1] * -1};
        while (emptyI != robot[0] || emptyJ != robot[1]) {
            int nextI = emptyI + reverseDirections[0];
            int nextJ = emptyJ + reverseDirections[1];
            map[emptyI][emptyJ] = map[nextI][nextJ];
            emptyI = nextI;
            emptyJ = nextJ;
        }
        robot[0] += direction[0];
        robot[1] += direction[1];
    }

    private static void moveRobotOnWideMap(char[][] map, int[] robot, char directionC) {
        int[] direction = DIRECTIONS[directionC];
        int emptyI = robot[0] + direction[0];
        int emptyJ = robot[1] + direction[1];
        if (map[emptyI][emptyJ] == '.') {
            robot[0] += direction[0];
            robot[1] += direction[1];
            return;
        }
        if (map[emptyI][emptyJ] == '#') {
            return;
        }
        List<WideBox> boxes = new ArrayList<>();
        boxes.add(WideBox.createBox(map, emptyI, emptyJ));
        if (!getBoxesToMove(boxes, map, direction)) {
            return;
        }

        for (int i = boxes.size() - 1; i >= 0; i--) {
            boxes.get(i).moveBox(map, direction);
        }

        robot[0] += direction[0];
        robot[1] += direction[1];
    }

    private static boolean getBoxesToMove(List<WideBox> boxes, char[][] map, int[] direction) {
        Set<WideBox> visited = new HashSet<>(boxes);
        for (int b = 0; b < boxes.size(); b++) {
            WideBox box = boxes.get(b);

            for (int[] point : box.getPoints()) {
                int i = point[0] + direction[0];
                int j = point[1] + direction[1];

                if (map[i][j] == '#') {
                    return false;
                }
                if (map[i][j] == '.' || box.isPointInBox(i, j)) {
                    continue;
                }
                WideBox next = WideBox.createBox(map, i, j);
                if (!visited.contains(next)) {
                    visited.add(next);
                    boxes.add(next);
                }
            }
        }
        return true;
    }

    private record WideBox(int i0, int j0, int i1, int j1) {
        public static WideBox createBox(char[][] map, int i, int j) {
            if (map[i][j] == '[') {
                return new WideBox(i, j, i, j + 1);
            } else if (map[i][j] == ']') {
                return new WideBox(i, j - 1, i, j);
            } else {
                throw new IllegalArgumentException();
            }
        }

        public int[][] getPoints() {
            return new int[][]{{i0, j0}, {i1, j1}};
        }

        public boolean isPointInBox(int i, int j) {
            return (i == i0 && j == j0) || (i == i1 && j == j1);
        }

        public void moveBox(char[][] map, int[] direction) {
            if (direction[0] == 0) {
                int newI0 = i0 + direction[0];
                int newJ0 = j0 + direction[1];
                int newI1 = i1 + direction[0];
                int newJ1 = j1 + direction[1];

                if (direction[1] == 1) {
                    if (map[newI1][newJ1] != '.') {
                        throw new RuntimeException();
                    }
                    map[i0][j0] = '.';
                    map[newI0][newJ0] = '[';
                    map[newI1][newJ1] = ']';
                } else {
                    if (map[newI0][newJ0] != '.') {
                        throw new RuntimeException();
                    }
                    map[i1][j1] = '.';
                    map[newI0][newJ0] = '[';
                    map[newI1][newJ1] = ']';
                }
            } else {
                for (int[] point : getPoints()) {
                    int newI = point[0] + direction[0];
                    int newJ = point[1] + direction[1];
                    if (map[newI][newJ] != '.') {
                        throw new RuntimeException();
                    }
                    map[newI][newJ] = map[point[0]][point[1]];
                    map[point[0]][point[1]] = '.';
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(getGPSHashAfterMoving("day15/test.txt", false));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(getGPSHashAfterMoving("day15/input.txt", false));
        System.out.println("== TEST 2 ==");
        System.out.println(getGPSHashAfterMoving("day15/test.txt", true));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(getGPSHashAfterMoving("day15/input.txt", true));
    }
}
