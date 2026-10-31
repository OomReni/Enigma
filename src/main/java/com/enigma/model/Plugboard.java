package com.enigma.model;

import java.util.Collection;

/** Plugboard represented by up to 13 disjoint letter swaps. */
public final class Plugboard {
    private final int[] mapping = new int[26];

    public Plugboard() {
        for (int i = 0; i < 26; i++) mapping[i] = i;
    }

    public Plugboard(Collection<String> pairs) {
        this();
        if (pairs == null) throw new IllegalArgumentException("Pairs collection is required");
        if (pairs.size() > 13) throw new IllegalArgumentException("Plugboard supports at most 13 pairs");
        boolean[] used = new boolean[26];
        for (String pair : pairs) {
            if (pair == null || pair.length() != 2) {
                throw new IllegalArgumentException("Each plugboard pair must contain exactly two letters");
            }
            int a = letterIndex(pair.charAt(0));
            int b = letterIndex(pair.charAt(1));
            if (a == b) throw new IllegalArgumentException("A plugboard pair must connect different letters");
            if (used[a] || used[b]) throw new IllegalArgumentException("A letter cannot occur in multiple plugboard pairs");
            used[a] = used[b] = true;
            mapping[a] = b;
            mapping[b] = a;
        }
    }

    public int map(int input) { return mapping[input]; }

    public String swapFor(char letter) {
        int input = letterIndex(letter);
        return String.valueOf((char) ('A' + mapping[input]));
    }

    private static int letterIndex(char c) {
        char upper = Character.toUpperCase(c);
        if (upper < 'A' || upper > 'Z') throw new IllegalArgumentException("Plugboard letters must be A-Z");
        return upper - 'A';
    }
}
