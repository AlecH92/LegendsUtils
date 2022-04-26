package io.elour;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TocHeader { //32 bytes
	String token; //1 2 3 4
	String version; //5 6 7 8
	short tocCompressor; // 9
	short fileNameCompressor; // 10
	short unusedone; // 11
	short unusedtwo; // 12
	long numberOfFiles; //13 14 15 16
	long sizeOfTOC; //17 18 19 20
	long sizeOfNameBlock; //21 22 23 24
	long uncompSizeOfNameBlock; //25 26 27 28
	long numberOfTreFiles; //29 30 31 32
	long sizeOfTreFileNameBlock; //33 34 35 36
	
	TocHeader() {
		this.token = "";
		this.version = "1000";
		this.tocCompressor = 0;
		this.fileNameCompressor = 0;
		this.unusedone = 0;
		this.unusedtwo = 0;
	}
	
	TocHeader(String a, String b, long c, long d, long e, long f, long g, long h) {
		this.token = a;
		this.version = b;
		this.tocCompressor = 0;
		this.fileNameCompressor = 0;
		this.unusedone = 0;
		this.unusedtwo = 0;
		this.numberOfFiles = c;
		this.sizeOfTOC = d;
		this.sizeOfNameBlock = e;
		this.uncompSizeOfNameBlock = f;
		this.numberOfTreFiles = g;
		this.sizeOfTreFileNameBlock = h;
	}

	void setSizeOfTOC(long sizeOfTOC) { //are these really necessary? can't we just "identifier.variable = #" ?
		this.sizeOfTOC = sizeOfTOC;
	}
	void setNumberOfFiles(long numberOfFiles) {
		this.numberOfFiles = numberOfFiles;
	}
	void setSizeOfNameBlock(long numberOfFiles) {
		this.sizeOfNameBlock = numberOfFiles;
		this.uncompSizeOfNameBlock = numberOfFiles;
	}
	void setNumberOfTreFiles(long numberOfFiles) {
		this.numberOfTreFiles = numberOfFiles;
	}
	void setSizeOfTreFileNameBlock(long numberOfFiles) {
		this.sizeOfTreFileNameBlock = numberOfFiles;
	}
	
	void print() {
		System.out.println("TREE Info: Type "+token+", Version " + version + ", Number of Files: " + numberOfFiles + ", TOC Offset: " + 0 
				+ ", TOC Comprsesed? " + tocCompressor + ", Size of TOC: " + sizeOfTOC + ", Block Compressor? " + 0 + ", Size of Name Block: " + sizeOfNameBlock
				+ ", Uncompressed Size of Name Block: " + uncompSizeOfNameBlock + "\n");
	}
	byte[] toByteArray() {
	    ByteBuffer buffer = ByteBuffer.allocate(32);
	    buffer.put((byte) tocCompressor); //1
	    buffer.put((byte) fileNameCompressor); //2
	    buffer.put((byte) unusedone); //3
	    buffer.put((byte) unusedtwo); //4
	    buffer.order(ByteOrder.LITTLE_ENDIAN);
	    buffer.putLong(numberOfFiles); //5 6 7 8
	    buffer.position(8); //longs are '8' bytes in java, ignore the last 4 bytes and rewind
	    buffer.putLong(sizeOfTOC); // 9 10 11 12
	    buffer.position(12);
	    buffer.putLong(sizeOfNameBlock); // 13 14 15 16
	    buffer.position(16);
	    buffer.putLong(uncompSizeOfNameBlock); //17 18 19 20
	    buffer.position(20);
	    buffer.putLong(numberOfTreFiles);
	    buffer.position(24);
	    buffer.putLong(sizeOfTreFileNameBlock);
	    buffer.position(28);
	    buffer.limit(28);
	    return buffer.array();
	}
	ByteBuffer getByteBuffer() {
	    ByteBuffer buffer = ByteBuffer.allocate(32);
	    buffer.put((byte) tocCompressor); //1
	    buffer.put((byte) fileNameCompressor); //2
	    buffer.put((byte) unusedone); //3
	    buffer.put((byte) unusedtwo); //4
	    buffer.putLong(numberOfFiles); //5 6 7 8
	    buffer.position(8); //longs are '8' bytes in java, ignore the last 4 bytes and rewind
	    buffer.putLong(sizeOfTOC); // 9 10 11 12
	    buffer.position(12);
	    buffer.putLong(sizeOfNameBlock); // 13 14 15 16
	    buffer.position(16);
	    buffer.putLong(uncompSizeOfNameBlock); //17 18 19 20
	    buffer.position(20);
	    buffer.putLong(numberOfTreFiles);
	    buffer.position(24);
	    buffer.putLong(sizeOfTreFileNameBlock);
	    buffer.position(28);
	    buffer.limit(28);
	    buffer = ByteBuffer.wrap(buffer.array(), 0, 28);
	    return buffer;
	}
	CharSequence toCharSequence() {
	    ByteBuffer buffer = ByteBuffer.allocate(32);
	    buffer.put((byte) tocCompressor); //1
	    buffer.put((byte) fileNameCompressor); //2
	    buffer.put((byte) unusedone); //3
	    buffer.put((byte) unusedtwo); //4
	    buffer.putLong(numberOfFiles); //5 6 7 8
	    buffer.position(8); //longs are '8' bytes in java, ignore the last 4 bytes and rewind
	    buffer.putLong(sizeOfTOC); // 9 10 11 12
	    buffer.position(12);
	    buffer.putLong(sizeOfNameBlock); // 13 14 15 16
	    buffer.position(16);
	    buffer.putLong(uncompSizeOfNameBlock); //17 18 19 20
	    buffer.position(20);
	    buffer.putLong(numberOfTreFiles);
	    buffer.position(24);
	    buffer.putLong(sizeOfTreFileNameBlock);
	    buffer.position(28);
	    buffer.limit(28);
		CharSequence x = null;
		try {
			x = new String(buffer.array(), "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return x;
	}
}
