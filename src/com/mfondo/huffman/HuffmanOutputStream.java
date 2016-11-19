package com.mfondo.huffman;

import java.io.FilterOutputStream;
import java.io.OutputStream;

/**
 * Created by mfriesen on 11/18/16.
 *
 * TODO
 */
public class HuffmanOutputStream extends FilterOutputStream {

    public HuffmanOutputStream(OutputStream out) {
        super(out);
    }
}
