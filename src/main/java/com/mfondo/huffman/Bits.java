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

    Bits(String bitStr) {
        if(bitStr != null) {
            char c;
            for (int i = bitStr.length() - 1; i >= 0; i--) {
                c = bitStr.charAt(i);
                if('1' == c) {
                    data |= 1 << bitCnt;
                } else if('0' != c) {
                    throw new IllegalArgumentException("Invalid bit string " + bitStr);
                }
                bitCnt++;
            }
        }
    }

    Bits(byte data) {
        this.data = data;
        bitCnt = Byte.SIZE;
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

    boolean getBit(int index) {
        if(index < 0 || index >= bitCnt) {
            throw new IllegalArgumentException("Invalid offset " + index + " size " + bitCnt);
        }
        return ((1 << index) & data) != 0;
    }

    void reverse() {
        //a lookup table would probably be faster here
        byte tmp = 0;
        int j = bitCnt - 1;
        for(int i = 0; i < bitCnt; i++) {
            if(getBit(i)) {
                tmp |= 1 << j;
            }
            j--;
        }
        data = tmp;
    }

    void setData(byte data) {
        this.data = data;
        this.bitCnt = Byte.SIZE;
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

    @Override
    public String toString() {
        char[] ret = new char[bitCnt];
        int index = 0;
        for(int i = bitCnt - 1; i >= 0; i--) {
            ret[index++] = ((1 << i) & data) != 0 ? '1' : '0';
        }
        return new String(ret);
    }
}
