package com.master.henrik.shared;

/**
 * Created by Henri on 17.09.2015.
 */
public class Converter {

    private static final String SELECT_APDU_HEADER = "00A40400";

    public static byte[] BuildSelectApdu(String aid) {
        // Format: [CLASS | INSTRUCTION | PARAMETER 1 | PARAMETER 2 | LENGTH | DATA]
        return HexStringToByteArray(SELECT_APDU_HEADER + String.format("%02X", aid.length() / 2) + aid);
    }

    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] HexStringToByteArray(String s) {
        int len = s.length();
        if(len == 1){
            s = "0" + s;
            len = s.length();
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String StringToHex(String s){
        char[] chars = s.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++)
        {
            builder.append(Integer.toHexString((int) chars[i]));
        }
        return builder.toString();
    }

    public static String hexToString(String hexValue)
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < hexValue.length(); i += 2)
        {
            String str = hexValue.substring(i, i + 2);
            builder.append((char) Integer.parseInt(str, 16));
        }
        return builder.toString();
    }

    public static String hexLengthToProperHex(String hexLength){
        if(hexLength.length() == 1)
            hexLength = "000" + hexLength;
        else if(hexLength.length() == 2){
            hexLength = "00" + hexLength;
        }
        else if(hexLength.length() == 3){
            hexLength = "0" + hexLength;
        }
        return hexLength;
    }
}
