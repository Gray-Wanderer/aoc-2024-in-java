package andrei.mishunin.aoc2024.day11;

import andrei.mishunin.aoc2024.tools.InputReader;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Solution {
    static Map<String, Map<Integer, BigInteger>> TRANSFORM_COUNT = new HashMap<>();

    public static long solve(String file, int iterations) {
        List<String> input = InputReader.readAllLines(file);
        String[] numbers = input.get(0).split(" ");

        NumberNode head = new NumberNode(numbers[0]);
        NumberNode current = head;
        for (int i = 1; i < numbers.length; i++) {
            NumberNode node = new NumberNode(numbers[i]);
            node.prev = current;
            current.next = node;
            current = node;
        }

        for (int i = 0; i < iterations; i++) {
            NumberNode node = head;
            while (node != null) {
                node = node.transform().next;
            }
//            System.out.println();
        }

        long count = 0;
        NumberNode node = head;
        while (node != null) {
            node = node.next;
            count++;
        }

        return count;
    }

    public static String solve2(String file, int iterations) {
        List<String> input = InputReader.readAllLines(file);
        String[] numberS = input.get(0).split(" ");

        BigInteger[] numbers = new BigInteger[numberS.length];
        for (int i = 0; i < numberS.length; i++) {
            numbers[i] = new BigInteger(numberS[i]);
        }

        BigInteger count = BigInteger.ZERO;
        for (BigInteger number : numbers) {
            count = count.add(transformCount(number, iterations));
        }

        return count.toString();
    }


    private static BigInteger transformCount(BigInteger number, int steps) {
        LinkedList<Pair> stack = new LinkedList<>();
        stack.push(new Pair(number, steps));

        while (!stack.isEmpty()) {
            Pair pair = stack.removeLast();

            String numberS = pair.num.toString();
            BigInteger memo = getFromMemo(pair.num, pair.step);
            if (memo != null) {
                continue;
            }

            if (BigInteger.ZERO.compareTo(pair.num) == 0) {
                memo = getFromMemo(pair.num, pair.step - 1);
                if (memo == null) {
                    stack.addLast(pair);
                    stack.addLast(new Pair(BigInteger.ONE, pair.step - 1));
                } else {
                    putToMemo(pair.num, pair.step, memo);
                }
            } else if (numberS.length() % 2 == 0) {
                String s1 = numberS.substring(0, numberS.length() / 2);
                BigInteger n1 = new BigInteger(s1);
                String s2 = numberS.substring(numberS.length() / 2);
                BigInteger n2 = new BigInteger(s2);
                BigInteger memo1 = getFromMemo(n1, pair.step - 1);
                BigInteger memo2 = getFromMemo(n2, pair.step - 1);
                if (memo1 != null && memo2 != null) {
                    putToMemo(pair.num, pair.step, memo1.add(memo2));
                } else {
                    stack.addLast(pair);
                    if (memo1 == null) {
                        stack.addLast(new Pair(n1, pair.step - 1));
                    }
                    if (memo2 == null) {
                        stack.addLast(new Pair(n2, pair.step - 1));
                    }
                }
            } else {
                BigInteger nextNumber = pair.num.multiply(BigInteger.valueOf(2024));
                memo = getFromMemo(nextNumber, pair.step - 1);
                if (memo == null) {
                    stack.addLast(pair);
                    stack.addLast(new Pair(nextNumber, pair.step - 1));
                } else {
                    putToMemo(nextNumber, pair.step, memo);
                }
            }
        }

        return getFromMemo(number, steps);
    }

    public static void putToMemo(BigInteger number, int step, BigInteger count) {
        String numberS = number.toString();
        TRANSFORM_COUNT.computeIfAbsent(numberS, s -> new HashMap<>())
                .put(step, count);
    }

    private static BigInteger getFromMemo(BigInteger number, int steps) {
        if (steps == 0) {
            return BigInteger.ONE;
        }

        String numberS = number.toString();
        var memo = TRANSFORM_COUNT.computeIfAbsent(numberS, s -> new HashMap<>());
        if (memo.containsKey(steps)) {
            return memo.get(steps);
        }
        return null;
    }

    private record Pair(BigInteger num, int step) {
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(solve2("day11/test.txt", 25));
//        System.out.println("== SOLUTION 1 ==");
//        System.out.println(solve("day11/input.txt", 25));
//        System.out.println("== TEST 2 ==");
//        System.out.println(solve("day11/test.txt"));
//        System.out.println("== SOLUTION 2 ==");
//        System.out.println(solve("day11/input.txt", 75));
    }

    private static class NumberNode {
        String numberS;
        BigInteger number;
        NumberNode prev = null;
        NumberNode next = null;

        public NumberNode(String numberS) {
            this.numberS = numberS;
            this.number = new BigInteger(numberS);
        }

        public NumberNode(BigInteger number) {
            this.numberS = number.toString();
            this.number = number;
        }

        public NumberNode transform() {
            if (BigInteger.ZERO.compareTo(number) == 0) {
                numberS = "1";
                number = BigInteger.ONE;
//                System.out.print(number + " ");
                return this;
            }
            if (numberS.length() % 2 == 0) {
                BigInteger s1 = new BigInteger(numberS.substring(0, numberS.length() / 2));
                BigInteger s2 = new BigInteger(numberS.substring(numberS.length() / 2));
                numberS = s1.toString();
                number = s1;
                NumberNode newNode = new NumberNode(s2);
                newNode.next = this.next;
                newNode.prev = this;
                this.next = newNode;
//                System.out.print(s1 + " " + s2 + " ");
                return newNode;
            }
            number = number.multiply(BigInteger.valueOf(2024));
            numberS = number.toString();
//            System.out.print(number + " ");
            return this;
        }
    }
}
