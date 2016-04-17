package com.master.henrik.smartcard;

import android.app.ExpandableListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.master.henrik.controller.NFCSmartcardController;
import com.master.henrik.controller.NFCSmartcardControllerInterface;
import com.master.henrik.shared.Converter;
import com.master.henrik.shared.StorageHandler;
import com.master.henrik.statics.FilePaths;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

public class BindingActivity extends AppCompatActivity implements NFCSmartcardControllerInterface, onSubmitListener{
    final String TAG = "BindingActivity";
    NFCSmartcardController nfcscc;
    Button initiateButton;
    Button resetButton;
    String AID = "0102030405060708090007";

    private KeyPairGenerator kpg;
    private KeyPair keys;

    int transactionstep = 0;
    int numberOfTriesLeft = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding);
        setupButtonNavigation();
        setupKeys();
    }

    private void setupKeys(){
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
            //kpg.initialize(2048);
            kpg.initialize(512);
            keys = kpg.generateKeyPair();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    public void setupButtonNavigation() {

        initiateButton = (Button) findViewById(R.id.btnInitiateBinding);
        initiateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateBinding();
            }
        });

        resetButton = (Button) findViewById(R.id.btnReset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetCard();
            }
        });
    }

    private void resetCard(){
        //TESTING ONLY
        if(nfcscc == null){
            nfcscc = new NFCSmartcardController(this, this);
        }
        String hexMessage = "0101";
        nfcscc.sendPayloadDataToNFCCard(AID, "05", "09", "00", hexMessage);
    }

    private void initiateBinding(){
        sendPublicKey();
        transactionstep = 0;
        Log.i(TAG, "Initiated binding.");
        String hexMessage = Converter.StringToHex("444");
        sendMessage(hexMessage, "01");

    }

    private void sendMessage(String hexMessage, String p1){
        if(nfcscc == null){
            nfcscc = new NFCSmartcardController(this, this);
        }
            nfcscc.sendPayloadDataToNFCCard(AID, "05", p1, "00", hexMessage);
    }

    private void sendPINCode(String code){
        nfcscc.sendPayloadDataToNFCCard(AID, "05", "02", "00", code);
    }

    private void handleTransaction(String receivedHexString){
        String[] hexValues = hexStringToHexArray(receivedHexString);
        if(hexValues[1].equals("05")){
            Log.d(TAG, "RESET step");
            //Reset was performed
            transactionstep = 0;
        }
        else {
            switch (transactionstep) {
                case 0:
                    Log.d(TAG, "Case 0");

                    if (hexValues[1].equals("00")) {
                        //Smart card wants pin code
                        numberOfTriesLeft = Integer.parseInt(hexValues[2]);
                        showPINDialog();

                        break;
                    } else {
                        //Accepted PIN, lets move on


                        transactionstep++;
                    }

                case 1:
                    Log.d(TAG, "PIN was accepted, next stage");
                    sendPublicKey();

                    break;

                default:
                    Log.d(TAG, "Unrecognized transaction step");
            }
        }
    }

    private void sendPublicKey() {

        RSAPublicKey mPub = (RSAPublicKey) keys.getPublic();


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
        sendMessage(fullMessage, "03");
    }

    private String[] hexStringToHexArray(String s){
        String[] collection = new String[s.length()/2];
        for(int i = 0; i < s.length()/2; i++){
            collection[i] = s.substring(i*2, i*2+2);
        }
        return collection;
    }

    private void showPINDialog(){
        PincodeDialog pinDialog = new PincodeDialog();
        pinDialog.mListener = this;
        pinDialog.numberOfTries = numberOfTriesLeft;
        pinDialog.show(getFragmentManager(), "");
    }

    @Override
    public void nfcCallback(String completionStatus) {
        nfcscc.disableNFC();
        Log.i(TAG, "NFCCallback: " + completionStatus);

        StorageHandler storageHandler = new StorageHandler(getApplicationContext());

            String received = storageHandler.readFromFileAppDir(FilePaths.tempStorageFileName);
            handleTransaction(received);




    }

    @Override
    public void setOnSubmitListener(String arg) {

        String codeHex = "";

        String[] args = arg.split("");

        for(String s : args){
            try {
                codeHex += "0" + Integer.toHexString(Integer.parseInt(s));
            }
            catch(Exception e){
                Log.d(TAG, "\"" + s + "\" is not a valid integer. If \"\" (blank), please ignore.");
            }
        }
        Log.d(TAG, "HexPIN: " + codeHex);

        sendPINCode(codeHex);

    }
}
