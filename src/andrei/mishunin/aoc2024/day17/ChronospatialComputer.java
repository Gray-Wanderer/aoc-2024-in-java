package andrei.mishunin.aoc2024.day17;

import andrei.mishunin.aoc2024.tools.InputReader;

import java.util.List;

public class ChronospatialComputer {
    public static String executeProgram(String file) {
        List<String> input = InputReader.readAllLines(file);
        long registerA = Long.parseLong(input.get(0).substring(12));
        long registerB = Long.parseLong(input.get(1).substring(12));
        long registerC = Long.parseLong(input.get(2).substring(12));
        String[] program = input.get(4).substring(9).split(",");

        return executeProgram(registerA, registerB, registerC, program);
    }

    public static String executeProgram(long registerA, long registerB, long registerC, String[] program) {
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

    public static long fixRegisterA(String file) {
        List<String> input = InputReader.readAllLines(file);
        String program = input.get(4).substring(9);

        return fixRegisterA(program + ",", program.split(","), 0, program.length() - 1);
    }

    public static long fixRegisterA(String fullExpectedOutput, String[] subProgram, long registerA, int start) {
        if (start < 0) {
            return registerA;
        }

        registerA *= 8;
        String expectedOutput = fullExpectedOutput.substring(start);

        for (int i = 0; i < 8; i++) {
            String subResult = executeProgram(registerA, 0, 0, subProgram);

            if (subResult.equals(expectedOutput)) {
                long nextRegisterA = fixRegisterA(fullExpectedOutput, subProgram, registerA, start - 2);
                if (nextRegisterA != -1) {
                    return nextRegisterA;
                }
            }
            registerA++;
        }
        return -1;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(executeProgram("day17/test.txt"));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(executeProgram("day17/input.txt"));
        System.out.println("== TEST 2 ==");
        System.out.println(fixRegisterA("day17/test.txt"));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(fixRegisterA("day17/input.txt"));
    }
}
