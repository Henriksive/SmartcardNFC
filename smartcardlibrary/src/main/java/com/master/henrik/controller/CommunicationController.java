package com.master.henrik.controller;

import android.app.Activity;
import android.util.Log;

import com.master.henrik.shared.CommunicationType;
import com.master.henrik.shared.Converter;

import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

/**
 * Created by Henri on 17.04.2016.
 */
public class CommunicationController {

    NFCSmartcardController nfcscc;
    MSDSmartcardController msdscc;
    final String TAG = "CommunicationController";
    CommunicationType type = CommunicationType.NOTSET;

    public void setupNFCController(NFCSmartcardControllerInterface nfcSmartcardControllerInterface, Activity currentActivity) {
        if(nfcscc == null) {
            nfcscc = new NFCSmartcardController(nfcSmartcardControllerInterface, currentActivity);
        }
    }

    public void initNFCCommunication(String cardAID, String hexMessage){
        Log.i(TAG, "Initiated NFCCommunication.");
        nfcscc.sendPayloadDataToNFCCard(cardAID, "08", "00", "00", hexMessage);
    }

    public void disableNFC(){
        nfcscc.disableNFC();
    }

    public void setupmSDController(MSDSmartcardControllerInterface msdSmartcardControllerInterface, Activity currentActivity) {
        if(msdscc == null) {
            msdscc = new MSDSmartcardController(msdSmartcardControllerInterface, currentActivity);
        }
    }

    public void initmSDCommunication(String cardAID, String hexMessage){
        Log.i(TAG, "Initiated MSDCommunication.");
        msdscc.sendDataTomSDCard(cardAID, "01", "00", "00", hexMessage);
    }

    //Binding

    public void bindingInit(NFCSmartcardControllerInterface nfcSmartcardControllerInterface, Activity currentActivity){
        type = CommunicationType.NFC;
        setupNFCController(nfcSmartcardControllerInterface, currentActivity);
    }

    public void bindingInit(MSDSmartcardControllerInterface msdSmartcardControllerInterface, Activity currentActivity){
        type = CommunicationType.MSD;
        setupmSDController(msdSmartcardControllerInterface, currentActivity);
    }

    public void bindingStepOne(String AID){
        if(type.equals(CommunicationType.NFC)){
            nfcscc.sendPayloadDataToNFCCard(AID, "05", "01", "00", "00");
        }
        else{
            msdscc.sendDataTomSDCard(AID, "05", "01", "00", "00");
        }
    }

    public void bindingStepTwo(String AID, String code){
        if(type.equals(CommunicationType.NFC)){
            nfcscc.sendPayloadDataToNFCCard(AID, "05", "02", "00", code);
        }
        else{
            msdscc.sendDataTomSDCard(AID, "05", "02", "00", code);
        }
    }

    public void bindingStepThree(String AID, RSAPublicKey mPub) {
        byte[] publicByteArrModTemp = mPub.getModulus().toByteArray();
        byte[] publicByteArrMod = Arrays.copyOfRange(publicByteArrModTemp, 1, publicByteArrModTemp.length); //Remove signed short!
        byte[] publicByteArrExp = mPub.getPublicExponent().toByteArray();

        String publicHexKeyMod = Converter.ByteArrayToHexString(publicByteArrMod);
        //String modLength = "0" + Integer.toHexString(publicHexKeyMod.length()/2);
        String modLength = Integer.toHexString(publicHexKeyMod.length()/2);

        Log.d(TAG, "Mod: " + publicHexKeyMod);
        Log.d(TAG, publicHexKeyMod.length() +" : " + modLength);

        String publicHexKeyExp = Converter.ByteArrayToHexString(publicByteArrExp);
        String expLength = "0" + Integer.toHexString(publicHexKeyExp.length()/2); //TODO: DANGEROUS
        //String expLength =  Integer.toHexString(publicHexKeyExp.length()/2); //TODO: DANGEROUS
        Log.d(TAG, "Exp: " + publicHexKeyExp);
        Log.d(TAG, publicHexKeyExp.length() + " : " +expLength);

        String fullMessage = modLength + publicHexKeyMod + expLength + publicHexKeyExp;


        Log.d(TAG, "Fullmsg length: " + fullMessage.length());
        Log.d(TAG, "Fullmsg: " + fullMessage);
        nfcscc.sendPayloadDataToNFCCard(AID, "05", "03", "00", fullMessage);
    }
}
