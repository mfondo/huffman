package com.mfondo.huffman;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mfriesen on 11/23/16.
 *
 * TODO javadoc and unit test
 */
class BitInputStream {

    private final InputStream is;
    private byte buffer;
    private byte bufferBitPos = -1;

    BitInputStream(InputStream is) {
        this.is = is;
    }

    boolean readBit() throws IOException {
        if(bufferBitPos <= 0) {
            int tmp = is.read();
            if(tmp < 0) {
                throw new EOFException();
            }
            buffer = (byte)tmp;
            bufferBitPos = Byte.SIZE;
        }
        return ((1 << --bufferBitPos) & buffer) != 0;
    }

    //todo make sure called in finally!
    void close() throws IOException {
        is.close();
    }
}
