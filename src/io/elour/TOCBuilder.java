package io.elour;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static io.elour.Utils.*;

public class TOCBuilder {

    public static void main(String[] args) {
        String outFilename = args[0];
        FileOutputStream outStream = null;
        Writer writer = null;
        try {
            TocHeader tocHeader = new TocHeader();
            Map<String, Integer> allFiles = new LinkedHashMap<String, Integer>();
            int numberOfFilesTotal = 0;
            int sizeOfTreFileNames = 0;
            int sizeOfNameBlock = 0;
            int numberOfTreFiles = 0;
            int numberOfTOCTreFiles = 0;
            String[] treFiles = new String[1000];
            for (int z = 1; z < args.length; z++) {
                FileInputStream inputStream = new FileInputStream(args[z]);
                String formquestionmark = readString(inputStream, 4); // decide here - TREE or TOC
                if (formquestionmark.equalsIgnoreCase("eert")) { //TREE
                    sizeOfTreFileNames += (Paths.get(args[z]).getFileName().toString().length() + 1);
                    treFiles[numberOfTreFiles] = Paths.get(args[z]).getFileName().toString();
                    numberOfTreFiles++;
                    String TemplateType = readString(inputStream, 4); // 1000 (4)
                    if(!(TemplateType.equalsIgnoreCase("5000") || TemplateType.equalsIgnoreCase("4000"))) { //we can read 4 or 5 TREs
                        System.out.println("Not a valid TREE File " + (Paths.get(args[z]).getFileName().toString()));
                        System.exit(124);
                    }
                    long number_of_files = readUint32(inputStream); // LENGTH (4)
                    long size_of_toc = readUint32(inputStream); // LENGTH (4)
                    long size_of_name_block = readUint32(inputStream); // LENGTH (4)
                    long size_of_name_block_uncon = readUint32(inputStream); // LENGTH (4)
                    long number_of_tre_files = readUint32(inputStream); // LENGTH (4)
                    long size_of_tre_block = readUint32(inputStream); // LENGTH (4)
                    long size_of_tre_blockt = readUint32(inputStream); // LENGTH (4)
                    System.out.println("TREE files + " + number_of_files);
                    TreeHeader header = new TreeHeader(formquestionmark, TemplateType, number_of_files, size_of_toc,
                            size_of_name_block, size_of_name_block_uncon, number_of_tre_files, size_of_tre_block,
                            size_of_tre_blockt);
                    numberOfFilesTotal += (int) header.numberOfFiles;
                    header.print();
                    //System.exit(1);
                    sizeOfNameBlock += size_of_name_block;
                }
                else if(formquestionmark.equalsIgnoreCase(" cot")) { //TOC
                    String TemplateType = readString(inputStream, 4); //1000 (4)
                    if(!TemplateType.equalsIgnoreCase("1000")) {
                        System.out.println("Not a valid TOC File");
                        System.exit(124);
                    }
                    skip(4, inputStream); //skip compressor & two unused (4)
                    long number_of_files = readUint32(inputStream); // LENGTH (4)
                    long size_of_toc = readUint32(inputStream); // LENGTH (4)
                    long size_of_name_block = readUint32(inputStream); // LENGTH (4)
                    long size_of_name_block_uncon = readUint32(inputStream); // LENGTH (4)
                    long number_of_tre_files = readUint32(inputStream); // LENGTH (4)
                    long size_of_tre_block = readUint32(inputStream); // LENGTH (4)
                    numberOfTOCTreFiles += number_of_tre_files;
                    System.out.println("TOC files + " + number_of_files);

                    for(int i = 0; i < number_of_tre_files; i++) { //print out our tre file names
                        String treName = readUntil((byte)0,inputStream);
                        treFiles[numberOfTreFiles] = treName;
                        numberOfTreFiles++;
                    }
                    numberOfFilesTotal += (int) number_of_files;
                    sizeOfNameBlock += size_of_name_block_uncon;
                    sizeOfTreFileNames += size_of_tre_block;

                }
                else {
                    System.out.println("Not a valid TRE or TOC file " + (Paths.get(args[z]).getFileName().toString()));
                }
                inputStream.close();
            }
            tocHeader.setSizeOfTreFileNameBlock(sizeOfTreFileNames); // toc header - size of tre file name block
            tocHeader.setNumberOfFiles(numberOfFilesTotal); // toc header - number of files
            tocHeader.setSizeOfNameBlock(sizeOfNameBlock); // toc header - size of file name block
            tocHeader.setSizeOfTOC(numberOfFilesTotal * 24);
            tocHeader.setNumberOfTreFiles(numberOfTreFiles); // toc header - number of tre files
            System.out.println("Total number of files " + numberOfFilesTotal);
            outStream = new FileOutputStream(outFilename);
            writer = new BufferedWriter(new OutputStreamWriter(outStream));
            TableOfContentsEntry[] tocEntries = new TableOfContentsEntry[numberOfFilesTotal];
            int tocEntriesListNum = 0;
            int TOCNamesSoFar = 0;
            for (int z = 0; z < args.length; z++) {
                FileInputStream inputStream = new FileInputStream(args[z]);
                String formquestionmark = readString(inputStream, 4); // " " C O T (4) TOC_
                if (formquestionmark.equalsIgnoreCase("eert")) { //TREE
                    String TemplateType = readString(inputStream, 4); // 1000 (4)
                    long number_of_files = readUint32(inputStream); // LENGTH (4)
                    long size_of_toc = readUint32(inputStream); // LENGTH (4)
                    long size_of_name_block = readUint32(inputStream); // LENGTH (4)
                    long size_of_name_block_uncon = readUint32(inputStream); // LENGTH (4)
                    long number_of_tre_files = readUint32(inputStream); // LENGTH (4)
                    long size_of_tre_block = readUint32(inputStream); // LENGTH (4)
                    long size_of_tre_blockt = readUint32(inputStream); // LENGTH (4)
                    TreeHeader header = new TreeHeader(formquestionmark, TemplateType, number_of_files, size_of_toc,
                            size_of_name_block, size_of_name_block_uncon, number_of_tre_files, size_of_tre_block,
                            size_of_tre_blockt);
                    header.print();
                    if (TemplateType.equalsIgnoreCase("6000")) { // invalid header?
                        System.out.println("Skipping input file " + args[z] + " because it does not have a TOC"); //so v6000 TOC files do not have a header - we can NOT read them. alert but skip.
                    } else if (TemplateType.equalsIgnoreCase("5000") || TemplateType.equalsIgnoreCase("4000")) { // 4 and 5 use same processing, valid headers
                        //numberOfTreFiles++;
                        TreeTOCEntry[] treetoc = new TreeTOCEntry[(int) header.numberOfFiles];
                        inputStream.getChannel().position(header.tocOffset);
                        if (header.tocCompressor == 2) { //compressed table of contents, unzip and process
                            byte[] tocData = new byte[(int) header.sizeOfTOC];
                            inputStream.read(tocData, 0, (int) header.sizeOfTOC);
                            Inflater decompresser = new Inflater();
                            decompresser.setInput(tocData, 0, (int) header.sizeOfTOC);
                            byte[] result = new byte[24 * (int) header.numberOfFiles];
                            decompresser.inflate(result);
                            decompresser.end();
                            int loc = 0;
                            for (int i = 0; i < header.numberOfFiles; i++) {
                                long crc = readUint32(result, loc); // LENGTH (4)
                                loc += 4;
                                long length = readUint32(result, loc);
                                loc += 4;
                                long offset = readUint32(result, loc);
                                loc += 4;
                                long compressor = readUint32(result, loc);
                                loc += 4;
                                long compressedLength = readUint32(result, loc);
                                loc += 4;
                                long fileNameOffset = readUint32(result, loc);
                                loc += 4;
                                treetoc[i] = new TreeTOCEntry(crc, length, offset, compressor, compressedLength, fileNameOffset);
                                //treetoc[i].print();
                            }
                        } else { //uncompressed table of contents, just read
                            for (int i = 0; i < header.numberOfFiles; i++) {
                                long crc = readUint32(inputStream); // LENGTH (4)
                                long length = readUint32(inputStream);
                                long offset = readUint32(inputStream);
                                long compressor = readUint32(inputStream);
                                long compressedLength = readUint32(inputStream);
                                long fileNameOffset = readUint32(inputStream);
                                treetoc[i] = new TreeTOCEntry(crc, length, offset, compressor, compressedLength, fileNameOffset);
                            }
                        }
                        if (header.blockCompressor == 2) { //compressed name block, unzip and process
                            byte[] blockData = new byte[(int) header.sizeOfNameBlock];
                            inputStream.read(blockData, 0, (int) header.sizeOfNameBlock);
                            Inflater decompresser = new Inflater();
                            decompresser.setInput(blockData, 0, (int) header.sizeOfNameBlock);
                            byte[] result = new byte[(int) header.uncompSizeOfNameBlock];
                            decompresser.inflate(result);
                            //System.out.println("ADLER" + decompresser.getAdler());
                            decompresser.end();
                            int loc = 0;
                            for (int i = 0; i < header.numberOfFiles; i++) {
                                String trename = readUntil((byte) 0, result, loc);
                                treetoc[i].setName(trename);
                                treetoc[i].print();
                                System.out.println("LOCATION IS " + loc);
                                loc += trename.length() + 1;
                            }
                        } else { //uncompressed name block, just read
                            for (int i = 0; i < header.numberOfFiles; i++) {
                                String trename = readUntil((byte) 0, inputStream);
                                treetoc[i].setName(trename);
                            }
                        }
                        for (int j = 0; j < treetoc.length; j++) { //compile our separate tree's table of contents into one main table of contents for the TOC
                            tocEntries[tocEntriesListNum] = new TableOfContentsEntry(treetoc[j], numberOfTOCTreFiles);
                            tocEntriesListNum++;
                        }
                        numberOfTOCTreFiles++; //increase tre size number after we set what tre they are from
                    } else {
                        System.out.println("Tree File Error");
                        System.exit(124);
                    }
                }
                else if(formquestionmark.equalsIgnoreCase(" cot")) { //TOC
                    String TemplateType = readString(inputStream,4); //1000 (4)
                    if(!TemplateType.equalsIgnoreCase("1000")) {
                        System.out.println("Not a valid TOC File"); //TOC only exists as 1000. bail if not.
                        System.exit(124);
                    }
                    skip(4, inputStream); //skip compressor & two unused (4)
                    long number_of_files = readUint32(inputStream); // LENGTH (4)
                    System.out.println("Reading a TOC...");
                    System.out.println("Number of Files: " + number_of_files);
                    long size_of_toc = readUint32(inputStream); // LENGTH (4)
                    System.out.println("Size of TOC: " + size_of_toc);
                    long size_of_name_block = readUint32(inputStream); // LENGTH (4)
                    System.out.println("Size of File Names: " + size_of_name_block);
                    long size_of_name_block_uncon = readUint32(inputStream); // LENGTH (4)
                    System.out.println("Size of File Names (Uncompressed): " + size_of_name_block_uncon);
                    long number_of_tre_files = readUint32(inputStream); // LENGTH (4)
                    System.out.println("Number of TRE Files: " + number_of_tre_files);
                    long size_of_tre_block = readUint32(inputStream); // LENGTH (4)
                    System.out.println("Size of TRE Files List: " + size_of_tre_block);
                    System.out.println("Currently at position " + inputStream.getChannel().position());
                    System.out.println("END Reading a TOC...");

                    TableOfContentsEntry[] tocEntriesFromATOC = new TableOfContentsEntry[(int) number_of_files];

                    for(int i = 0; i < number_of_tre_files; i++) { //print out our tre file names
                        readUntil((byte)0,inputStream); //skipping over these? print them later - before Z
                    }

                    for(int i = 0; i <number_of_files; i++) {
                        tocEntriesFromATOC[i] = new TableOfContentsEntry(inputStream.read(), inputStream.read(), (short) (readShort(inputStream) + TOCNamesSoFar),readUint32(inputStream),readUint32(inputStream),readUint32(inputStream),readUint32(inputStream),readUint32(inputStream));
                    }

                    for(int i = 0; i < number_of_files; i++) {
                        String the_filename = readUntil((byte) 0, inputStream);
                        tocEntriesFromATOC[i].fileName = the_filename;
                    }

                    for (int j = 0; j < tocEntriesFromATOC.length; j++) { //compile our separate tree's table of contents into one main table of contents for the TOC
                        tocEntries[tocEntriesListNum] = tocEntriesFromATOC[j];
                        tocEntriesListNum++;
                    }
                    TOCNamesSoFar += number_of_tre_files; //we track for consecutive TOCs how many TREs we expect - we need to offset the entry in their next index.
                }

                inputStream.close();
            }

            Arrays.sort(tocEntries); //sort our full TOC table of contents
            //tocEntries[8797].skipThis = true; //Use this to skip certain entries! Enable the printing below, find the file specified and then set skipThis to true.
			/*for(int i = 0; i < tocEntries.length; i++) {
				System.out.print("TOC " + i);
				tocEntries[i].print();
			}*/
            //tocEntries[0].print(); //DEBUG //these are for making sure we're not, uh, insane.
            //tocEntries[1].print();
            //tocEntries[2].print();

            for (int i = 0; i < tocEntries.length; i++) {
                if (allFiles.containsKey(tocEntries[i].fileName)) { //allFiles only tracks names, we process via tocEntries because it's sorted by CRC which is !!! 100% !!! required to work with SWG
                    System.out.println("duplicated is " + allFiles.get(tocEntries[i].fileName));
                    System.out.println(allFiles.size());
                    System.out.println("duplicate file detected\t" + tocEntries[i].fileName + "\tfrom tre file "
                            + treFiles[tocEntries[allFiles.get(tocEntries[i].fileName)].treeFileIndex] + "\talso in "
                            + treFiles[tocEntries[i].treeFileIndex]);
                    tocEntries[allFiles.get(tocEntries[i].fileName)].skipThis = true; //keep track of which ones we skip for later on
                    tocEntries[allFiles.get(tocEntries[i].fileName)].print();
                    tocEntries[i].print();
                    allFiles.remove(tocEntries[allFiles.get(tocEntries[i].fileName)]); // key associated with value?
                    allFiles.put(tocEntries[i].fileName, i); // add to our named list
                } else {
                    allFiles.put(tocEntries[i].fileName, i); // add to our named list
                }
            }
            writer.write(" COT1000"); // write start of header
            writer.flush();
            int totalNameSize = 0;
            int numberOfFiles = 0;
            for (int i = 0; i < tocEntries.length; i++) {
                if (!tocEntries[i].skipThis) { //if we didn't say to skip this one, write it. skipped = duplicated / exists in older tre
                    totalNameSize += tocEntries[i].fileNameOffset +1;
                    numberOfFiles++;
                }
            }
			/*for (String key : allFiles.keySet()) { //get total name block size, for TOC header
				totalNameSize += key.length() + 1;
			}*/
            //tocHeader.setNumberOfFiles(allFiles.size()); //number of files = allFiles.size() (which does not include duplicates)
            int sizeOfTreFilesR = 0;
            for (int z = 0; z < numberOfTreFiles; z++) {
                sizeOfTreFilesR += treFiles[z].length() + 1;
            }
            tocHeader.setSizeOfTreFileNameBlock(sizeOfTreFilesR); // toc header - size of tre file name block
            tocHeader.setNumberOfFiles(numberOfFiles); //number of files
            tocHeader.setSizeOfNameBlock(totalNameSize); //from above
            tocHeader.setSizeOfTOC(numberOfFiles * 24); //table of contents = number of files * 24 (each TOC data for a file is 24 bytes)
            System.out.println("Size of TOC: " + tocHeader.sizeOfTOC);
            System.out.println("Size of Name Block: " + tocHeader.sizeOfNameBlock);
            System.out.println("name size " + allFiles.values().toString().length());
            outStream.write(tocHeader.toByteArray(), 0, 28); // write rest of header //Due to Java and 'long' being 8 bytes - the array is 4 bytes longer than required. only write what is required.
            for (int z = 0; z < numberOfTreFiles; z++) {
                writer.write(treFiles[z]); // write our TRE file names //Important! Paths removes C://blah//blah//blah
                writer.write(0); //Important! Strings are null'd at the end
            }
            writer.flush(); //flush because we use outStream.write - if not flushed, you may end up with jumbled data.
            long initialPos = outStream.getChannel().position(); //sanity checking, endPos - initialPos = TOC size
            // Set<String> keys = allFiles.keySet();
            for (int i = 0; i < tocEntries.length; i++) {
                if (!tocEntries[i].skipThis) { //if we didn't say to skip this one, write it. skipped = duplicated / exists in older tre
                    outStream.write(tocEntries[i].toByteArray(), 0, 24);
                }
            }
            long endPos = outStream.getChannel().position();
            System.out.println("Start and end of TOC: " + initialPos + ", " + endPos + ", x-y= " + (endPos - initialPos));
            initialPos = outStream.getChannel().position();
            System.out.println("writing filename " + tocEntries[0].fileName);

            //tocEntries[0].print();
            //tocEntries[1].print();
            //tocEntries[2].print();

            for (int i = 0; i < tocEntries.length; i++) {
                if (!tocEntries[i].skipThis) {
                    outStream.write(tocEntries[i].fileName.getBytes()); //writing file names and the null
                    outStream.write((byte) 0);
                }
            }
            endPos = outStream.getChannel().position();
            System.out.println("Start and end of Name Block: " + initialPos + ", " + endPos + ", x-y= " + (endPos - initialPos));
            System.out.println("new number of files " + allFiles.size());

            for (int z = 0; z < numberOfTreFiles; z++) {
                System.out.println("TRE " + z + " is " + treFiles[z]); //for debug!
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file");
        } catch (IOException ex) {
            System.out.println("Error reading file");
        } catch (DataFormatException e) { // from inflater
            e.printStackTrace();
        }
        finally {
            try {
                writer.close();
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
