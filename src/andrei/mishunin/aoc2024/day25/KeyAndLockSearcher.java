package andrei.mishunin.aoc2024.day25;

import andrei.mishunin.aoc2024.tools.InputReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KeyAndLockSearcher {
    static final long KEY_MASK = 0b11111;

    public static long findCountFitsCombinations(String file) {
        List<String> input = InputReader.readAllLines(file);
        List<Long> keys = new ArrayList<>();
        List<Long> locks = new ArrayList<>();

        Iterator<String> iterator = input.iterator();
        while (iterator.hasNext()) {
            long keyOrLock = getKeyOrLock(iterator);
            if ((keyOrLock & KEY_MASK) == KEY_MASK) {
                keys.add(keyOrLock);
            } else {
                locks.add(keyOrLock);
            }
        }

        int fitsCount = 0;
        for (Long key : keys) {
            for (Long lock : locks) {
                if ((key & lock) == 0) {
                    fitsCount++;
                }
            }
        }

        return fitsCount;
    }

    private static long getKeyOrLock(Iterator<String> inputIterator) {
        long keyOrLock = 0;
        while (inputIterator.hasNext()) {
            String line = inputIterator.next();
            if (line.isEmpty()) {
                return keyOrLock;
            }
            for (int i = 0; i < line.length(); i++) {
                keyOrLock <<= 1;
                keyOrLock |= line.charAt(i) == '#' ? 1 : 0;
            }
        }
        return keyOrLock;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(findCountFitsCombinations("day25/test.txt"));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(findCountFitsCombinations("day25/input.txt"));
    }
}
