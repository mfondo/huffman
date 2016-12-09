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

    @Test
    public void testToString() {
        Bits bits = new Bits();
        assertEquals("", bits.toString());
        bits.addHighestBit(true);
        assertEquals("1", bits.toString());
        bits.removeHighestBit();
        bits.addHighestBit(false);
        assertEquals("0", bits.toString());
        bits.removeHighestBit();
        bits.addHighestBit(true);
        bits.addHighestBit(false);
        bits.addHighestBit(true);
        assertEquals("101", bits.toString());
        bits.addHighestBit(false);
        assertEquals("0101", bits.toString());
    }

    @Test
    public void testToFromString() {
        assertToFromString(new Bits());
        assertToFromString(new Bits("1"));
        assertToFromString(new Bits("10"));
        assertToFromString(new Bits("1010"));
        assertToFromString(new Bits("11111"));
    }

    private void assertToFromString(Bits bits) {
        Bits actual = new Bits(bits.toString());
        assertEquals(bits, actual);
    }

    @Test
    public void testReverse() {
        Bits bits = new Bits();
        bits.reverse();
        assertEquals(new Bits(), bits);

        bits = new Bits("1");
        bits.reverse();
        assertEquals(new Bits("1"), bits);

        bits = new Bits("10");
        bits.reverse();
        assertEquals(new Bits("01"), bits);

        bits = new Bits("1011");
        bits.reverse();
        assertEquals(new Bits("1101"), bits);
    }
}
