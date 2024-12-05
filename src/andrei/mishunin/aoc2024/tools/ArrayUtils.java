package andrei.mishunin.aoc2024.tools;

public class ArrayUtils {
    private ArrayUtils() {
    }

    public static <T> void reverse(T[] data) {
        int n = data.length - 1;

        for (int i = (n - 1) / 2; i >= 0; i--) {
            int j = n - i;
            var swap = data[i];
            data[i] = data[j];
            data[j] = swap;
        }
    }

    public static void reverse(int[] data) {
        int n = data.length - 1;

        for (int i = (n - 1) / 2; i >= 0; i--) {
            int j = n - i;
            var swap = data[i];
            data[i] = data[j];
            data[j] = swap;
        }
    }

    public static int[] toInt(String[] data) {
        int[] parsedData = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            parsedData[i] = Integer.parseInt(data[i]);
        }
        return parsedData;
    }

    public static Integer[] toInteger(String[] data) {
        Integer[] parsedData = new Integer[data.length];
        for (int i = 0; i < data.length; i++) {
            parsedData[i] = Integer.parseInt(data[i]);
        }
        return parsedData;
    }
}
