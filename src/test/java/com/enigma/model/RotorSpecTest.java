package com.enigma.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RotorSpecTest {
    @Test
    void forwardAndReverseAreInversesAtEveryOffset() {
        for (RotorSpec spec : RotorSpec.values()) {
            for (int offset = 0; offset < 26; offset++) {
                for (int letter = 0; letter < 26; letter++) {
                    assertEquals(letter, spec.reverse(spec.forward(letter, offset), offset),
                            spec + " offset=" + offset + " letter=" + letter);
                }
            }
        }
    }

    @Test
    void knownRotorWiringIsCorrect() {
        assertEquals("EKMFLGDQVZNTOWYHXUSPAIBRCJ", RotorSpec.I.wiring());
        assertEquals("AJDKSIRUXBLHWTMCQGZNPYFVOE", RotorSpec.II.wiring());
        assertEquals("BDFHJLCPRTXVZNYEIWGAKMUSQO", RotorSpec.III.wiring());
    }
}
