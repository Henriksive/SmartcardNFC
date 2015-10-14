package com.master.henrik.nfc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import com.master.henrik.smartcardnfc.R;


/**
 * Created by Henri on 16.09.2015.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class CardReaderFragment extends Fragment implements  NFCCardReader.CardCallback{

    private final static String TAG = "CardReaderFragment";
    private NFCCardReader nfcReader;
    public static int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
    public static Activity currentActivity;
    public static String AIDString;
    /**
     * Hex-String
     */
    public static String transmitString;


    public CardReaderFragment(){
        nfcReader = new NFCCardReader(this);
    }




    public void enableReaderMode() {
        Log.i(TAG, "Enabling reader mode");
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(currentActivity);
        if (nfc != null) {
            nfcReader.payloadString = transmitString;
            nfcReader.SD_CARD_AID = AIDString;
            nfc.enableReaderMode(currentActivity, nfcReader, READER_FLAGS, null);
        }
    }

    public void disableReaderMode() {
        Log.i(TAG, "Disabling reader mode");
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(currentActivity);
        if (nfc != null) {
            nfc.disableReaderMode(currentActivity);
        }
    }

    @Override
    public void onInfoReceived(final String info) {
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView outputField = (TextView) currentActivity.findViewById(R.id.lblOutput);
                outputField.setText(info);
                Log.i(TAG, "Received: " + info);
            }
        });
    }
}
