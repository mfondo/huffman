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
    private static void populateEncodedBits(Node node, Bits bits, Map<Byte, Bits> map) {
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
                map.put(node.data, new Bits(bits));
            }
        }
    }

    private static Node buildTree(byte[] buffer, int cnt) {
        Map<Byte, Integer> valueCnts = getValueCounts(buffer, cnt);
        List<Node> initialWeights = new ArrayList<>(valueCnts.size());//todo linked list would be more efficient for adds/removes?
        for(Map.Entry<Byte, Integer> entry : valueCnts.entrySet()) {
            Node node = new Node();
            node.data = entry.getKey();
            node.cnt = entry.getValue();
            initialWeights.add(node);
        }
        //sort descending by count
        Collections.sort(initialWeights, NODE_CNT_COMPARATOR);
        List<Node> combinedWeights = new ArrayList<Node>();//todo linked list would be more efficient for adds/removes?
        Node smallestCntNode1;
        Node smallestCntNode2;
        Node smallestCntNode3;
        Node smallestCntNode4;
        Node smallestCntNode5;
        Node smallestCntNode6;
        Node tmpNode;
        Node[] smallestNodes = new Node[4];
        while(true) {
            smallestCntNode1 = null;
            smallestCntNode2 = null;
            smallestCntNode3 = null;
            smallestCntNode4 = null;
            for(int i = 0; i < smallestNodes.length; i++) {
                smallestNodes[i] = null;
            }
            if(initialWeights.size() > 1 && combinedWeights.size() > 1) {
                if(!initialWeights.isEmpty()) {
                    smallestCntNode1 = initialWeights.get(0);
                    smallestNodes[0] = smallestCntNode1;
                    if(initialWeights.size() > 1) {
                        smallestCntNode2 = initialWeights.get(1);
                        smallestNodes[1] = smallestCntNode2;
                    }
                }
                if(!combinedWeights.isEmpty()) {
                    smallestCntNode3 = combinedWeights.get(0);
                    smallestNodes[2] = smallestCntNode3;
                    if(combinedWeights.size() > 1) {
                        smallestCntNode4 = combinedWeights.get(1);
                        smallestNodes[3] = smallestCntNode4;
                    }
                }
                Arrays.sort(smallestNodes, NODE_CNT_COMPARATOR);
                tmpNode = smallestNodes[0];
                if(tmpNode == smallestCntNode1) {
                    initialWeights.remove(0);
                } else if(tmpNode == smallestCntNode3) {
                    combinedWeights.remove(0);
                }
                smallestCntNode5 = tmpNode;
                tmpNode = smallestNodes[1];
                if(tmpNode == smallestCntNode1) {
                    initialWeights.remove(0);
                } else if(tmpNode == smallestCntNode2) {
                    combinedWeights.remove(0);
                } else if(tmpNode == smallestCntNode3) {
                    combinedWeights.remove(0);
                } else if(tmpNode == smallestCntNode4) {
                    combinedWeights.remove(0);
                }
                smallestCntNode6 = tmpNode;
                Node combinedNode = new Node();
                combinedNode.cnt = smallestCntNode5.cnt + smallestCntNode6.cnt;
                combinedNode.leftChild = smallestCntNode5;
                combinedNode.rightChild = smallestCntNode6;
                smallestCntNode5.parent = combinedNode;
                smallestCntNode6.parent = combinedNode;
                combinedWeights.add(combinedWeights.size(), combinedNode);
            } else {
                break;
            }
        }
        return initialWeights.isEmpty() ? initialWeights.get(0) : combinedWeights.get(0);
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

    private static class Node {

        private Node parent;
        private Node leftChild;
        private Node rightChild;
        private byte data;
        private int cnt;

    }
}
