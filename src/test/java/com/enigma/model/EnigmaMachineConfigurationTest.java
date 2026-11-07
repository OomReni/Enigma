package com.enigma.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnigmaMachineConfigurationTest {

    @Test
    void rotorConfigurationChangesHardwareAndRingSettingsWithoutMovingWindows() {
        EnigmaMachine machine = new EnigmaMachine(new MachineConfig(
                List.of(RotorSpec.I, RotorSpec.II, RotorSpec.III),
                ReflectorSpec.B,
                "AAA",
                "MCK",
                List.of()));

        machine.SetRotorConfiguration(List.of(RotorSpec.IV, RotorSpec.V, RotorSpec.II), "BCD");

        assertEquals(List.of(RotorSpec.IV, RotorSpec.V, RotorSpec.II), machine.RotorOrder());
        assertEquals("BCD", machine.RingSettings());
        assertEquals("MCK", machine.rotorPositions());
    }

    @Test
    void rotorConfigurationPreservesExistingPlugboardWiring() {
        List<RotorSpec> newOrder = List.of(RotorSpec.IV, RotorSpec.V, RotorSpec.II);
        EnigmaMachine machine = new EnigmaMachine(new MachineConfig(
                List.of(RotorSpec.I, RotorSpec.II, RotorSpec.III),
                ReflectorSpec.B,
                "AAA",
                "MCK",
                List.of("AB")));

        machine.SetRotorConfiguration(newOrder, "BCD");

        EnigmaMachine expected = new EnigmaMachine(new MachineConfig(
                newOrder,
                ReflectorSpec.B,
                "BCD",
                "MCK",
                List.of("AB")));

        assertEquals(expected.process("ENIGMATEST"), machine.process("ENIGMATEST"));
    }

    @Test
    void rotorConfigurationRejectsDuplicateRotors() {
        EnigmaMachine machine = new EnigmaMachine(MachineConfig.defaultConfig());

        assertThrows(IllegalArgumentException.class,
                () -> machine.SetRotorConfiguration(
                        List.of(RotorSpec.I, RotorSpec.I, RotorSpec.III), "AAA"));
    }

    @Test
    void rotorConfigurationRejectsInvalidRingSettings() {
        EnigmaMachine machine = new EnigmaMachine(MachineConfig.defaultConfig());

        assertThrows(IllegalArgumentException.class,
                () -> machine.SetRotorConfiguration(
                        List.of(RotorSpec.I, RotorSpec.II, RotorSpec.III), "AA"));
    }
}
