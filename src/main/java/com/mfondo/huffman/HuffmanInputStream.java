package main.java.com.mfondo.huffman;

import java.io.FilterInputStream;
import java.io.InputStream;

/**
 * Created by mfriesen on 11/18/16.
 *
 * TODO
 */
public class HuffmanInputStream extends FilterInputStream {

    public HuffmanInputStream(InputStream in) {
        super(in);
    }
}
