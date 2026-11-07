package com.enigma.model;

import java.util.List;
import java.util.Locale;

public final class EnigmaMachine {
    private Rotor left;
    private Rotor middle;
    private Rotor right;
    private final ReflectorSpec reflector;
    private Plugboard plugboard;

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

    /**
     * Installs a new left-to-right rotor order and ring setting.
     * The visible window positions are preserved when the hardware is exchanged.
     */
    public void SetRotorConfiguration(List<RotorSpec> rotorOrder, String ringSettings) {
        if (rotorOrder == null || rotorOrder.size() != 3
                || rotorOrder.stream().anyMatch(java.util.Objects::isNull)
                || rotorOrder.stream().distinct().count() != 3) {
            throw new IllegalArgumentException("Select three different rotors");
        }
        if (ringSettings == null || !ringSettings.toUpperCase(Locale.ROOT).matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Ring settings must be exactly three letters A-Z");
        }

        String rings = ringSettings.toUpperCase(Locale.ROOT);
        String positions = rotorPositions();
        left = new Rotor(rotorOrder.get(0), rings.charAt(0), positions.charAt(0));
        middle = new Rotor(rotorOrder.get(1), rings.charAt(1), positions.charAt(1));
        right = new Rotor(rotorOrder.get(2), rings.charAt(2), positions.charAt(2));
    }

    public List<RotorSpec> RotorOrder() {
        return List.of(left.spec(), middle.spec(), right.spec());
    }

    public String RingSettings() {
        return new String(new char[]{
                (char) ('A' + left.ringSetting()),
                (char) ('A' + middle.ringSetting()),
                (char) ('A' + right.ringSetting())
        });
    }

    /**
     * Replaces the plugboard wiring without resetting rotor positions.
     * The supplied pairs are validated by the Plugboard constructor.
     */
    public void setPlugboardPairs(List<String> pairs) {
        plugboard = new Plugboard(pairs);
    }

    public List<String> PlugboardPairs() {
        return plugboard.pairs();
    }

    public static EnigmaMachine create(MachineConfig config) {
        return new EnigmaMachine(config);
    }
}
