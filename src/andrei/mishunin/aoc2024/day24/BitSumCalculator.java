package andrei.mishunin.aoc2024.day24;

import andrei.mishunin.aoc2024.tools.InputReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BitSumCalculator {
    public static String findWrongConnections(String file) {
        List<String> input = InputReader.readAllLines(file);
        Map<String, Gate> gates = new HashMap<>();

        var iterator = input.iterator();
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.isEmpty()) {
                break;
            }

            String[] inputGate = line.split(": ");
            String gateName = inputGate[0];
            gates.put(gateName, new Gate(gateName, "VALUE"));
        }

        Map<String, StringPair> gateConnections = new HashMap<>();
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.isEmpty()) {
                break;
            }

            // 0   1   2  3   4
            //x21 XOR y21 -> qcm
            String[] gate = line.split(" ");
            String inGateName1 = gate[0];
            String gateType = gate[1];
            String inGateName2 = gate[2];
            String outGateName = gate[4];

            gateConnections.put(outGateName, new StringPair(inGateName1, inGateName2));
            gates.put(outGateName, new Gate(outGateName, gateType));
        }

        gateConnections.forEach((gateName, pair) -> {
            Gate gate = gates.get(gateName);
            gate.g1 = gates.get(pair.g1);
            gate.g2 = gates.get(pair.g2);
        });

        Gate x0 = gates.get("x00");
        Gate y0 = gates.get("y00");
        Gate cOut = null;
        for (Gate gate : gates.values()) {
            if (gate.isTheSame(x0, y0, "AND")) {
                cOut = gate;
                break;
            }
        }
        if (cOut == null) {
            throw new RuntimeException("Base combination of cout on level 0 doesn't exist");
        }

        int i = 1;
        List<String> swapNames = new ArrayList<>();
        while (swapNames.size() != 8) {
            String xGateName = (i < 10 ? "x0" : "x") + i;
            String yGateName = (i < 10 ? "y0" : "y") + i;
            String zNodeName = (i < 10 ? "z0" : "z") + i;
            Gate xGate = gates.get(xGateName);
            Gate yGate = gates.get(yGateName);
            Gate zGate = gates.get(zNodeName);

            cOut = fixSumAndGetCout(gates.values(), swapNames, xGate, yGate, cOut, zGate);

            i++;
        }

        return swapNames.stream().sorted().collect(Collectors.joining(","));
    }

    /*
     * How to sum:
     *
     * cOut -----------------.
     *                        XOR (XxorYxorC) -> SUM (z..)
     * x(..) -.              /
     *         XOR (XxorY) -{
     * y(..) -'              \
     *                        AND (XxorYandC) - OR (XxorYandCorXandY) -> cOut
     *                       /                 /
     * cOut ----------------'                 /
     *                                       /
     * x(..) -.                             /
     *         AND (XandY) ----------------'
     * y(..) -'
     */
    private static Gate fixSumAndGetCout(Collection<Gate> gateList, List<String> swapNames, Gate xGate, Gate yGate, Gate cOut, Gate zGate) {
        String xGateName = xGate.name;
        String yGateName = yGate.name;
        Gate XxorY = null;
        Gate XandY = null;
        for (Gate gate : gateList) {
            if (gate.isTheSame(xGate, yGate, "XOR")) {
                XxorY = gate;
            } else if (gate.isTheSame(xGate, yGate, "AND")) {
                XandY = gate;
            }
        }
        if (XxorY == null) {
            throw new RuntimeException("Base combination '" + xGateName + " XOR " + yGateName + "' doesn't exist");
        }
        if (XandY == null) {
            throw new RuntimeException("Base combination '" + xGateName + " AND " + yGateName + "' doesn't exist");
        }

        Gate XxorYxorC = null;
        for (Gate gate : gateList) {
            if (gate.isTheSame(XxorY, cOut, "XOR")) {
                XxorYxorC = gate;
                break;
            }
        }

        if (XxorYxorC == null) {
            for (Gate gate : gateList) {
                if (gate.contains(XxorY, "XOR") || gate.contains(cOut, "XOR")) {
                    XxorYxorC = gate;
                    break;
                }
            }

            if (XxorYxorC == null) {
                throw new RuntimeException("Base combination '" + xGateName + " XOR " + yGateName + " XOR cout' doesn't exist");
            }

            GatePair pair = findSwapToFixWrongParent(XxorYxorC, cOut, XxorY);

            swapNames.add(pair.g1.name);
            swapNames.add(pair.g2.name);
            swapGates(gateList, pair.g1, pair.g2);
        }

        if (zGate != XxorYxorC) {
            swapNames.add(zGate.name);
            swapNames.add(XxorYxorC.name);
            swapGates(gateList, zGate, XxorYxorC);
        }

        Gate XxorYandC = null;
        for (Gate gate : gateList) {
            if (gate.isTheSame(XxorY, cOut, "AND")) {
                XxorYandC = gate;
                break;
            }
        }

        Gate XxorYandCorXandY = null;
        for (Gate gate : gateList) {
            if (gate.isTheSame(XxorYandC, XandY, "OR")) {
                XxorYandCorXandY = gate;
                break;
            }
        }

        if (XxorYandCorXandY == null) {
            for (Gate gate : gateList) {
                if (gate.contains(XxorYandC, "OR") || gate.contains(XandY, "OR")) {
                    XxorYandCorXandY = gate;
                    break;
                }
            }
            if (XxorYandCorXandY == null) {
                throw new RuntimeException("Base combination for cout doesn't exist");
            }

            GatePair gatesToSwap = findSwapToFixWrongParent(XxorYandCorXandY, XxorYandC, XandY);

            swapNames.add(gatesToSwap.g1.name);
            swapNames.add(gatesToSwap.g2.name);
            swapGates(gateList, gatesToSwap.g1, gatesToSwap.g2);
        }
        return XxorYandCorXandY;
    }

    private static GatePair findSwapToFixWrongParent(Gate gateWithWrongParent, Gate parent1, Gate parent2) {
        Gate parentNotIncluded;
        Gate parentIncluded;
        if (gateWithWrongParent.g1 == parent1 || gateWithWrongParent.g2 == parent1) {
            parentIncluded = parent1;
            parentNotIncluded = parent2;
        } else if (gateWithWrongParent.g1 == parent2 || gateWithWrongParent.g2 == parent2) {
            parentIncluded = parent2;
            parentNotIncluded = parent1;
        } else {
            throw new RuntimeException("Wrong input, gate doesn't contains any of two parents");
        }

        Gate wrongParent;
        if (gateWithWrongParent.g1 == parentIncluded) {
            wrongParent = gateWithWrongParent.g2;
        } else {
            wrongParent = gateWithWrongParent.g1;
        }

        return new GatePair(parentNotIncluded, wrongParent);
    }

    private static void swapGates(Collection<Gate> gateList, Gate firstGate, Gate secondGate) {
        String firstName = firstGate.name;
        String secondName = secondGate.name;

        for (Gate gate : gateList) {
            if (gate.g1 == firstGate) {
                gate.g1 = secondGate;
            } else if (gate.g1 == secondGate) {
                gate.g1 = firstGate;
            }
            if (gate.g2 == firstGate) {
                gate.g2 = secondGate;
            } else if (gate.g2 == secondGate) {
                gate.g2 = firstGate;
            }
        }

        firstGate.name = secondName;
        secondGate.name = firstName;
    }

    public static void main(String[] args) {
        System.out.println("== SOLUTION 2 ==");
        System.out.println(findWrongConnections("day24/input.txt"));
    }

    static class Gate {
        String name;
        Gate g1 = null;
        Gate g2 = null;
        String gateType;

        Gate(String name, String gateType) {
            this.name = name;
            this.gateType = gateType;
        }

        boolean isTheSame(Gate g1, Gate g2, String gateType) {
            if (!this.gateType.equals(gateType)) {
                return false;
            }

            return (this.g1 == g1 && this.g2 == g2) || (this.g1 == g2 && this.g2 == g1);
        }

        boolean contains(Gate g1, String gateType) {
            return this.gateType.equals(gateType) && (this.g1 == g1 || this.g2 == g1);
        }
    }

    record StringPair(String g1, String g2) {
    }

    record GatePair(Gate g1, Gate g2) {
    }
}
