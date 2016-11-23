package com.mfondo.huffman;

/**
 * Created by mfriesen on 11/23/16.
 *
 * Some bits
 */
class Bits {

    byte data;
    byte bitCnt;

    Bits() {}

    Bits(Bits bits) {
        if(bits != null) {
            this.data = bits.data;
            this.bitCnt = bits.bitCnt;
        }
    }

    void addHighestBit(boolean b) {
        if(bitCnt >= Byte.SIZE) {
            throw new IllegalArgumentException("Too many bits");
        }
        if(b) {
            data |= 1 << (bitCnt++);
        } else {
            data &= ~(1 << (bitCnt++));
        }
    }

    void removeHighestBit() {
        if(bitCnt <= 0) {
            throw new IllegalArgumentException("Too few bits");
        }
        data &= ~(1 << (--bitCnt));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bits bits = (Bits) o;
        if (data != bits.data) return false;
        return bitCnt == bits.bitCnt;

    }

    @Override
    public int hashCode() {
        int result = (int) data;
        result = 31 * result + (int) bitCnt;
        return result;
    }
}
