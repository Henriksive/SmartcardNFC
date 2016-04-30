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
        type = CommunicationType.NFC;
    }

    public void initNFCCommunication(String cardAID, String INS, String P1, String P2,  String hexMessage){
        Log.i(TAG, "Initiated NFCCommunication.");
        nfcscc.sendDataToNFCCard(cardAID, INS, P1, P2, hexMessage);
    }

    public void disableNFC(){
        nfcscc.disableNFC();
    }

    public void setupmSDController(MSDSmartcardControllerInterface msdSmartcardControllerInterface, Activity currentActivity) {
        if(msdscc == null) {
            msdscc = new MSDSmartcardController(msdSmartcardControllerInterface, currentActivity);
        }
        type = CommunicationType.MSD;
    }

    public void initmSDCommunication(String cardAID, String INS, String P1, String P2,  String hexMessage){
        Log.i(TAG, "Initiated MSDCommunication.");
        msdscc.sendDataTomSDCard(cardAID, INS, P1, P2, hexMessage);
    }

    public void bindingStepOne(String AID){
        if(type.equals(CommunicationType.NFC)){
            nfcscc.sendDataToNFCCard(AID, "05", "01", "00", "00");
        }
        else{
            msdscc.sendDataTomSDCard(AID, "05", "01", "00", "00");
        }
    }

    public void bindingStepTwo(String AID, String code){
        if(type.equals(CommunicationType.NFC)){
            nfcscc.sendDataToNFCCard(AID, "05", "02", "00", code);
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
        nfcscc.sendDataToNFCCard(AID, "05", "03", "00", fullMessage);
    }

    // Signing

    public void signData(CommunicationType type, String cardAID, String hexMessage){
        if(type.equals(CommunicationType.NFC)) {
            nfcscc.sendDataToNFCCard(cardAID, "03", "00", "00", hexMessage);
        }
        else{
            msdscc.sendDataTomSDCard(cardAID, "03", "00", "00", hexMessage);
        }
    }

    //RSA
    public void cryptoRSA(CommunicationType type, boolean encrypt, String cardAID, String hexMessage){
        String p1 = "02";
        if(encrypt) {
            p1 = "01";
        }

        if(type.equals(CommunicationType.NFC)) {
            nfcscc.sendDataToNFCCard(cardAID, "06", p1, "00", hexMessage);
        }
        else{
            msdscc.sendDataTomSDCard(cardAID, "06", p1, "00", hexMessage);
        }
    }

    //AES
    public void cryptoAES(CommunicationType type, boolean encrypt, String cardAID, String hexMessage){
        String p1 = "02";
        if(encrypt) {
            p1 = "01";
        }

        if(type.equals(CommunicationType.NFC)) {
            nfcscc.sendDataToNFCCard(cardAID, "09", p1, "00", hexMessage);
        }
        else{
            msdscc.sendDataTomSDCard(cardAID, "09", p1, "00", hexMessage);
        }
    }

    //Get public key_MOD
    public void getCardPubMod(CommunicationType type, String cardAID){
        if(type.equals(CommunicationType.NFC)) {
            nfcscc.sendDataToNFCCard(cardAID, "01", "00", "00", "00");
        }
        else{
            msdscc.sendDataTomSDCard(cardAID, "01", "00", "00", "00");
        }
    }

    //Get public key_EXP
    public void getCardPubExp(CommunicationType type, String cardAID){
        if(type.equals(CommunicationType.NFC)) {
            nfcscc.sendDataToNFCCard(cardAID, "02", "00", "00", "00");
        }
        else{
            msdscc.sendDataTomSDCard(cardAID, "02", "00", "00", "00");
        }
    }
}
