package io.elour;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TreeHeader { // 36 bytes
    String token; //1 2 3 4
    String version; //5 6 7 8
    long numberOfFiles; //9 10 11 12
    long tocOffset; //13 14 15 16
    long tocCompressor; //17 18 19 20
    long sizeOfTOC; //21 22 23 24
    long blockCompressor; //25 26 27 28
    long sizeOfNameBlock; //29 30 31 32
    long uncompSizeOfNameBlock; //33 34 35 36

    //Token, Version, Num Files, TOC Offset, TOC Comp, TOC Size, Block Comp, Block Size, Uncomp Block Size
    TreeHeader(String a, String b, long c, long d, long e, long f, long g, long h, long i) {
        this.token = a;
        this.version = b;
        this.numberOfFiles = c;
        this.tocOffset = d;
        this.tocCompressor = e;
        this.sizeOfTOC = f;
        this.blockCompressor = g;
        this.sizeOfNameBlock = h;
        this.uncompSizeOfNameBlock = i;
    }

    void print() {
        System.out.println("TREE Info: Type "+token+", Version " + version + ", Number of Files: " + numberOfFiles + ", TOC Offset: " + tocOffset
                + ", TOC Comprsesed? " + tocCompressor + ", Size of TOC: " + sizeOfTOC + ", Block Compressor? " + blockCompressor + ", Size of Name Block: " + sizeOfNameBlock
                + ", Uncompressed Size of Name Block: " + uncompSizeOfNameBlock + "\n");
    }
    byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(44);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(this.token.getBytes()); // 1 2 3 4
        buffer.position(4);
        buffer.put(this.version.getBytes()); // 5 6 7 8
        buffer.position(8);
        buffer.putLong(numberOfFiles); //9 10 11 12
        buffer.position(12);
        buffer.putLong(tocOffset); // 13 14 15 16
        buffer.position(16);
        buffer.putLong(tocCompressor); // 17 18 19 20
        buffer.position(20);
        buffer.putLong(sizeOfTOC); //21 22 23 24
        buffer.position(24);
        buffer.putLong(blockCompressor);// 25 26 27 28
        buffer.position(28);
        buffer.putLong(sizeOfNameBlock); //29 30 31 32
        buffer.position(32);
        buffer.putLong(uncompSizeOfNameBlock); //33 34 35 36
        buffer.position(36);
        buffer.limit(36);
        return buffer.array();
    }
}