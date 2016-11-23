package com.mfondo.huffman;

import java.io.EOFException;
import java.io.InputStream;

/**
 * Created by mfriesen on 11/23/16.
 *
 * TODO javadoc and unit test
 */
class BitInputStream {

    private final InputStream is;

    BitInputStream(InputStream is) {
        this.is = is;
    }

    boolean readBit() throws EOFException {
        //todo how to handle EOF?
        throw new UnsupportedOperationException();//todo
    }
}
