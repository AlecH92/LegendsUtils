package io.elour;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Utils {
    /**
     * Skips ahead in an input stream
     * @param skipNum Number of bytes to skip
     * @param toSkip FileInputStream to perform reads on
     */
    static void skip(int skipNum, InputStream toSkip)
    {
        try
        {
            long skipped = toSkip.skip(skipNum);
            if(skipped != skipNum)
            {
                System.err.println("Should have skipped " + skipNum + " but we skipped " + skipped);
                skip((int) (skipNum - skipped), toSkip);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    static void skipReal(int skipNum, InputStream toSkip)
    {
        try
        {
            byte[] b = new byte[skipNum];
            toSkip.read(b, 0, skipNum);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Returns the length of a FORM or CHUNK (4 bytes)
     * @param toSkip FileInputStream to perform reads on
     */
    static int getLength(InputStream toSkip) {
        try {
            return (toSkip.read() << 24 | (toSkip.read() << 16) | (toSkip.read() << 8) | (toSkip.read()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Reads a short (2 bytes)
     * @param toSkip FileInputStream to perform reads on
     */
    static int readShort(InputStream toSkip) {
        try {
            return ((toSkip.read()) | (toSkip.read() << 8));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Reads a float (4 bytes)
     * @param toSkip FileInputStream to perform reads on
     */
    static float floatFetcher(InputStream toSkip) {
        try {
            byte[] b = new byte[4];
            toSkip.read(b, 0, 4);
            return ByteBuffer.wrap(Objects.requireNonNull(b)).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1f;
    }

    /**
     * Reads an unsigned integer (4 bytes)
     * @param toSkip FileInputStream to perform reads on
     */
    static long readBigEndian4(InputStream toSkip) {
        try {
            byte[] b = new byte[4];
            toSkip.read(b, 0, 4);
            //System.err.println("Big Endian byte array: " + b[0] +" "+ b[1] +" "+ b[2] +" "+ b[3]);
            ByteBuffer bb = ByteBuffer.wrap(b);
            //bb.order(ByteOrder.LITTLE_ENDIAN);
            return bb.getInt();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Reads an unsigned integer (4 bytes)
     * @param toSkip FileInputStream to perform reads on
     */
    static long readUint32(InputStream toSkip) {
        try {
            byte[] b = new byte[4];
            toSkip.read(b, 0, 4);
            return ((long) (b[3] & 0xFF) << 24) | (b[2] & 0xFF) << 16 | (b[1] & 0xFF) << 8 | b[0] & 0xFF;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Reads an unsigned integer from a byte array
     * @param input byte array containing integer
     * @param loc length of uint to read
     * @return the int
     */
    static long readUint32(byte[] input, int loc) {
        return ((long) (input[loc + 3] & 0xFF) << 24) | (long) ((input[loc + 2] & 0xFF) << 16)
                | (long) ((input[loc + 1] & 0xFF) << 8) | (long) (input[loc] & 0xFF);
    }
    static long readUint32(byte[] input)
    {
        return readUint32(input, 0);
    }

    /**
     * Reads a reverse unsigned integer (4 bytes)
     * @param toSkip FileInputStream to perform reads on
     */
    static long readRUint(InputStream toSkip) {
        try {
            return ((long) (toSkip.read()) & 0xff | (long) (toSkip.read() << 8) & 0xff00
                    | (long) (toSkip.read() << 16) & 0xff0000 | (long) (toSkip.read() << 24) & 0xff000000);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Reads data until a specified delimiter is seen
     * @param delim Delimiter to read until
     * @param toSkip FileInputStream to perform reads on
     */
    static String readUntil(byte delim, InputStream toSkip) {
        try {
            byte thisRead = (byte) toSkip.read();
            StringBuilder returnme = new StringBuilder();
            while (thisRead != delim) {
                returnme.append((char) thisRead);
                thisRead = (byte) toSkip.read();
            }
            return returnme.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "err";
    }

    /**
     * Reads data until a specified delimiter is seen
     * @param delim Delimiter to read until
     * @param input An input byte array to read from
     * @param loc Start point to read from in input
     */
    static String readUntil(byte delim, byte[] input, int loc) {
        StringBuilder returnme = new StringBuilder();
        while (input[loc] != delim) {
            returnme.append((char) input[loc]);
            loc++;
        }
        return returnme.toString();
    }

    /**
     * Reads a string from IFF files
     * @param toSkip FileInputStream to perform reads on
     * @param length Length of String to read
     */
    static String readString(InputStream toSkip, int length) {
        String baseObject = "";
        try {
            byte[] baseObjectAr = new byte[length]; // create a byte array of
            // length
            toSkip.read(baseObjectAr, 0, length);
            baseObject = new String(baseObjectAr, StandardCharsets.UTF_8); // turn the byte
            // array into a
            // string
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // read the bytes

        return baseObject;
    }

    /**
     * Reads a string from IFF files
     * @param toSkip FileInputStream to perform reads on
     */
    static String readString(InputStream toSkip) {
        return readUntil((byte)0, toSkip);
    }

    /**
     * Reads a unicode string from IFF files
     * @param toSkip FileInputStream to perform reads on
     * @param length Length of String to read
     */
    static String readWideString(InputStream toSkip, int length) {
        String baseObject = "";
        try {
            byte thisRead;
            StringBuilder returnme = new StringBuilder();
            for(int i = 0; i < length; i++)
            {
                thisRead = (byte) toSkip.read();
                returnme.append((char) thisRead);
                toSkip.read(); //throwaway
            }
            return returnme.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // read the bytes

        return baseObject;
    }

    /**
     * Checks for a corresponding string set (FORM, 0001 etc)
     * @param toCheckFor The answer we're looking for
     * @param inputStream FileInputStream to perform reads on
     * @param fatalOnFalse If true, exit program on failure to match
     */
    public static boolean checkFor(String toCheckFor, InputStream inputStream, boolean fatalOnFalse)
    {
        String compare = readString(inputStream, 4);
        return compare.equalsIgnoreCase(toCheckFor);
    }
    /**
     * Checks for a corresponding string set (FORM, 0001 etc)
     * Exits program if match fails
     * @param toCheckFor The answer we're looking for
     * @param inputStream FileInputStream to perform reads on
     */
    public static void checkFor(String toCheckFor, InputStream inputStream)
    {
        String compare = readString(inputStream, 4);
        if(!compare.equalsIgnoreCase(toCheckFor))
        {
            System.err.println("CheckFor failed, " + toCheckFor + " != " + compare);
            System.exit(123);
        }
    }

    /**
     * Reads data from an input stream and prints out the data in a hex-dump format
     * @param is The input stream
     * @throws IOException err0r
     */
    public static void hexDump(InputStream is) throws IOException {
        int i = 0;

        while (is.available() > 0) {
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder("   ");
            System.out.printf("%04X  ", i * 16);
            for (int j = 0; j < 16; j++) {
                if (is.available() > 0) {
                    int value = is.read();
                    sb1.append(String.format("%02X ", value));
                    if (!Character.isISOControl(value)) {
                        sb2.append((char)value);
                    }
                    else {
                        sb2.append(".");
                    }
                }
                else {
                    for (;j < 16;j++) {
                        sb1.append("   ");
                    }
                }
            }
            System.out.print(sb1);
            System.out.println(sb2);
            i++;
        }
        is.close();
    }

    /**
     * Looks up the specified template from a FORM name
     * @param idInput the FORM name input
     * @return a client or server tdf filename
     */
    static String templateLookup(String idInput) {
        if (idInput.equalsIgnoreCase("SBMK")) {
            return "battlefield_marker_object_template";
        }

        if (idInput.equalsIgnoreCase("SBOT")) {
            return "building_object_template";
        }

        if (idInput.equalsIgnoreCase("CCLT")) {
            return "cell_object_template";
        }

        if (idInput.equalsIgnoreCase("SCNC")) {
            return "construction_contract_object_template";
        }

        if (idInput.equalsIgnoreCase("SCOT")) {
            return "creature_object_template";
        }

        if (idInput.equalsIgnoreCase("SDSC")) {
            return "draft_schematic_object_template";
        }

        if (idInput.equalsIgnoreCase("SFOT")) {
            return "factory_object_template";
        }

        if (idInput.equalsIgnoreCase("SGRP")) {
            return "group_object_template";
        }

        if (idInput.equalsIgnoreCase("SGLD")) {
            return "guild_object_template";
        }

        if (idInput.equalsIgnoreCase("SIOT")) {
            return "installation_object_template";
        }

        if (idInput.equalsIgnoreCase("SITN")) {
            return "intangible_object_template";
        }

        if (idInput.equalsIgnoreCase("SJED")) {
            return "jedi_manager_object_template";
        }

        if (idInput.equalsIgnoreCase("SMSC")) {
            return "manufacture_schematic_object_template";
        }

        if (idInput.equalsIgnoreCase("SMSD")) {
            return "mission_data_object_template";
        }

        if (idInput.equalsIgnoreCase("SMLE")) {
            return "mission_list_entry_object_template";
        }

        if (idInput.equalsIgnoreCase("SMSO")) {
            return "mission_object_template";
        }

        if (idInput.equalsIgnoreCase("SHOT")) {
            return "object_template";
        }

        if (idInput.equalsIgnoreCase("SPLY")) {
            return "player_object_template";
        }

        if (idInput.equalsIgnoreCase("SPQO")) {
            return "player_quest_object_template";
        }

        if (idInput.equalsIgnoreCase("RCCT")) {
            return "resource_container_object_template";
        }

        if (idInput.equalsIgnoreCase("SSHP")) {
            return "ship_object_template";
        }

        if (idInput.equalsIgnoreCase("STAT")) {
            return "static_object_template";
        }

        if (idInput.equalsIgnoreCase("STOT")) {
            return "tangible_object_template";
        }

        if (idInput.equalsIgnoreCase("STOK")) {
            return "token_object_template";
        }

        if (idInput.equalsIgnoreCase("SUNI")) {
            return "universe_object_template";
        }

        if (idInput.equalsIgnoreCase("SVOT")) {
            return "vehicle_object_template";
        }

        if (idInput.equalsIgnoreCase("SWAY")) {
            return "waypoint_object_template";
        }

        if (idInput.equalsIgnoreCase("SWOT")) {
            return "weapon_object_template";
        }

        return "UNKNOWN";
    }

    /**
     * Returns a gender string based on input
     * @param input A gender type as integer
     * @return String of the gender input
     */
    static String genderLookup(int input) {
        if (input == 0)
            return "GE_male";
        if (input == 1)
            return "GE_female";
        if (input == 2)
            return "GE_other";
        return "UNKNOWN";
    }

    /**
     * Returns a species string based on input
     * @param input A species type as integer
     * @return String of the species input
     */
    static String speciesLookup(int input) {
        if (input == 12)
            return "SP_bith";
        return "UNKNOWN";
    }

    /**
     * Returns data from the text of a template definition
     * @param fileContent Text of the template definition file
     * @param toLook Type of data to look for
     * @param data unused? 0
     * @return Information of tdf from toLook
     */
    static String templateLookupDataFrom(String fileContent, String toLook, int data) {
        int startLoc = fileContent.lastIndexOf("version ");

        int whereToStart = fileContent.toLowerCase().indexOf(toLook.toLowerCase(), startLoc);
        whereToStart += toLook.length();
        whereToStart = fileContent.indexOf("{", whereToStart) + 1;
        List<String> list = new ArrayList<String>();
        String str = "";
        while ((str = fileContent.substring(whereToStart, fileContent.indexOf("\n", whereToStart))) != null) {
            if (str.trim().equalsIgnoreCase("")) {
                whereToStart = fileContent.indexOf("\n", whereToStart) + 1;
                continue;
            }
            if (str.trim().charAt(0) == '/') {
                whereToStart = fileContent.indexOf("\n", whereToStart) + 1;
                continue;
            }
            if (str.trim().equalsIgnoreCase("}")) {
                break;
            }
            list.add(str.trim());
            whereToStart = fileContent.indexOf("\n", whereToStart) + 1;
        }
        return "";
    }

    /**
     * Returns data from an in-repo file
     * @param rsc String path of the file to read from
     * @return Text data of the file input
     */
    static String getResource(String rsc) {
        StringBuilder val = new StringBuilder();

        try {
            Class cls = Class.forName("Utils");

            // returns the ClassLoader object associated with this Class
            ClassLoader cLoader = cls.getClassLoader();

            // input stream
            InputStream i = cLoader.getResourceAsStream(rsc);
            BufferedReader r = new BufferedReader(new InputStreamReader(i));

            // reads each line
            String l;
            while((l = r.readLine()) != null) {
                val.append(l);
            }
            i.close();
        } catch(Exception e) {
            System.out.println(e);
        }
        return val.toString();
    }
}
