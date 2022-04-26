package io.elour;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TreeTOCEntry implements Comparable<TreeTOCEntry> { //24 BYTES
    long crc; // 1 2 3 4
    long length; // 5 6 7 8
    long offset; // 9 10 11 12
    long compressor; // 13 14 15 16
    long compressedLength; // 17 18 19 20
    long fileNameOffset; // 21 22 23 24
    long originalFileNameOffset;
    String name;

    TreeTOCEntry(long crc, long length, long offset, long compressor, long compressedLength, long fileNameOffset) {
        this.compressor = compressor;
        this.crc = crc;
        this.fileNameOffset = fileNameOffset;
        this.offset = offset;
        this.length = length;
        this.compressedLength = compressedLength;
        this.name = "";
    }

    void print() {
        System.out.println("compressor: " + compressor  +  ", crc: " + crc + ", file name length: " + fileNameOffset + ", file name offset: " + originalFileNameOffset + ", offset: " + offset + ", length: " + length + ", compressed length: " + compressedLength + ", File name: " + name);
    }

    void setName(String name) {
        this.name = name;
        originalFileNameOffset = fileNameOffset;
        this.fileNameOffset = name.length();
    }
    byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(30);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(this.crc); // 1 2 3 4
        buffer.position(4);
        buffer.putLong(this.length); // 5 6 7 8
        buffer.position(8);
        buffer.putLong(this.offset); //9 10 11 12
        buffer.position(12);
        buffer.putLong(this.compressor); // 13 14 15 16
        buffer.position(16);
        buffer.putLong(this.compressedLength); // 17 18 19 20
        buffer.position(20);
        buffer.putLong(this.originalFileNameOffset); //21 22 23 24
        buffer.limit(24);
        return buffer.array();
    }

    @Override
    public int compareTo(TreeTOCEntry arg0) {
        return Long.compare(this.crc, arg0.crc);
    }
}