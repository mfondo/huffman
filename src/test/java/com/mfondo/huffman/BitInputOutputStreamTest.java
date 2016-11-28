package com.mfondo.huffman;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by mfriesen on 11/23/16.
 *
 * Unit tests for {@link BitOutputStream} and {@link BitInputStream}
 */
public class BitInputOutputStreamTest {

    @Test
    public void testWrite() throws Exception {
        Bits bits = new Bits();
        bits.addHighestBit(true);
        testWriteRead(new Bits[] {bits}, new byte[] {(byte)0b10000000});
        bits.addHighestBit(true);
        testWriteRead(new Bits[] {bits}, new byte[] {(byte)0b11000000});
        testWriteRead(new Bits[] {
                newBits(true, true, true, false, false, false),
                newBits(true, true, true, true)},
                new byte[] {(byte)0b11100011, (byte)0b11000000});
    }

    private Bits newBits(boolean... bits) {
        Bits ret = new Bits();
        /**
         * Note that this adds the arguments so that the first element in bits
         * is the most significant digit and the last is the least
         */
        for(int i = bits.length - 1; i >= 0; i--) {
            ret.addHighestBit(bits[i]);
        }
        return ret;
    }

    @Test
    public void testEmptyWriteRead() throws Exception {
        testWriteRead(new Bits());
    }

    @Test
    public void testOneBit() throws Exception {
        Bits bits = new Bits();
        bits.addHighestBit(true);
        testWriteRead(bits);
    }

    @Test
    public void testTwoBits() throws Exception {
        Bits bits = new Bits();
        bits.addHighestBit(false);
        bits.addHighestBit(true);
        testWriteRead(bits);
    }

    @Test
    public void testThreeBits() throws Exception {
        Bits bits = new Bits();
        bits.addHighestBit(true);
        bits.addHighestBit(false);
        bits.addHighestBit(true);
        testWriteRead(bits);
    }

    @Test
    public void testLotsBits() throws Exception {
        List<Bits> bitsList = new ArrayList<>();
        Bits bits = new Bits();
        for(int i = 0; i < 8; i++) {
            bits.addHighestBit(true);
        }
        bitsList.add(bits);
        bits = new Bits();
        for(int i = 0; i < 8; i++) {
            bits.addHighestBit(i % 2 == 0);
        }
        bitsList.add(bits);
        bits = new Bits();
        for(int i = 0; i < 8; i++) {
            bits.addHighestBit(false);
        }
        bitsList.add(bits);
        testWriteRead(bitsList.toArray(new Bits[bitsList.size()]));
    }

    private void testWriteRead(Bits[] bits, byte[] expected) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitOutputStream bos = new BitOutputStream(baos);
        for(Bits bit : bits) {
            bos.write(bit);
        }
        bos.flush();
        assertArrayEquals(expected, baos.toByteArray());
    }

    private void testWriteRead(Bits... bits) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitOutputStream bos = new BitOutputStream(baos);
        int totalInputBits = 0;
        for(Bits bit : bits) {
            bos.write(bit);
            totalInputBits += bit.bitCnt;
        }
        bos.flush();
        BitInputStream bis = new BitInputStream(new ByteArrayInputStream(baos.toByteArray()));
        List<Boolean> readBits = new ArrayList<>();
        try {
            readBits.add(bis.readBit());
        } catch (EOFException e) {
            //end of file
        }
        assertEquals(totalInputBits, readBits.size());
        Bits currentBits = bits[0];//todo advance this based on bits.cnt
        for(int i = 0; i < totalInputBits; i++) {
            //todo assertEquals(readBits.get(i));//todo
        }
    }
}
