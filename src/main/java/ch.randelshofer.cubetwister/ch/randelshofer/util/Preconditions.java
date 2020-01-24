package ch.randelshofer.util;

public class Preconditions {
    private Preconditions() {
        // prevent instance creation
    }

    public static void checkRange(int value, int min, int max, String variableName) {
        if (min > value || value > max) {
            throw new IllegalArgumentException(variableName + " is out of bounds. value=" + value);
        }
    }
}
