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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Henri on 16.09.2015.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class NFCCardReader implements NfcAdapter.ReaderCallback {
    private static final String TAG = "NFCCardReader";

    private ArrayList<byte[]> _dataList;


    // "OK" status word sent in response to SELECT AID command (0x9000)
    private static final byte[] SELECT_OK_SW = {(byte) 0x90, (byte) 0x00};
    private WeakReference<NFCCardCallback> cCardCallBack;
    // AID for card service.
    public String AID;

    public NFCCardReader(NFCCardCallback cardCallback){
        cCardCallBack = new WeakReference<NFCCardCallback>(cardCallback);
        _dataList = new ArrayList<byte[]>();
    }

    public void setCommandQueue(ArrayList<byte[]> datalist){
        _dataList = datalist;
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        Log.i(TAG, "New tag discovered.");

        IsoDep isoDep = IsoDep.get(tag);
        if (isoDep != null) {
            try {
                isoDep.connect();
                isoDep.setTimeout(60000);

                //Select correct application on card
                Log.d(TAG, "Starting select application");
                byte[] command = Converter.BuildSelectApdu(AID);

                Log.d(TAG, "Sending: " + Converter.ByteArrayToHexString(command));
                byte[] result = isoDep.transceive(command);

                int resultLength = result.length;
                byte[] statusWord = {result[resultLength-2], result[resultLength-1]};
                Log.d(TAG, "Received: " + Converter.ByteArrayToHexString(statusWord));

                //Send commands
                if (Arrays.equals(SELECT_OK_SW, statusWord)) {

                    //byte[] testData2 = Converter.HexStringToByteArray("08070000040102030404");
                    //byte[] testData = Converter.HexStringToByteArray("800900000000040102030400FF");
                    //byte[] testData2 = Converter.HexStringToByteArray("80070000000004010203040004");
                    //byte[] dekryptdata = Converter.HexStringToByteArray("8007000000000510569F370B0005");
                    for(byte[] payloadData : _dataList){
                        //payloadData = testData;
                        Log.d(TAG, "Sending: " + Converter.ByteArrayToHexString(payloadData));
                        byte[] response = isoDep.transceive(payloadData);

                        Log.d(TAG, "Receiving: " + Converter.ByteArrayToHexString(response));

                        cCardCallBack.get().onInfoReceived(Converter.ByteArrayToHexString(response));
                    }
                }
                else{
                    Log.d(TAG, "SELECT FAILED");
                }
            } catch (IOException e) {
                Log.e(TAG, "Could not communicate with NFC card: " + e.toString());
            }
        } else {
            Log.e(TAG, "IsoDep is null.");
        }
    }
}
