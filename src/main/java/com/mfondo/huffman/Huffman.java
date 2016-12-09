package com.mfondo.huffman;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Created by mfriesen on 11/18/16.
 *
 * Huffman encoding/decoding methods
 * https://en.wikipedia.org/wiki/Huffman_coding
 */
class Huffman {

    private static final Comparator<Node> NODE_CNT_COMPARATOR = new Comparator<Node>() {
        @Override
        public int compare(Node o1, Node o2) {
            return Integer.compare(o1.cnt, o2.cnt);
        }
    };

    public static void readHuffmanEncoded(InputStream is, OutputStream os) throws IOException {
        Node rootNode = null;//todo read this from stream
        BitInputStream bis = new BitInputStream(is);
        boolean bit;
        Node node = rootNode;
        try {
            bit = bis.readBit();
            if(bit) {
                node = node.leftChild;
            } else {
                node = node.rightChild;
            }
            if(node.rightChild == null || node.leftChild == null) {
                //found a leaf node
                os.write(node.data);
                node = rootNode;
            }
        } catch (EOFException e) {
            //end of file, ignore
        }
    }

    public static void writeHuffmanEncoded(InputStream is, OutputStream os, int chunkSize) throws IOException {
        //todo read chunkSize bytes into buffer (detect end of stream and adjust)
        byte[] buffer = new byte[chunkSize];
        Node rootParent = buildTree(buffer, -1);//todo cnt
        Map<Byte, Bits> byteBitsMap = new HashMap<>();
        //todo writing the number of symbols to be encoded first will help with trailing bits on the bitstream
        //todo "canonical huffman code would be more efficient to serialize: https://en.wikipedia.org/wiki/Canonical_Huffman_code the tree
        populateEncodedBits(rootParent, new Bits(), byteBitsMap);
        byte tmp;
        Node node;
        //todo write the rootParent tree
        BitOutputStream bitOutputStream = new BitOutputStream(os);
        for(int i = 0; i < buffer.length; i++) {
            tmp = buffer[i];
            Bits bits = byteBitsMap.get(tmp);
            bitOutputStream.write(bits);
        }
        bitOutputStream.flush();
    }

    /*
    TODO
    for each child node, populate the bits by traversing to the root
     */
    static void populateEncodedBits(Node node, Bits bits, Map<Byte, Bits> map) {
        if(node != null) {
            boolean hasChildren = false;
            if(node.leftChild != null) {
                bits.addHighestBit(true);
                populateEncodedBits(node.leftChild, bits, map);
                hasChildren = true;
                bits.removeHighestBit();
            }
            if(node.rightChild != null) {
                bits.addHighestBit(false);
                populateEncodedBits(node.rightChild, bits, map);
                hasChildren = true;
                bits.removeHighestBit();
            }
            if(!hasChildren) {
                //copy bits since it is modified in here
                Bits copy = new Bits(bits);
                copy.reverse();
                map.put(node.data, copy);
            }
        }
    }

    //default access for unit tests
    static Node buildTree(byte[] buffer, int cnt) {
        Map<Byte, Integer> valueCnts = getValueCounts(buffer, cnt);
        PriorityQueue<Node> queue = new PriorityQueue<>(cnt, NODE_CNT_COMPARATOR);
        for(Map.Entry<Byte, Integer> entry : valueCnts.entrySet()) {
            Node node = new Node();
            node.data = entry.getKey();
            node.cnt = entry.getValue();
            queue.add(node);
        }
        int queueSize;
        Node node1;
        Node node2;
        Node combinedNode;
        while(true) {
            queueSize = queue.size();
            if(queueSize > 1) {
                node1 = queue.poll();
                node2 = queue.poll();
                combinedNode = new Node();
                combinedNode.cnt = node1.cnt + node2.cnt;
                combinedNode.leftChild = node1;
                combinedNode.rightChild = node2;
                node1.parent = combinedNode;
                node2.parent = combinedNode;
                queue.add(combinedNode);
            } else {
                return queue.iterator().next();
            }
        }
    }

    private static Map<Byte, Integer> getValueCounts(byte[] buffer, int cnt) {
        Map<Byte, Integer> valueCnts = new HashMap<Byte, Integer>();
        byte data;
        for(int i = 0; i < cnt; i++) {
            data = buffer[i];
            Integer dataCnt = valueCnts.get(data);
            if(dataCnt == null) {
                dataCnt = 0;
            }
            valueCnts.put(data, ++dataCnt);
        }
        return valueCnts;
    }

    //default access for unit tests
    static class Node {

        Node parent;
        Node leftChild;
        Node rightChild;
        byte data;
        int cnt;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            if (data != node.data) return false;
            if (cnt != node.cnt) return false;
            if (parent != null ? !parent.equals(node.parent) : node.parent != null) return false;
            if (leftChild != null ? !leftChild.equals(node.leftChild) : node.leftChild != null) return false;
            return rightChild != null ? rightChild.equals(node.rightChild) : node.rightChild == null;
        }

        @Override
        public int hashCode() {
            int result = parent != null ? parent.hashCode() : 0;
            result = 31 * result + (leftChild != null ? leftChild.hashCode() : 0);
            result = 31 * result + (rightChild != null ? rightChild.hashCode() : 0);
            result = 31 * result + (int) data;
            result = 31 * result + cnt;
            return result;
        }

        @Override
        public String toString() {
            //todo this throws StackOverflow errors because of recursive toStrings() on Nodes refs
            final StringBuilder sb = new StringBuilder("Node{");
            sb.append("parent=").append(parent);
            sb.append(", leftChild=").append(leftChild);
            sb.append(", rightChild=").append(rightChild);
            sb.append(", data=").append(data);
            sb.append(", cnt=").append(cnt);
            sb.append('}');
            return sb.toString();
        }
    }
}
