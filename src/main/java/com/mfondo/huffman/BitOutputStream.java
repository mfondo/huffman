package com.mfondo.huffman;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by mfriesen on 11/23/16.
 *
 * TODO javadoc and unit test
 */
class BitOutputStream {

    private final OutputStream os;
    private byte buffer;
    private byte offset = Byte.SIZE;

    BitOutputStream(OutputStream os) {
        this.os = os;
    }

    void write(Bits bits) throws IOException {
        if(bits.bitCnt > 0) {
            int shift = offset - bits.bitCnt;
            if(shift < 0) {
                if(true) {
                    throw new UnsupportedOperationException();//todo
                }
                //todo shift only some into buffer
                offset = Byte.SIZE;
                os.write(buffer);
                //todo shift the rest into buffer
            } else {
                buffer |= bits.data << shift;
            }
        }
    }

    void flush() throws IOException {
        if(offset < Byte.SIZE) {
            os.write(buffer);
        }
        //todo how to write end of stream if bits don't end on a byte boundary? maybe prefix-free encoding makes it not matter
    }
}
