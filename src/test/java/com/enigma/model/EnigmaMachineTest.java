package com.enigma.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EnigmaMachineTest {
    private MachineConfig config(String rings, String positions, List<String> plugs) {
        return new MachineConfig(List.of(RotorSpec.I, RotorSpec.II, RotorSpec.III),
                ReflectorSpec.B, rings, positions, plugs);
    }

    @Test
    void canonicalVectorForThreeRotorsReflectorB() {
        EnigmaMachine machine = new EnigmaMachine(config("AAA", "AAA", List.of()));
        assertEquals("BDZGO", machine.process("AAAAA"));
    }

    @Test
    void sameSettingsAndStartingStateDecryptCiphertext() {
        MachineConfig key = config("AAA", "AAA", List.of("AV", "BS", "CG"));
        String plaintext = "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG";
        String ciphertext = new EnigmaMachine(key).process(plaintext);
        String recovered = new EnigmaMachine(key).process(ciphertext);
        assertEquals(plaintext.replaceAll("[a-z]", "").toUpperCase(), recovered);
    }

    @Test
    void outputNeverEqualsInputForAlphabeticCharacters() {
        EnigmaMachine machine = new EnigmaMachine(config("AAA", "AAA", List.of()));
        String input = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String output = machine.process(input);
        assertEquals(input.length(), output.length());
        for (int i = 0; i < input.length(); i++) assertNotEquals(input.charAt(i), output.charAt(i));
    }

    @Test
    void preservesSpacesAndPunctuationWithoutSteppingForThem() {
        EnigmaMachine withPunctuation = new EnigmaMachine(config("AAA", "AAA", List.of()));
        EnigmaMachine lettersOnly = new EnigmaMachine(config("AAA", "AAA", List.of()));
        assertEquals(lettersOnly.process("ABCD"), withPunctuation.process("AB-CD").replace("-", ""));
        assertEquals("AB-CD".length(), withPunctuation.process("AB-CD").length());
    }

    @Test
    void advancesBeforeFirstLetterAndTracksPosition() {
        EnigmaMachine machine = new EnigmaMachine(config("AAA", "AAA", List.of()));
        machine.process("A");
        assertEquals("AAB", machine.rotorPositions());
    }

    @Test
    void middleRotorDoubleStepsAtItsNotch() {
        EnigmaMachine machine = new EnigmaMachine(config("AAA", "ADV", List.of()));
        // At ADV, the middle rotor D is one step before its E notch and the
        // fast rotor V is at its notch. First press carries middle to E.
        machine.stepRotors();
        assertEquals("AEW", machine.rotorPositions());
        // At AEW, middle is at E notch, so next step advances both left and middle.
        machine.stepRotors();
        assertEquals("BF X".replace(" ", ""), machine.rotorPositions());
    }

    @Test
    void validatesMachineConfiguration() {
        assertThrows(IllegalArgumentException.class, () -> new MachineConfig(
                List.of(RotorSpec.I, RotorSpec.I, RotorSpec.III), ReflectorSpec.B, "AAA", "AAA", List.of()));
        assertThrows(IllegalArgumentException.class, () -> config("AA", "AAA", List.of()));
    }
}
