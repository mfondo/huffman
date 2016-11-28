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
                buffer |= bits.data >> -shift;
                os.write(buffer);
                buffer = 0;
                int bitsRemaining = bits.bitCnt - offset;
                offset = (byte)(Byte.SIZE - bitsRemaining);
                buffer |= bits.data << offset;
            } else {
                buffer |= bits.data << shift;
                offset -= bits.bitCnt;
            }
        }
    }

    void flush() throws IOException {
        //todo not quite right - this buffer will be in a weird state if this is written again
        if(offset < Byte.SIZE) {
            os.write(buffer);
        }
        //todo how to write end of stream if bits don't end on a byte boundary? maybe prefix-free encoding makes it not matter
    }
}
