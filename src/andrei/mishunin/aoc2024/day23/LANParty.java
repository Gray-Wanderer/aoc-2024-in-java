package andrei.mishunin.aoc2024.day23;

import andrei.mishunin.aoc2024.tools.InputReader;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LANParty {
    public static int getContOfGroup3T(String file) {
        List<String> input = InputReader.readAllLines(file);
        Map<String, Set<String>> edges = getEdges(input);

        Set<TripleConnection> connected = new HashSet<>();
        for (String computer : edges.keySet()) {
            Set<String> connections = edges.get(computer);
            for (String nextConnected : connections) {
                for (String nextNextConnected : edges.get(nextConnected)) {
                    if (!nextNextConnected.equals(computer) && connections.contains(nextNextConnected)) {
                        TripleConnection t = new TripleConnection(computer, nextConnected, nextNextConnected);
                        connected.add(t);
                    }
                }
            }
        }

        int threeInterConnectedWithT = 0;
        for (TripleConnection tripleConnection : connected) {
            if (tripleConnection.pc1.charAt(0) == 't'
                    || tripleConnection.pc2.charAt(0) == 't'
                    || tripleConnection.pc3.charAt(0) == 't') {
                threeInterConnectedWithT++;
            }
        }
        return threeInterConnectedWithT;
    }

    record TripleConnection(String pc1, String pc2, String pc3) {
        TripleConnection(String pc1, String pc2, String pc3) {
            String[] arr = new String[]{pc1, pc2, pc3};
            Arrays.sort(arr);
            this.pc1 = arr[0];
            this.pc2 = arr[1];
            this.pc3 = arr[2];
        }
    }

    public static String getLargestStrongConnectedGroup(String file) {
        List<String> input = InputReader.readAllLines(file);
        Map<String, Set<String>> edges = getEdges(input);

        Set<String> computers = edges.keySet();
        Set<String> largestGroup = Collections.emptySet();
        for (String mainComputer : computers) {
            Set<String> strongGroup = new HashSet<>();
            strongGroup.add(mainComputer);

            boolean hasChanges = true;
            while (hasChanges) {
                hasChanges = false;
                for (String computer : computers) {
                    if (!strongGroup.contains(computer) && edges.get(computer).containsAll(strongGroup)) {
                        strongGroup.add(computer);
                        hasChanges = true;
                    }
                }
            }

            if (largestGroup.size() < strongGroup.size()) {
                largestGroup = strongGroup;
            }
        }

        return largestGroup.stream().sorted().collect(Collectors.joining(","));
    }

    private static Map<String, Set<String>> getEdges(List<String> input) {
        Map<String, Set<String>> edges = new HashMap<>();
        for (String s : input) {
            String[] computers = s.split("-");
            edges.computeIfAbsent(computers[0], k -> new HashSet<>()).add(computers[1]);
            edges.computeIfAbsent(computers[1], k -> new HashSet<>()).add(computers[0]);
        }
        return edges;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(getContOfGroup3T("day23/test.txt"));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(getContOfGroup3T("day23/input.txt"));
        System.out.println("== TEST 2 ==");
        System.out.println(getLargestStrongConnectedGroup("day23/test.txt"));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(getLargestStrongConnectedGroup("day23/input.txt"));
    }
}
