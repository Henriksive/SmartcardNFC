package com.master.henrik.shared;

import android.util.Log;

import java.util.Random;

/**
 * Created by Henri on 13.10.2015.
 */
public class StringGenerator {

    private static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    public static String generateRandomString(int length){
        StringBuilder builder = new StringBuilder();
        Random randGen = new Random();
        for(int i = 0; i < length; i++){
            int number = randGen.nextInt(CHAR_LIST.length());
           builder.append(CHAR_LIST.charAt(number));
        }
        return  builder.toString();
    }
}
