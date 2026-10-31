package com.enigma.model;

import java.util.List;

/** Immutable initial configuration for a three-rotor Enigma I machine. */
public record MachineConfig(
        List<RotorSpec> rotorOrder,
        ReflectorSpec reflector,
        String ringSettings,
        String startPositions,
        List<String> plugboardPairs) {

    public MachineConfig {
        if (rotorOrder == null || rotorOrder.size() != 3) {
            throw new IllegalArgumentException("Exactly three rotors are required, ordered left-to-right");
        }
        if (rotorOrder.stream().anyMatch(java.util.Objects::isNull)) {
            throw new IllegalArgumentException("Rotor selections cannot be null");
        }
        if (rotorOrder.stream().distinct().count() != 3) {
            throw new IllegalArgumentException("A rotor may only be installed once");
        }
        if (reflector == null) throw new IllegalArgumentException("Reflector is required");
        ringSettings = validateLetters(ringSettings, "ring settings");
        startPositions = validateLetters(startPositions, "start positions");
        if (plugboardPairs == null) throw new IllegalArgumentException("Plugboard pairs are required");
        plugboardPairs = List.copyOf(plugboardPairs);
        // Validate pair rules during configuration creation.
        new Plugboard(plugboardPairs);
        rotorOrder = List.copyOf(rotorOrder);
    }

    private static String validateLetters(String value, String label) {
        if (value == null || value.length() != 3) {
            throw new IllegalArgumentException(label + " must be exactly three letters");
        }
        String upper = value.toUpperCase(java.util.Locale.ROOT);
        if (!upper.matches("[A-Z]{3}")) throw new IllegalArgumentException(label + " must contain only A-Z");
        return upper;
    }

    public static MachineConfig defaultConfig() {
        return new MachineConfig(
                List.of(RotorSpec.I, RotorSpec.II, RotorSpec.III),
                ReflectorSpec.B, "AAA", "AAA", List.of());
    }
}
