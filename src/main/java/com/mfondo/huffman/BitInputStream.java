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

    void readBits(Bits bits, byte bitCnt) throws IOException {
        //this method could probably be optimized further
        bits.data = 0;
        bits.bitCnt = 0;
        for(int i = 0; i < bitCnt; i++) {
            bits.addHighestBit(readBit());
        }
        bits.reverse();
    }

    //todo make sure called in finally!
    void close() throws IOException {
        is.close();
    }
}
