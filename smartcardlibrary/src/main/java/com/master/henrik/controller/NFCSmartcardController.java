package com.master.henrik.controller;

import android.app.Activity;
import android.util.Log;

import com.gemalto.gmm.sdk.Apdu;
import com.master.henrik.nfc.NFCCardReaderController;
import com.master.henrik.shared.Common;
import com.master.henrik.shared.Converter;
import com.master.henrik.statics.ApduStatics;

import java.util.Arrays;

/**
 * Created by Henri on 19.10.2015.
 */
public class NFCSmartcardController {
    private static final String TAG = "NFCSmartcardController";
    private static final String PREFIX_COMMAND = "80";
    private static final double framesize = ApduStatics.extendedAPDULength;
    private static final double framesizeAES = ApduStatics.extendedAPDULengthAES;

    private int numberOfFrames;

    private NFCSmartcardControllerInterface mainClass;
    private Activity _currentActivity;
    private NFCCardReaderController nfcCardReaderController;


    public NFCSmartcardController(NFCSmartcardControllerInterface mClass, Activity currentActivity){
        mainClass = mClass;
        _currentActivity = currentActivity;
    }

    /**
     *  Method for sending data to a NFC card. This must be done prior to discovering a NFC tag.
     * @param AID AID of Smartcard application
     * @param cardInstruction Instruction on Smartcard application
     * @param hexData Payload
     */
    public void sendPayloadDataToNFCCard(String AID, String cardInstruction, String P1, String P2, String hexData){
        Log.i(TAG, "sendPayloadDataToNFCCard()");
        nfcCardReaderController = new NFCCardReaderController(mainClass, _currentActivity);
        nfcCardReaderController.AIDString = AID;

        byte[] byteData = Converter.HexStringToByteArray(hexData);
        numberOfFrames = (int)Math.ceil(byteData.length / framesize);


        sliceIntoFramesAndInsertInQueueForPayload(byteData, cardInstruction, P1, P2);

        nfcCardReaderController.initiateNFCTransaction();
    }

    private void sliceIntoFramesAndInsertInQueueForPayload(byte[] byteData, String cardInstruction, String P1, String P2) {
        for(int i = 0; i < numberOfFrames; i++){
            int start = (int)(i*framesize);
            int end = (int)(i*framesize + framesize);
            if(end > byteData.length) {
                end = byteData.length;
            }
            byte[] currentData = Arrays.copyOfRange(byteData, start, end);


            String hexLengthLC = Converter.hexLengthToProperHex(Integer.toHexString(currentData.length));
            String hexLengthLE = Converter.hexLengthToProperHex(Integer.toHexString(ApduStatics.maxDefaultLE));

            byte[] byteCurrentLengthLC = Converter.HexStringToByteArray("00" +hexLengthLC);
            byte[] byteCurrentLengthLE = Converter.HexStringToByteArray(hexLengthLE);

            String hexHeader = PREFIX_COMMAND + cardInstruction + P1 + P2;
            byte[] byteHeader = Converter.HexStringToByteArray(hexHeader);

            byte[] fullHeader = Common.mergeByteArray(byteHeader, byteCurrentLengthLC);
            byte[] fullPayload = Common.mergeByteArray(currentData, byteCurrentLengthLE);

            byte[] fullCommand = Common.mergeByteArray(fullHeader, fullPayload);



            nfcCardReaderController.addToCommandQueue(fullCommand);
        }
    }

    public void sendAESCryptoDataToNFCCard(String AID, String cardInstruction, String P1, String P2, String hexData){
        Log.i(TAG, "sendCryptoDataToNFCCard()");
        nfcCardReaderController = new NFCCardReaderController(mainClass, _currentActivity);
        nfcCardReaderController.AIDString = AID;

        byte[] paddedByteData;
        byte[] byteData = Converter.HexStringToByteArray(hexData);
        Log.i(TAG, "byteDatalength: " + byteData.length);
        int remainder = byteData.length % ApduStatics.AESBlockSize;
        Log.i(TAG, "Remainder: " + remainder);
        if(remainder != 0){
            int missingPad = ApduStatics.AESBlockSize - remainder;
            paddedByteData = new byte[byteData.length + missingPad];
            System.arraycopy(byteData, 0, paddedByteData, 0, byteData.length);
            System.arraycopy(ApduStatics.AESPadArray, 0, paddedByteData ,byteData.length, missingPad);
        }
        else{
            paddedByteData = byteData;
        }
        Log.i(TAG, "New byteDatalenght: " + paddedByteData.length);

        numberOfFrames = (int)Math.ceil(paddedByteData.length / framesizeAES);


        sliceIntoFramesAndInsertInQueueForAESCrypto(paddedByteData, cardInstruction, P1, P2);

        nfcCardReaderController.initiateNFCTransaction();
    }

    private void sliceIntoFramesAndInsertInQueueForAESCrypto(byte[] byteData, String cardInstruction, String P1, String P2) {
        for(int i = 0; i < numberOfFrames; i++){
            int start = (int)(i*framesizeAES);
            int end = (int)(i*framesizeAES + framesizeAES);
            if(end > byteData.length) {
                end = byteData.length;
            }
            byte[] currentData = Arrays.copyOfRange(byteData, start, end);


            String hexLengthLC = Converter.hexLengthToProperHex(Integer.toHexString(currentData.length));
            String hexLengthLE = Converter.hexLengthToProperHex(Integer.toHexString(currentData.length));
            Log.d(TAG,"Payload int size: " + currentData.length);
            Log.d(TAG,"Payload hex size: " + hexLengthLC);

            byte[] byteCurrentLengthLC = Converter.HexStringToByteArray("00" +hexLengthLC);
            byte[] byteCurrentLengthLE = Converter.HexStringToByteArray(hexLengthLE);
            //byte[] byteCurrentLength = Converter.HexStringToByteArray("000001");

            String hexHeader = PREFIX_COMMAND + cardInstruction + P1 + P2;
            byte[] byteHeader = Converter.HexStringToByteArray(hexHeader);

            byte[] fullHeader = Common.mergeByteArray(byteHeader, byteCurrentLengthLC);
            byte[] fullPayload = Common.mergeByteArray(currentData, byteCurrentLengthLE);

            byte[] fullCommand = Common.mergeByteArray(fullHeader, fullPayload);



            nfcCardReaderController.addToCommandQueue(fullCommand);
        }
    }



    /**
     * Method for turning of NFC discovery.
     */
    public void disableNFC(){
        if(nfcCardReaderController != null){
            nfcCardReaderController.disableNFCTransaction();
        }
    }
}
