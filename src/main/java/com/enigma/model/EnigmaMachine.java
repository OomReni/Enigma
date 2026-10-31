package com.enigma.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Stateful three-moving-rotor Enigma I simulation.
 *
 * Rotor list and displayed strings use left-to-right order: slow, middle, fast.
 * Each alphabetic character steps the machine before electrical substitution.
 */
public final class EnigmaMachine {
    private final Rotor left;
    private final Rotor middle;
    private final Rotor right;
    private final ReflectorSpec reflector;
    private final Plugboard plugboard;

    public EnigmaMachine(MachineConfig config) {
        if (config == null) throw new IllegalArgumentException("Machine configuration is required");
        List<RotorSpec> specs = config.rotorOrder();
        String rings = config.ringSettings();
        String starts = config.startPositions();
        left = new Rotor(specs.get(0), rings.charAt(0), starts.charAt(0));
        middle = new Rotor(specs.get(1), rings.charAt(1), starts.charAt(1));
        right = new Rotor(specs.get(2), rings.charAt(2), starts.charAt(2));
        reflector = config.reflector();
        plugboard = new Plugboard(config.plugboardPairs());
    }

    /** Encrypts/decrypts text, preserving non-letters without stepping for them. */
    public String process(String input) {
        if (input == null) throw new IllegalArgumentException("Input text cannot be null");
        StringBuilder output = new StringBuilder(input.length());
        for (char original : input.toCharArray()) {
            char upper = Character.toUpperCase(original);
            if (upper < 'A' || upper > 'Z') {
                output.append(original);
                continue;
            }
            stepRotors();
            int signal = plugboard.map(upper - 'A');
            signal = right.forward(signal);
            signal = middle.forward(signal);
            signal = left.forward(signal);
            signal = reflector.reflect(signal);
            signal = left.reverse(signal);
            signal = middle.reverse(signal);
            signal = right.reverse(signal);
            signal = plugboard.map(signal);
            output.append((char) ('A' + signal));
        }
        return output.toString();
    }

    /**
     * Enigma pawls engage based on the pre-step notch state. This reproduces
     * the middle rotor's double-step behavior:
     * - middle at notch: left and middle step;
     * - otherwise, right at notch: middle steps;
     * - right always steps.
     */
    public void stepRotors() {
        boolean middleAtNotch = middle.isAtNotch();
        boolean rightAtNotch = right.isAtNotch();
        if (middleAtNotch) left.step();
        if (middleAtNotch || rightAtNotch) middle.step();
        right.step();
    }

    public String rotorPositions() {
        return new String(new char[]{left.windowLetter(), middle.windowLetter(), right.windowLetter()});
    }

    public void setRotorPositions(String positions) {
        if (positions == null || positions.length() != 3 || !positions.toUpperCase(Locale.ROOT).matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Rotor positions must be exactly three letters A-Z");
        }
        String upper = positions.toUpperCase(Locale.ROOT);
        left.setPosition(upper.charAt(0));
        middle.setPosition(upper.charAt(1));
        right.setPosition(upper.charAt(2));
    }

    /** Creates a fresh machine with the same immutable key settings and original start positions. */
    public static EnigmaMachine create(MachineConfig config) {
        return new EnigmaMachine(config);
    }
}
