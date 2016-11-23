package com.mfondo.huffman;

/**
 * Created by mfriesen on 11/23/16.
 *
 * TODO javadoc and unit test
 */
public class Bits {

    public byte data;
    public byte bitCnt;

    public Bits() {}

    public Bits(Bits bits) {
        if(bits != null) {
            this.data = bits.data;
            this.bitCnt = bits.bitCnt;
        }
    }

    //todo unit test
    void addHighestBit(boolean b) {
        if(b) {
            data |= 1 << (bitCnt++);
        }
        //todo precondition on bitCnt overflowing
    }

    //todo unit test
    void removeHighestBit() {
        //data &=;//todo decrement bitCnt
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
