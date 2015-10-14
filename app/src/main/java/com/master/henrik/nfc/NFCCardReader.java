package com.master.henrik.nfc;

import android.annotation.TargetApi;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Build;
import android.util.Log;

import com.master.henrik.shared.Converter;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;



/**
 * Created by Henri on 16.09.2015.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class NFCCardReader implements NfcAdapter.ReaderCallback {
    private static final String TAG = "NFCCardReader";



    // "OK" status word sent in response to SELECT AID command (0x9000)
    private static final byte[] SELECT_OK_SW = {(byte) 0x90, (byte) 0x00};
    private WeakReference<CardCallback> cCardCallBack;
    //public String payloadString = "8000000000";
    public String payloadString = "";
    // AID for card service.
    public String SD_CARD_AID;

    public NFCCardReader(CardCallback cardCallback){
        cCardCallBack = new WeakReference<CardCallback>(cardCallback);
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        Log.i(TAG, "New tag discovered.");

        IsoDep isoDep = IsoDep.get(tag);
        if (isoDep != null) {
            try {
                isoDep.connect();

                //Select correct application on card
                Log.i(TAG, "Starting select application");
                byte[] command = Converter.BuildSelectApdu(SD_CARD_AID);

                Log.i(TAG, "Sending: " + Converter.ByteArrayToHexString(command));
                byte[] result = isoDep.transceive(command);

                int resultLength = result.length;
                byte[] statusWord = {result[resultLength-2], result[resultLength-1]};
                Log.i(TAG, "Received: " + statusWord.toString());

                //Send second command
                if (Arrays.equals(SELECT_OK_SW, statusWord)) {

                    Log.i(TAG, "Starting send command to application");

                    byte[] command2 = Converter.HexStringToByteArray(payloadString);
                    Log.i(TAG, "SENDING: " + Converter.ByteArrayToHexString(command2));
                    result = isoDep.transceive(command2);
                    Log.i(TAG, "RECEIVING : " + Converter.ByteArrayToHexString(result));

                    cCardCallBack.get().onInfoReceived(Converter.ByteArrayToHexString(result));
                }
            } catch (IOException e) {
                Log.e(TAG, "Could not communicate with NFC card: " + e.toString());
            }
        } else {
            Log.i(TAG, "IsoDep is null.");
        }
    }

    public interface CardCallback {
        public void onInfoReceived(String info);
    }
}
