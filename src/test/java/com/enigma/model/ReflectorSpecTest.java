package com.enigma.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReflectorSpecTest {
    @Test
    void everyReflectorIsReciprocalAndHasNoFixedPoints() {
        for (ReflectorSpec reflector : ReflectorSpec.values()) {
            for (int i = 0; i < 26; i++) {
                int out = reflector.reflect(i);
                assertNotEquals(i, out);
                assertEquals(i, reflector.reflect(out));
            }
        }
    }
}
