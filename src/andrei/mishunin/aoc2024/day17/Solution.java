package andrei.mishunin.aoc2024.day17;

import andrei.mishunin.aoc2024.tools.InputReader;

import java.util.List;

public class Solution {
    public static String solve(String file) {
        List<String> input = InputReader.readAllLines(file);
        long registerA = Long.parseLong(input.get(0).substring(12));
        long registerB = Long.parseLong(input.get(1).substring(12));
        long registerC = Long.parseLong(input.get(2).substring(12));
        String[] program = input.get(4).substring(9).split(",");

        int i = 0;
        StringBuilder output = new StringBuilder();
        while (i < program.length) {
            int instruction = Integer.parseInt(program[i]);
            int operandLiteral = Integer.parseInt(program[i + 1]);
            long operandCombo = switch (operandLiteral) {
                case 4 -> registerA;
                case 5 -> registerB;
                case 6 -> registerC;
                default -> operandLiteral;
            };

            switch (instruction) {
                case 0:
                    registerA = registerA / (1L << operandCombo);
                    break;
                case 1:
                    registerB ^= operandLiteral;
                    break;
                case 2:
                    registerB = operandCombo % 8;
                    break;
                case 3:
                    if (registerA != 0) {
                        i = operandLiteral;
                        continue;
                    }
                    break;
                case 4:
                    registerB ^= registerC;
                    break;
                case 5:
                    output.append(operandCombo % 8).append(',');
                    break;
                case 6:
                    registerB = registerA / (1L << operandCombo);
                    break;
                case 7:
                    registerC = registerA / (1L << operandCombo);
                    break;
            }
            i += 2;
        }

        return output.toString();
    }

    public static long solve2(String file, int[] expected) {
        long registerA = expected[0];
        for (int i = 1; i < expected.length; i++) {
            registerA += registerA * 8 + ((long) expected[i]);
        }

        long r = registerA;
        int i = 0;
        while (r != 0) {
            i++;
            r /= 8;
        }
        System.out.println(i);
        return registerA;
    }

    public static long solve3(String file, int[] expected) {
        List<String> input = InputReader.readAllLines(file);
        String[] program = input.get(4).substring(9).split(",");

        long min = 1;
        long max = 7;
        for (int i = 0; i < expected.length - 1; i++) {
            min *= 8;
            max *= 8;
        }

        brute:
        for (long bruteRegisterA = min; bruteRegisterA <= max; bruteRegisterA++) {
            long registerA = bruteRegisterA;
            long registerB = 0;
            long registerC = 0;

            int i = 0;
            int expectedI = 0;
            while (i < program.length) {
                int instruction = Integer.parseInt(program[i]);
                int operandLiteral = Integer.parseInt(program[i + 1]);
                long operandCombo = switch (operandLiteral) {
                    case 4 -> registerA;
                    case 5 -> registerB;
                    case 6 -> registerC;
                    default -> operandLiteral;
                };

                switch (instruction) {
                    case 0:
                        registerA = registerA / (1L << operandCombo);
                        break;
                    case 1:
                        registerB ^= operandLiteral;
                        break;
                    case 2:
                        registerB = operandCombo % 8;
                        break;
                    case 3:
                        if (registerA != 0) {
                            i = operandLiteral;
                            continue;
                        }
                        break;
                    case 4:
                        registerB ^= registerC;
                        break;
                    case 5:
                        if (expected[expectedI++] != operandCombo % 8) {
                            continue brute;
                        }
                        break;
                    case 6:
                        registerB = registerA / (1L << operandCombo);
                        break;
                    case 7:
                        registerC = registerA / (1L << operandCombo);
                        break;
                }
                i += 2;
            }

            return bruteRegisterA;
        }

        return -1;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(solve("day17/test.txt"));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(solve("day17/input.txt"));
        //1,7,6,5,1,0,5,0,7
//        System.out.println("== TEST 2 ==");
//        System.out.println(reverseSolve("day17/test.txt"));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(solve2("day17/input.txt", new int[]{2, 4, 1, 3, 7, 5, 4, 2, 0, 3, 1, 5, 5, 5, 3, 0}));
        //30115082 too low
    }
}
