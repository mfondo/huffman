package com.mfondo.huffman;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by mfriesen on 11/23/16.
 *
 * Unit tests for {@link Bits}
 */
public class BitsTest {

    @Test
    public void addClearBits() {
        Bits bits = new Bits();
        bits.addHighestBit(true);
        assertEquals(1, bits.data);
        assertEquals(1, bits.bitCnt);
        bits.removeHighestBit();
        assertEquals(0, bits.data);
        assertEquals(0, bits.bitCnt);

        bits.addHighestBit(false);
        assertEquals(0, bits.data);
        assertEquals(1, bits.bitCnt);
        bits.removeHighestBit();
        assertEquals(0, bits.data);
        assertEquals(0, bits.bitCnt);

        bits.addHighestBit(true);
        bits.addHighestBit(false);
        bits.addHighestBit(true);
        bits.addHighestBit(false);
        bits.addHighestBit(true);
        assertEquals(0b00010101, bits.data);
        assertEquals(5, bits.bitCnt);
        bits.removeHighestBit();
        assertEquals(0b00000101, bits.data);
        assertEquals(4, bits.bitCnt);
        bits.removeHighestBit();
        assertEquals(0b00000101, bits.data);
        assertEquals(3, bits.bitCnt);
        bits.removeHighestBit();
        assertEquals(0b00000001, bits.data);
        assertEquals(2, bits.bitCnt);
    }
}
