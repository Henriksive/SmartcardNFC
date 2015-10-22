package com.master.henrik.shared;

/**
 * Created by Henri on 20.10.2015.
 */
public class Common {

    public static byte[] mergeByteArray(byte[] a, byte[] b){
        int aLen = a.length;
        int bLen = b.length;
        byte[] c= new byte[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
}
