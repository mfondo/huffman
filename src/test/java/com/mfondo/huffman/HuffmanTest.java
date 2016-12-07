package com.mfondo.huffman;

import org.junit.Test;

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
        byte[] bytes = new byte[] {1, 2, 2, 3};
        Huffman.Node actualRoot = Huffman.buildTree(bytes, bytes.length);
        assertSymbols(actualRoot, 1, new Bits("11"), 2, new Bits("1"), 3, new Bits("01"));
        //todo todo test all the boundary conditions, including empty
    }

    @Test
    public void testSerializeTree() {
        //todo
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
