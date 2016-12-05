package com.mfondo.huffman;

import org.junit.Test;

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
        Huffman.Node expectedRoot = null;//todo
        assertEquals(expectedRoot, actualRoot);
        //todo todo test all the boundary conditions, including empty
    }

    @Test
    public void testSerializeTree() {
        //todo
    }

    //todo generate random bit sequences - write/then read them and assert equal - if not, generate the bit sequence that didn't work
}
