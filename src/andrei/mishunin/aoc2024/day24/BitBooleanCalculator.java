package andrei.mishunin.aoc2024.day24;

import andrei.mishunin.aoc2024.tools.InputReader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BitBooleanCalculator {
    public static long calc(String file) {
        List<String> input = InputReader.readAllLines(file);
        Map<String, Integer> gateValues = new HashMap<>();

        var iterator = input.iterator();
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.isEmpty()) {
                break;
            }

            String[] inputGate = line.split(": ");
            gateValues.put(inputGate[0], Integer.parseInt(inputGate[1]));
        }

        Map<String, Gate> gates = new HashMap<>();
        List<String> outputGates = new ArrayList<>();
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.isEmpty()) {
                break;
            }

            // 0   1   2  3   4
            //x21 XOR y21 -> qcm
            String[] gate = line.split(" ");
            String gateName = gate[4];
            gates.put(gateName, new Gate(gate[0], gate[2], gate[1]));
            if (gateName.charAt(0) == 'z') {
                outputGates.add(gateName);
            }
        }

        for (String outputGate : outputGates) {
            calculateGate(gateValues, gates, outputGate);
        }

        outputGates.sort(Comparator.reverseOrder());
        long result = 0;
        for (String outputGate : outputGates) {
            result <<= 1;
            result |= gateValues.get(outputGate);
        }

        return result;
    }

    static int calculateGate(Map<String, Integer> gateValues, Map<String, Gate> gates, String gateName) {
        if (gateValues.containsKey(gateName)) {
            return gateValues.get(gateName);
        }

        Gate gate = gates.get(gateName);

        int g1 = calculateGate(gateValues, gates, gate.g1);
        int g2 = calculateGate(gateValues, gates, gate.g2);
        int result = gate.gateFunction.apply(g1, g2);
        gateValues.put(gateName, result);
        return result;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(calc("day24/test.txt"));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(calc("day24/input.txt"));
    }

    static class Gate {
        String g1;
        String g2;
        GateFunction gateFunction;

        Gate(String g1, String g2, String gateType) {
            this.g1 = g1;
            this.g2 = g2;
            this.gateFunction = getGate(gateType);
        }
    }

    @FunctionalInterface
    interface GateFunction {
        int apply(int a, int b);
    }

    static GateFunction getGate(String gateType) {
        return switch (gateType) {
            case "AND" -> BitBooleanCalculator::andGate;
            case "OR" -> BitBooleanCalculator::orGate;
            case "XOR" -> BitBooleanCalculator::xorGate;
            default -> throw new IllegalArgumentException();
        };
    }

    static int andGate(int a, int b) {
        return a & b;
    }

    static int orGate(int a, int b) {
        return a | b;
    }

    static int xorGate(int a, int b) {
        return a ^ b;
    }
}
