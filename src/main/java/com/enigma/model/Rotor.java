package com.enigma.model;

/** Mutable installed rotor: fixed wiring plus ring setting and visible window position. */
public final class Rotor {
    private final RotorSpec spec;
    private final int ringSetting;
    private int position;

    public Rotor(RotorSpec spec, char ringSetting, char startPosition) {
        if (spec == null) throw new IllegalArgumentException("Rotor spec is required");
        this.spec = spec;
        this.ringSetting = letterIndex(ringSetting, "ring setting");
        this.position = letterIndex(startPosition, "start position");
    }

    public RotorSpec spec() { return spec; }
    public int position() { return position; }
    public int ringSetting() { return ringSetting; }
    public char windowLetter() { return (char) ('A' + position); }

    public void setPosition(char letter) {
        position = letterIndex(letter, "rotor position");
    }

    public void step() {
        position = (position + 1) % 26;
    }

    public boolean isAtNotch() {
        return spec.isAtNotch(position, ringSetting);
    }

    public int forward(int input) {
        return spec.forward(input, effectiveOffset());
    }

    public int reverse(int input) {
        return spec.reverse(input, effectiveOffset());
    }

    private int effectiveOffset() {
        return Math.floorMod(position - ringSetting, 26);
    }

    static int letterIndex(char c, String label) {
        char upper = Character.toUpperCase(c);
        if (upper < 'A' || upper > 'Z') {
            throw new IllegalArgumentException(label + " must be A-Z");
        }
        return upper - 'A';
    }
}
