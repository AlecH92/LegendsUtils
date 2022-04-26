package io.elour;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TableOfContentsEntry implements Comparable<TableOfContentsEntry>{ //24 BYTES
	int compressor; // 1 char
	int unused; // 2 char
	short treeFileIndex; // 3 4
	long crc; // 5 6 7 8
	long fileNameOffset; // 9 10 11 12
	long offset; // 13 14 15 16
	long length; // 17 18 19 20
	long compressedLength; // 21 22 23 24
	String fileName = "";
	boolean skipThis = false;
	
	TableOfContentsEntry(int compressor, int unused, short treeFileIndex, long crc, long fileNameOffset, long offset, long length, long compressedLength) {
		this.compressor = compressor;
		this.unused = unused;
		this.treeFileIndex = treeFileIndex;
		this.crc = crc;
		this.fileNameOffset = fileNameOffset;
		this.offset = offset;
		this.length = length;
		this.compressedLength = compressedLength;
	}

	TableOfContentsEntry(TreeTOCEntry a, int treeFileIndex) { //create a TableOfContentsEntry from a TreeTOCEntry
		this.compressor = (int) a.compressor;
		this.unused = 0;
		this.treeFileIndex = (short) treeFileIndex;
		this.crc = a.crc;
		this.fileNameOffset = a.name.length();
		this.offset = a.offset;
		this.length = a.length;
		this.compressedLength = a.compressedLength;
		if(length != 0 && compressedLength == 0) {
			compressedLength = length;
		}
		this.fileName = a.name;
		this.skipThis = false;
	}
	
	void print() {
		System.out.println("compressor: " + compressor + ", unused: " + unused + ", tree file index: " + treeFileIndex + ", crc: " + crc + ", file name length: " + fileNameOffset + ", offset: " + offset + ", length: " + length + ", compressed length: " + compressedLength + ", file name " + fileName + ", skip? " + skipThis);
	}
	String tsvinfo() {
		return "" + compressor + "\t" + treeFileIndex + "\t" + crc + "\t" + fileNameOffset + "\t" + offset + "\t" + length + "\t" + compressedLength + "\n";
	}
	void writeData(Writer outputHere) {
		try {
			outputHere.write(""+compressor + unused + treeFileIndex + crc + fileNameOffset + offset + length + compressedLength);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	byte[] toByteArray() {
	    ByteBuffer buffer = ByteBuffer.allocate(28);
	    buffer.order(ByteOrder.LITTLE_ENDIAN); //Important! silly SOE
	    buffer.put((byte) compressor); //1 
	    buffer.put((byte) unused); //2
	    buffer.position(2);
	    buffer.putShort(treeFileIndex); //3 4
	    buffer.putLong(crc); //5 6 7 8
	    buffer.position(8); //longs are '8' bytes in java, ignore the last 4 bytes and rewind
	    buffer.putLong(fileNameOffset); //9 10 11 12 
	    buffer.position(12);
	    buffer.putLong(offset); //9 10 11 12 
	    buffer.position(16);
	    buffer.putLong(length); //13 14 15 16 
	    buffer.position(20);
	    buffer.putLong(compressedLength); //17 18 19 20
	    //buffer = ByteBuffer.wrap(buffer.array(), 0, 24); //this doesn't do anything? so...
	    return buffer.array();
	}

	@Override
	public int compareTo(TableOfContentsEntry arg0) { //implementing this, the sort is not automatic? be sure to call Arrays.sort()...
		if(this.crc > arg0.crc) {
			return 1;
		}
		else if(this.crc < arg0.crc) {
			return -1;
		}
		else {
			return 0;
		}
	}
}

