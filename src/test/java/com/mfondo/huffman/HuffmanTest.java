package com.mfondo.huffman;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by mfriesen on 11/23/16.
 *
 * Unit tests for {@link Huffman}
 */
public class HuffmanTest {

    @Test
    public void testBuildTree() {
        assertSymbols(new byte[] {1, 2}, 1, new Bits("1"), 2, new Bits("0"));
        assertSymbols(new byte[] {1, 2, 2, 3}, 1, new Bits("01"), 2, new Bits("1"), 3, new Bits("00"));
        assertSymbols(new byte[] {1, 2, 3, 3, 3, 3, 4, 4}, 1, new Bits("001"), 2, new Bits("000"), 3, new Bits("1"), 4, new Bits("01"));
        //todo todo test all the boundary conditions, including empty
    }

    private void assertSymbols(byte[] bytes, Object... expectedSymbols) {
        Huffman.Node actualRoot = Huffman.buildTree(bytes, bytes.length);
        assertSymbols(actualRoot, expectedSymbols);
    }

    @Test
    public void testReadWriteByteBitsMap() throws IOException {
        Map<Byte, Bits> byteBitsMap = new HashMap<>();
        assertReadWriteByteBitsMap(byteBitsMap);

        byteBitsMap.put((byte)1, new Bits("1"));
        assertReadWriteByteBitsMap(byteBitsMap);

        byteBitsMap.put((byte)2, new Bits("0010"));
        assertReadWriteByteBitsMap(byteBitsMap);

        byteBitsMap.put((byte)5, new Bits("010"));
        assertReadWriteByteBitsMap(byteBitsMap);
    }

    private void assertReadWriteByteBitsMap(Map<Byte, Bits> expected) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitOutputStream bos = new BitOutputStream(baos);
        Huffman.writeByteBitsMap(expected, bos);
        bos.flush();
        BitInputStream bis = new BitInputStream(new ByteArrayInputStream(baos.toByteArray()));
        Map<Byte, Bits> actual = Huffman.readByteBitsMap(bis);
        assertEquals(expected, actual);
    }

    private void assertSymbols(Huffman.Node root, Object... expectedSymbols) {
        Map<Byte, Bits> expectedSymbolsMap = new HashMap<>();
        assertEquals(0, expectedSymbols.length % 2);
        for(int i = 0; i < expectedSymbols.length; i++) {
            Object tmp = expectedSymbols[i++];
            byte byteVal;
            if(tmp instanceof Byte) {
                byteVal = (Byte)tmp;
            } else if (tmp instanceof Number) {
                byteVal = ((Number)tmp).byteValue();
            } else {
                throw new IllegalArgumentException("Invalid byte " + tmp);
            }
            expectedSymbolsMap.put(byteVal, (Bits)expectedSymbols[i]);
        }
        assertSymbols(root, expectedSymbolsMap);
    }

    private void assertSymbols(Huffman.Node root, Map<Byte, Bits> expectedSymbols) {
        Map<Byte, Bits> actualSymbols = new HashMap<>();
        Huffman.populateEncodedBits(root, new Bits(), actualSymbols);
        assertEquals(expectedSymbols, actualSymbols);
    }

    //todo generate random bit sequences - write/then read them and assert equal - if not, generate the bit sequence that didn't work
}
