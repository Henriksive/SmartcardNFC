package com.master.henrik.controller;

import android.content.Context;
import android.util.Log;

import com.master.henrik.msd.MSDCardReader;
import com.master.henrik.shared.Converter;
import com.master.henrik.statics.ApduStatics;

import java.util.Arrays;

/**
 * Created by Henri on 08.11.2015.
 */
public class MSDSmartcardController {

    private MSDSmartcardControllerInterface mainClass;
    private MSDCardReader msdCardReader;

    private final String TAG = "MSDSmartcardController";
    private static final String PREFIX_COMMAND = "80";
    private static final double framesize = ApduStatics.extendedAPDULength;

    private int numberOfFrames;
    private final byte[] signature = ApduStatics.apduSignature;

    public MSDSmartcardController(MSDSmartcardControllerInterface mClass, Context ctx){
        mainClass = mClass;
        msdCardReader = new MSDCardReader(mainClass, ctx, signature);

    }

    public void sendDataTomSDCard(String AID, String cardInstruction, String P1, String P2, String hexData){
        Log.d(TAG, "Initiating mSDCard communication");
        byte[] byteData = Converter.HexStringToByteArray(hexData);
        numberOfFrames = (int)Math.ceil(byteData.length / framesize);

        msdCardReader.resetQueue();
        sliceIntoFramesAndInsertInQueue(byteData, cardInstruction, P1, P2);
        msdCardReader.sendData(AID);

    }

    public void disconnectReader(){
        if(msdCardReader != null){
            msdCardReader.disconnect();
        }
    }

    private void sliceIntoFramesAndInsertInQueue(byte[] byteData, String cardInstruction, String P1, String P2) {
        for(int i = 0; i < numberOfFrames; i++){
            int start = (int)(i*framesize);
            int end = (int)(i*framesize + framesize);
            if(end > byteData.length) {
                end = byteData.length;
            }
            byte[] currentData = Arrays.copyOfRange(byteData, start, end);

            String hexLengthLC;
            String hexLengthLE;
            byte[] byteCurrentLengthLC;
            byte[] byteCurrentLengthLE;
            if(framesize > 255) {
                hexLengthLC = Converter.hexLengthToProperHex(Integer.toHexString(currentData.length));
                hexLengthLE = Converter.hexLengthToProperHex(Integer.toHexString(currentData.length));
                byteCurrentLengthLC = Converter.HexStringToByteArray("00" + hexLengthLC);
                byteCurrentLengthLE = Converter.HexStringToByteArray(hexLengthLE);
            }
            else {
                hexLengthLC = Converter.hexLengthToProperHex(Integer.toHexString(currentData.length)).substring(2);
                hexLengthLE = Converter.hexLengthToProperHex(Integer.toHexString(currentData.length)).substring(2);
                byteCurrentLengthLC = Converter.HexStringToByteArray(hexLengthLC);
                byteCurrentLengthLE = Converter.HexStringToByteArray(hexLengthLE);
            }
            Log.d(TAG, "Payload int size: " + currentData.length);
            Log.d(TAG, "Payload hex size: " + hexLengthLC);
            //byte[] byteCurrentLength = Converter.HexStringToByteArray("000001");

            String hexHeader = PREFIX_COMMAND + cardInstruction + P1 + P2;
            byte[] byteHeader = Converter.HexStringToByteArray(hexHeader);

            byte[] fullHeader = Converter.mergeByteArray(byteHeader, byteCurrentLengthLC);
            byte[] fullPayload = Converter.mergeByteArray(currentData, byteCurrentLengthLE);

            byte[] fullCommand = Converter.mergeByteArray(fullHeader, fullPayload);



            msdCardReader.addToCommandQueue(fullCommand);
        }
    }
}
