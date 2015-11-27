package com.master.henrik.nfc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.util.Log;

import com.master.henrik.controller.NFCSmartcardControllerInterface;
import com.master.henrik.shared.Converter;
import com.master.henrik.shared.StorageHandler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

//import com.master.henrik.smartcardnfc.R;


/**
 * Created by Henri on 16.09.2015.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class NFCCardReaderController implements NFCCardCallback{

    private final static String TAG = "NFCCardReaderController";
    private NFCCardReader nfcReader;
    public static int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
    private Activity _currentActivity;
    public static String AIDString;
    private WeakReference<NFCSmartcardControllerInterface> _smcInterface;
    private ArrayList<byte[]> _dataList;
    private int _packetCounter;
    private String _receivedData;
    private StorageHandler storageHandler;

    public NFCCardReaderController(NFCSmartcardControllerInterface smcInterface, Activity currentActivity){
        nfcReader = new NFCCardReader(this);
        _smcInterface = new WeakReference<NFCSmartcardControllerInterface>(smcInterface);
        _currentActivity = currentActivity;
        _dataList = new ArrayList<byte[]>();
        storageHandler = new StorageHandler(currentActivity.getApplicationContext());
    }

    public void addToCommandQueue(byte[] data){
        _dataList.add(data);
    }

    public void initiateNFCTransaction() {
        Log.i(TAG, "Enabling reader mode");
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(_currentActivity);

        if (nfc != null) {
            nfcReader.setCommandQueue(_dataList);
            nfcReader.AID = AIDString;
            _packetCounter = 0;
            _receivedData = "";
            nfc.enableReaderMode(_currentActivity, nfcReader, READER_FLAGS, null);
        }
    }

    public void disableNFCTransaction() {
        Log.i(TAG, "Disabling reader mode");
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(_currentActivity);
        if (nfc != null) {
            nfc.disableReaderMode(_currentActivity);
        }
    }

    @Override
    public void onInfoReceived(String info) {
        _packetCounter++;
        storageHandler.writeToFile(Converter.HexStringToByteArray(info.substring(0, info.length()-4)));
        if(_packetCounter == _dataList.size()) {
            storageHandler.closeOutputStream();
            _smcInterface.get().nfcCallback("Done");
        }

    }
}
