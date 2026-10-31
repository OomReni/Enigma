package com.enigma.model;

import java.util.Arrays;

/** Immutable wiring and turnover definition for a Wehrmacht/Luftwaffe Enigma I rotor. */
public enum RotorSpec {
    I("EKMFLGDQVZNTOWYHXUSPAIBRCJ", "Q"),
    II("AJDKSIRUXBLHWTMCQGZNPYFVOE", "E"),
    III("BDFHJLCPRTXVZNYEIWGAKMUSQO", "V"),
    IV("ESOVPZJAYQUIRHXLNFTGKDCMWB", "J"),
    V("VZBRGITYUPSDNHLXAWMJQOFECK", "Z");

    private final int[] forward = new int[26];
    private final int[] reverse = new int[26];
    private final int notch;

    RotorSpec(String wiring, String notchLetter) {
        if (wiring.length() != 26) {
            throw new IllegalArgumentException("Rotor wiring must contain 26 letters");
        }
        boolean[] seen = new boolean[26];
        for (int i = 0; i < 26; i++) {
            char c = wiring.charAt(i);
            if (c < 'A' || c > 'Z' || seen[c - 'A']) {
                throw new IllegalArgumentException("Rotor wiring must be a permutation of A-Z");
            }
            seen[c - 'A'] = true;
            forward[i] = c - 'A';
            reverse[c - 'A'] = i;
        }
        this.notch = notchLetter.charAt(0) - 'A';
    }

    public int forward(int input, int offset) {
        int shifted = mod26(input + offset);
        return mod26(forward[shifted] - offset);
    }

    public int reverse(int input, int offset) {
        int shifted = mod26(input + offset);
        return mod26(reverse[shifted] - offset);
    }

    /**
     * Tests the notch against the rotor's effective core position. The ring setting
     * changes the notch's relationship to the visible alphabet window.
     */
    public boolean isAtNotch(int windowPosition, int ringSetting) {
        return mod26(windowPosition - ringSetting) == notch;
    }

    public String wiring() {
        StringBuilder result = new StringBuilder(26);
        Arrays.stream(forward).forEach(i -> result.append((char) ('A' + i)));
        return result.toString();
    }

    public String notchLetter() {
        return String.valueOf((char) ('A' + notch));
    }

    static int mod26(int value) {
        return Math.floorMod(value, 26);
    }
}
