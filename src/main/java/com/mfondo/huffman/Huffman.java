package com.mfondo.huffman;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.HashMap;
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
        BitInputStream bis = new BitInputStream(is);
        try {
            while(true) {
                //read chunks until eof
                Map<Byte, Bits> byteBitsMap = readByteBitsMap(bis);
                Node rootNode = new Node();
                buildTreeFromMap(rootNode, byteBitsMap);
                Bits bits = new Bits();
                bis.readBits(bits, (byte) Byte.SIZE);
                int cnt = bits.data;
                for (int i = 0; i < cnt; i++) {
                    readByte(rootNode, bis, os);
                }
            }
        } catch (EOFException e) {
            //ignore
        }
    }

    static void readByte(Node rootNode, BitInputStream bis, OutputStream os) throws IOException {
        boolean bit;
        Node node = rootNode;
        while(true) {
            bit = bis.readBit();
            if (bit) {
                node = node.leftChild;
            } else {
                node = node.rightChild;
            }
            if (node.data != null) {
                //found a leaf node
                os.write(node.data);
                break;
            }
        }
    }

    public static void writeHuffmanEncoded(InputStream is, OutputStream os, int bufferSize) throws IOException {
        byte[] buffer = new byte[bufferSize];
        int offset = 0;
        int cnt;
        BitOutputStream bos = new BitOutputStream(os);
        while((cnt = is.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += cnt;
            if(offset >= buffer.length) {
                writeHuffmanEncoded(buffer, buffer.length, bos);
                offset = 0;
            }
        }
        if(offset > 0) {
            writeHuffmanEncoded(buffer, offset, bos);
        }
        bos.flush();
    }

    private static void writeHuffmanEncoded(byte[] buffer, int length, BitOutputStream bitOutputStream) throws IOException {
        Node rootParent = buildTree(buffer, length);
        Map<Byte, Bits> byteBitsMap = new HashMap<>();
        populateEncodedBits(rootParent, new Bits(), byteBitsMap);
        writeByteBitsMap(byteBitsMap, bitOutputStream);
        Bits bits = new Bits();
        bits.setData((byte)length);
        bitOutputStream.write(bits);
        byte data;
        for(int i = 0; i < length; i++) {
            data = buffer[i];
            bits = byteBitsMap.get(data);
            bitOutputStream.write(bits);
        }
    }

    static void writeByteBitsMap(Map<Byte, Bits> byteBitsMap, BitOutputStream bos) throws IOException {
        /**
         * todo
         * "canonical huffman code" would be more efficient to serialize
         * see https://en.wikipedia.org/wiki/Canonical_Huffman_code
         */
        Bits tmp = new Bits();
        tmp.setData((byte)byteBitsMap.size());//todo any overflow/underflow issues here?
        bos.write(tmp);
        Bits symbol;
        for(Map.Entry<Byte, Bits> entry : byteBitsMap.entrySet()) {
            tmp.setData(entry.getKey());
            bos.write(tmp);
            symbol = entry.getValue();
            tmp.setData(symbol.bitCnt);
            bos.write(tmp);
            bos.write(symbol);
        }
    }

    static Map<Byte, Bits> readByteBitsMap(BitInputStream bis) throws IOException {
        final Bits tmp = new Bits();
        bis.readBits(tmp, (byte)Byte.SIZE);
        final int size = tmp.data;
        byte data;
        Map<Byte, Bits> ret = new HashMap<>(size);
        for(int i = 0; i < size; i++) {
            bis.readBits(tmp, (byte)Byte.SIZE);
            data = tmp.data;
            bis.readBits(tmp, (byte)Byte.SIZE);
            bis.readBits(tmp, tmp.data);
            ret.put(data, new Bits(tmp));
        }
        return ret;
    }

    /*
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

    static void buildTreeFromMap(Node rootNode, Map<Byte, Bits> map) {
        for(Map.Entry<Byte, Bits> entry : map.entrySet()) {
            Byte data = entry.getKey();
            Bits bits = entry.getValue();
            buildTree(rootNode, data, bits);
        }
    }

    private static void buildTree(Node node, Byte data, Bits bits) {
        boolean tmp;
        for(int i = bits.bitCnt - 1; i >= 0; i--) {
            tmp = bits.getBit(i);
            Node childNode = null;
            if(tmp) {
                if(node.leftChild == null) {
                    childNode = new Node();
                    childNode.parent = node;
                    node.leftChild = childNode;
                } else {
                    childNode = node.leftChild;
                }
            } else {
                if(node.rightChild == null) {
                    childNode = new Node();
                    childNode.parent = node;
                    node.rightChild = childNode;
                } else {
                    childNode = node.rightChild;
                }
            }
            if(i == 0) {
                childNode.data = data;
            } else {
                node = childNode;
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
        Byte data;
        int cnt;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            if (cnt != node.cnt) return false;
            if (parent != null ? !parent.equals(node.parent) : node.parent != null) return false;
            if (leftChild != null ? !leftChild.equals(node.leftChild) : node.leftChild != null) return false;
            if (rightChild != null ? !rightChild.equals(node.rightChild) : node.rightChild != null) return false;
            return data != null ? data.equals(node.data) : node.data == null;
        }

        @Override
        public int hashCode() {
            int result = parent != null ? parent.hashCode() : 0;
            result = 31 * result + (leftChild != null ? leftChild.hashCode() : 0);
            result = 31 * result + (rightChild != null ? rightChild.hashCode() : 0);
            result = 31 * result + (data != null ? data.hashCode() : 0);
            result = 31 * result + cnt;
            return result;
        }

        @Override
        public String toString() {
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
