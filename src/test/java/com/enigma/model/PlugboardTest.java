package com.enigma.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PlugboardTest {
    @Test
    void swapsPairedLettersAndLeavesOthersAlone() {
        Plugboard board = new Plugboard(List.of("AV", "BS", "CG"));
        assertEquals("V", board.swapFor('A'));
        assertEquals("A", board.swapFor('V'));
        assertEquals("S", board.swapFor('B'));
        assertEquals("D", board.swapFor('D'));
    }

    @Test
    void rejectsMalformedAndInvalidPairs() {
        assertThrows(IllegalArgumentException.class, () -> new Plugboard(List.of("A")));
        assertThrows(IllegalArgumentException.class, () -> new Plugboard(List.of("A1")));
        assertThrows(IllegalArgumentException.class, () -> new Plugboard(List.of("AA")));
    }

    @Test
    void rejectsOverlappingPairs() {
        assertThrows(IllegalArgumentException.class, () -> new Plugboard(List.of("AB", "AC")));
    }

    @Test
    void rejectsMoreThanThirteenPairs() {
        assertThrows(IllegalArgumentException.class, () -> new Plugboard(
                List.of("AB", "CD", "EF", "GH", "IJ", "KL", "MN", "OP", "QR", "ST", "UV", "WX", "YZ", "AC")));
    }
}
