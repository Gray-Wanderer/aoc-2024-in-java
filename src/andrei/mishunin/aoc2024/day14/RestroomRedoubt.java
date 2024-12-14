package andrei.mishunin.aoc2024.day14;

import andrei.mishunin.aoc2024.tools.InputReader;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestroomRedoubt {
    private static final Pattern ROBOT_PATTERN = Pattern.compile("p=(\\d+),(\\d+) v=(-?\\d++),(-?\\d++)");

    public static int getSafetyFactor(String file, int n, int m, int time) {
        List<String> input = InputReader.readAllLines(file);
        int[] quadrants = new int[4];
        int n2 = n / 2;
        int m2 = m / 2;
        int[][][] quadrantsLimits = new int[][][]{
                {{0, 0}, {n2 - 1, m2 - 1}},
                {{n2 + 1, 0}, {n - 1, m2 - 1}},
                {{n2 + 1, m2 + 1}, {n - 1, m - 1}},
                {{0, m2 + 1}, {n2 - 1, m - 1}}
        };

        for (String s : input) {
            Robot robot = new Robot(s);
            robot.move(time, n, m);

            for (int i = 0; i < quadrantsLimits.length; i++) {
                int[][] quadrant = quadrantsLimits[i];

                if (robot.position[0] >= quadrant[0][0] && robot.position[1] >= quadrant[0][1]
                        && robot.position[0] <= quadrant[1][0] && robot.position[1] <= quadrant[1][1]) {
                    quadrants[i]++;
                }
            }
        }

        return quadrants[0] * quadrants[1] * quadrants[2] * quadrants[3];
    }

    public static long findChristmasTree(String file, int n, int m, boolean printChristmasTree) {
        List<String> input = InputReader.readAllLines(file);

        int robotCount = input.size();
        Robot[] robots = new Robot[robotCount];

        for (int i = 0; i < input.size(); i++) {
            robots[i] = new Robot(input.get(i));
        }

        int time = 0;
        while (true) {
            int[][] textMap = new int[n][m];
            for (Robot r : robots) {
                textMap[r.position[0]][r.position[1]]++;
            }

            StringBuilder christmasTree = new StringBuilder();
            for (int[] row : textMap) {
                for (int i : row) {
                    christmasTree.append(i > 0 ? '#' : ' ');
                }
                christmasTree.append('\n');
            }

            if (christmasTree.indexOf("##########") >= 0) {
                if (printChristmasTree) {
                    System.out.println(christmasTree);
                }
                break;
            }

            time++;
            for (Robot r : robots) {
                r.move(1, n, m);
            }
        }

        return time;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(getSafetyFactor("day14/test.txt", 7, 11, 100));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(getSafetyFactor("day14/input.txt", 103, 101, 100));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(findChristmasTree("day14/input.txt", 103, 101, false));
    }

    private static class Robot {
        final int[] position;
        final int[] velocity;

        Robot(String input) {
            Matcher robotM = ROBOT_PATTERN.matcher(input);
            if (!robotM.find()) {
                throw new RuntimeException("Invalid input: " + input);
            }

            this.position = new int[]{Integer.parseInt(robotM.group(2)), Integer.parseInt(robotM.group(1))};
            this.velocity = new int[]{Integer.parseInt(robotM.group(4)), Integer.parseInt(robotM.group(3))};
        }

        void move(int time, int n, int m) {
            position[0] = (int) ((position[0] + (long) velocity[0] * time) % n);
            position[1] = (int) ((position[1] + (long) velocity[1] * time) % m);
            if (position[0] < 0) {
                position[0] += n;
            }
            if (position[1] < 0) {
                position[1] += m;
            }
        }

        @Override
        public String toString() {
            return "Robot{" +
                    "position=" + Arrays.toString(position) +
                    ", velocity=" + Arrays.toString(velocity) +
                    '}';
        }
    }
}
