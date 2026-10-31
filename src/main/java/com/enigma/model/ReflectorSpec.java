package com.enigma.model;

/** Fixed reflector wiring. Each mapping is reciprocal and has no fixed points. */
public enum ReflectorSpec {
    A("EJMZALYXVBWFCRQUONTSPIKHGD"),
    B("YRUHQSLDPXNGOKMIEBFZCWVJAT"),
    C("FVPJIAOYEDRZXWGCTKUQSBNMHL");

    private final int[] mapping = new int[26];

    ReflectorSpec(String wiring) {
        if (wiring.length() != 26) throw new IllegalArgumentException("Reflector wiring must have 26 letters");
        boolean[] seen = new boolean[26];
        for (int i = 0; i < 26; i++) {
            char c = wiring.charAt(i);
            if (c < 'A' || c > 'Z' || seen[c - 'A']) {
                throw new IllegalArgumentException("Reflector wiring must be a permutation");
            }
            mapping[i] = c - 'A';
            seen[c - 'A'] = true;
        }
        for (int i = 0; i < 26; i++) {
            if (mapping[i] == i || mapping[mapping[i]] != i) {
                throw new IllegalArgumentException("Reflector must pair distinct letters reciprocally");
            }
        }
    }

    public int reflect(int input) { return mapping[input]; }
    public String wiring() {
        StringBuilder result = new StringBuilder(26);
        for (int value : mapping) result.append((char) ('A' + value));
        return result.toString();
    }
}
