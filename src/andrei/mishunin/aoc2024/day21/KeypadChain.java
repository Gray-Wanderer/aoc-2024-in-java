package andrei.mishunin.aoc2024.day21;

import andrei.mishunin.aoc2024.tools.InputReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeypadChain {

    static char[][] NUMERIC_KEYPAD = new char[][]{
            {'7', '8', '9'},
            {'4', '5', '6'},
            {'1', '2', '3'},
            {'#', '0', 'A'}
    };

    static char[][] DIRECTIONAL_KEYPAD = new char[][]{
            {'#', '^', 'A'},
            {'<', 'v', '>'},
    };
    static Map<Character, int[]> NUMERIC_COORDINATES = new HashMap<>();
    static Map<Character, int[]> DIRECTIONAL_COORDINATES = new HashMap<>();

    static RobotState NUMERIC_ROBOT;
    static RobotState DIRECTIONAL_ROBOT;

    static Map<Integer, Map<FromTo, Long>> DIRECTIONAL_PRESS_COUNTS_FOR_KEYPADS = new HashMap<>();

    static {
        for (int i = 0; i < NUMERIC_KEYPAD.length; i++) {
            for (int j = 0; j < NUMERIC_KEYPAD[i].length; j++) {
                if (NUMERIC_KEYPAD[i][j] != '#') {
                    NUMERIC_COORDINATES.put(NUMERIC_KEYPAD[i][j], new int[]{i, j});
                }
            }
        }
        for (int i = 0; i < DIRECTIONAL_KEYPAD.length; i++) {
            for (int j = 0; j < DIRECTIONAL_KEYPAD[i].length; j++) {
                if (DIRECTIONAL_KEYPAD[i][j] != '#') {
                    DIRECTIONAL_COORDINATES.put(DIRECTIONAL_KEYPAD[i][j], new int[]{i, j});
                }
            }
        }

        NUMERIC_ROBOT = new RobotState(NUMERIC_COORDINATES.get('A'));
        DIRECTIONAL_ROBOT = new RobotState(DIRECTIONAL_COORDINATES.get('A'));

        Map<FromTo, Long> firstRobotKeypad = new HashMap<>();
        for (int i0 = 0; i0 < DIRECTIONAL_KEYPAD.length; i0++) {
            for (int j0 = 0; j0 < DIRECTIONAL_KEYPAD[i0].length; j0++) {
                if (DIRECTIONAL_KEYPAD[i0][j0] == '#') {
                    continue;
                }
                for (char[] keysRow : DIRECTIONAL_KEYPAD) {
                    for (char targetKey : keysRow) {
                        if (targetKey != '#') {
                            FromTo fromTo = new FromTo(new RobotState(i0, j0), DIRECTIONAL_ROBOT, targetKey);
                            firstRobotKeypad.put(fromTo, moveForFirstRobotKeypad(new RobotState(i0, j0), targetKey));
                        }
                    }
                }
            }
        }
        DIRECTIONAL_PRESS_COUNTS_FOR_KEYPADS.put(1, firstRobotKeypad);
    }

    public static long getKeyPressingComplexity(String file, int directionalKeypadsCount) {
        List<String> input = InputReader.readAllLines(file);

        long result = 0;
        for (String s : input) {
            long pressedButtons = 0L;
            RobotState numState = NUMERIC_ROBOT;
            for (char nextButton : s.toCharArray()) {
                pressedButtons += 1 + moveRobotForNumpadKeypad(
                        directionalKeypadsCount,
                        numState,
                        DIRECTIONAL_ROBOT,
                        nextButton
                );
                numState = new RobotState(NUMERIC_COORDINATES.get(nextButton));
            }
            int num = Integer.parseInt(s.substring(0, s.length() - 1));
            result += (long) num * pressedButtons;
        }

        return result;
    }

    /**
     * .+---+---+---+
     * .| 7 | 8 | 9 |
     * .+---+---+---+
     * .| 4 | 5 | 6 |
     * .+---+---+---+
     * .| 1 | 2 | 3 |
     * .+---+---+---+
     * .    | 0 | A |
     * .    +---+---+
     */
    private static long moveRobotForNumpadKeypad(
            int directionalKeypadsCount,
            RobotState robotNumpadState,
            RobotState robotKeypadState1,
            char target) {
        int i0 = robotNumpadState.i;
        int j0 = robotNumpadState.j;

        int[] targetCoordinates = NUMERIC_COORDINATES.get(target);
        int i1 = targetCoordinates[0];
        int j1 = targetCoordinates[1];

        if (i0 == i1 && j0 == j1) {
            return moveRobotForDirectionalKeypad(directionalKeypadsCount, robotKeypadState1, DIRECTIONAL_ROBOT, 'A');
        }

        int dy = i1 - i0;
        long pressedFirstY = Long.MAX_VALUE;
        if (dy > 0) {
            if (NUMERIC_KEYPAD[i1][j0] != '#') {
                pressedFirstY = moveRobotForDirectionalKeypad(directionalKeypadsCount, robotKeypadState1, DIRECTIONAL_ROBOT, 'v');
                pressedFirstY += dy;
                pressedFirstY += moveRobotForNumpadKeypad(directionalKeypadsCount, new RobotState(i1, j0), new RobotState(DIRECTIONAL_COORDINATES.get('v')), target);
            }
        } else if (dy < 0) {
            pressedFirstY = moveRobotForDirectionalKeypad(directionalKeypadsCount, robotKeypadState1, DIRECTIONAL_ROBOT, '^');
            pressedFirstY -= dy;
            pressedFirstY += moveRobotForNumpadKeypad(directionalKeypadsCount, new RobotState(i1, j0), new RobotState(DIRECTIONAL_COORDINATES.get('^')), target);
        }

        long pressedFirstX = Long.MAX_VALUE;
        int dx = j1 - j0;

        if (dx > 0) {
            pressedFirstX = moveRobotForDirectionalKeypad(directionalKeypadsCount, robotKeypadState1, DIRECTIONAL_ROBOT, '>');
            pressedFirstX += dx;
            pressedFirstX += moveRobotForNumpadKeypad(directionalKeypadsCount, new RobotState(i0, j1), new RobotState(DIRECTIONAL_COORDINATES.get('>')), target);
        } else if (dx < 0) {
            if (NUMERIC_KEYPAD[i0][j1] != '#') {
                pressedFirstX = moveRobotForDirectionalKeypad(directionalKeypadsCount, robotKeypadState1, DIRECTIONAL_ROBOT, '<');
                pressedFirstX -= dx;
                pressedFirstX += moveRobotForNumpadKeypad(directionalKeypadsCount, new RobotState(i0, j1), new RobotState(DIRECTIONAL_COORDINATES.get('<')), target);
            }
        }

        return Math.min(pressedFirstX, pressedFirstY);
    }

    /**
     * .    +---+---+
     * .    | ^ | A |
     * .+---+---+---+
     * .| < | v | > |
     * .+---+---+---+
     */
    private static long moveRobotForDirectionalKeypad(
            int keypadIndex,
            RobotState robotKeypadState1,
            RobotState robotKeypadState2,
            char target) {

        FromTo fromTo = new FromTo(robotKeypadState1, robotKeypadState2, target);
        if (DIRECTIONAL_PRESS_COUNTS_FOR_KEYPADS.computeIfAbsent(keypadIndex, k -> new HashMap<>()).containsKey(fromTo)) {
            return DIRECTIONAL_PRESS_COUNTS_FOR_KEYPADS.get(keypadIndex).get(fromTo);
        }

        int i0 = robotKeypadState1.i;
        int j0 = robotKeypadState1.j;

        int[] targetCoordinates = DIRECTIONAL_COORDINATES.get(target);
        int i1 = targetCoordinates[0];
        int j1 = targetCoordinates[1];


        if (i0 == i1 && j0 == j1) {
            long finalPressed = moveRobotForDirectionalKeypad(keypadIndex - 1, robotKeypadState2, DIRECTIONAL_ROBOT, 'A');
            DIRECTIONAL_PRESS_COUNTS_FOR_KEYPADS.get(keypadIndex).put(fromTo, finalPressed);
            return finalPressed;
        }

        int dy = i1 - i0;
        long pressedFirstY = Long.MAX_VALUE;
        if (dy > 0) {
            pressedFirstY = moveRobotForDirectionalKeypad(keypadIndex - 1, robotKeypadState2, DIRECTIONAL_ROBOT, 'v');
            pressedFirstY += dy;
            pressedFirstY += moveRobotForDirectionalKeypad(keypadIndex, new RobotState(i1, j0), new RobotState(DIRECTIONAL_COORDINATES.get('v')), target);
        } else if (dy < 0) {
            if (DIRECTIONAL_KEYPAD[i1][j0] != '#') {
                pressedFirstY = moveRobotForDirectionalKeypad(keypadIndex - 1, robotKeypadState2, DIRECTIONAL_ROBOT, '^');
                pressedFirstY -= dy;
                pressedFirstY += moveRobotForDirectionalKeypad(keypadIndex, new RobotState(i1, j0), new RobotState(DIRECTIONAL_COORDINATES.get('^')), target);
            }
        }

        long pressedFirstX = Long.MAX_VALUE;
        int dx = j1 - j0;

        if (dx > 0) {
            pressedFirstX = moveRobotForDirectionalKeypad(keypadIndex - 1, robotKeypadState2, DIRECTIONAL_ROBOT, '>');
            pressedFirstX += dx;
            pressedFirstX += moveRobotForDirectionalKeypad(keypadIndex, new RobotState(i0, j1), new RobotState(DIRECTIONAL_COORDINATES.get('>')), target);
        } else if (dx < 0) {
            if (DIRECTIONAL_KEYPAD[i0][j1] != '#') {
                pressedFirstX = moveRobotForDirectionalKeypad(keypadIndex - 1, robotKeypadState2, DIRECTIONAL_ROBOT, '<');
                pressedFirstX -= dx;
                pressedFirstX += moveRobotForDirectionalKeypad(keypadIndex, new RobotState(i0, j1), new RobotState(DIRECTIONAL_COORDINATES.get('<')), target);
            }
        }

        long minPressed = Math.min(pressedFirstX, pressedFirstY);
        DIRECTIONAL_PRESS_COUNTS_FOR_KEYPADS.get(keypadIndex).put(fromTo, minPressed);
        return minPressed;
    }

    private static long moveForFirstRobotKeypad(RobotState robotKeypadState2, char target) {
        int i0 = robotKeypadState2.i;
        int j0 = robotKeypadState2.j;

        int[] targetCoordinates = DIRECTIONAL_COORDINATES.get(target);
        int i1 = targetCoordinates[0];
        int j1 = targetCoordinates[1];

        if (i0 == i1 && j0 == j1) {
            return 0L;
        }


        int dy = i1 - i0;
        long pressedFirstY = Long.MAX_VALUE;
        if (dy > 0) {
            pressedFirstY = dy + moveForFirstRobotKeypad(new RobotState(i1, j0), target);
        } else if (dy < 0) {
            if (DIRECTIONAL_KEYPAD[i1][j0] != '#') {
                pressedFirstY = -dy + moveForFirstRobotKeypad(new RobotState(i1, j0), target);
            }
        }

        long pressedFirstX = Long.MAX_VALUE;
        int dx = j1 - j0;
        if (dx > 0) {
            pressedFirstX = dx + moveForFirstRobotKeypad(new RobotState(i0, j1), target);
        } else if (dx < 0) {
            if (DIRECTIONAL_KEYPAD[i0][j1] != '#') {
                pressedFirstX = -dx + moveForFirstRobotKeypad(new RobotState(i0, j1), target);
            }
        }

        return Math.min(pressedFirstX, pressedFirstY);
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(getKeyPressingComplexity("day21/test.txt", 2));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(getKeyPressingComplexity("day21/input.txt", 2));
        System.out.println("== TEST 2 ==");
        System.out.println(getKeyPressingComplexity("day21/test.txt", 25));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(getKeyPressingComplexity("day21/input.txt", 25));
    }


    record RobotState(int i, int j) {
        public RobotState(int[] ij) {
            this(ij[0], ij[1]);
        }
    }

    record FromTo(RobotState r1, RobotState r2, char target) {
    }
}
