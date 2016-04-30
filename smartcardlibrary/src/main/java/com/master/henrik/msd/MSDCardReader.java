package com.master.henrik.msd;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.gemalto.idgo800.IDGoMain;
import com.gemalto.idgo800.apdu.ApduAPI;
import com.master.henrik.controller.MSDSmartcardControllerInterface;
import com.master.henrik.shared.Converter;
import com.master.henrik.shared.StorageHandler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Henri on 08.11.2015.
 */
public class MSDCardReader implements MSDCardCallback{
    private final String TAG = "MSDCardReader";
    private WeakReference<MSDSmartcardControllerInterface> _smcInterface;

    // "OK" status word sent in response to SELECT AID command (0x9000)
    private static final byte[] SELECT_OK_SW = {(byte) 0x90, (byte) 0x00};
    private ApduAPI mApdu = new ApduAPI();
    private int mApduSessionId = 0;
    private final byte[] appSignature;
    private byte[] selectAID;
    private Context _ctx;
    private String lastInfo = "";
    private ArrayList<byte[]> _dataList;
    private StorageHandler storageHandler;

    public MSDCardReader(MSDSmartcardControllerInterface smcInterface, Context ctx, byte[] appSignature){
        _smcInterface = new WeakReference<MSDSmartcardControllerInterface>(smcInterface);
        this.appSignature = appSignature;
        _ctx = ctx;
        _dataList = new ArrayList<byte[]>();
        storageHandler = new StorageHandler(_ctx);
    }

    public void resetQueue(){
        _dataList = new ArrayList<byte[]>();
    }

    public void addToCommandQueue(byte[] data){
        _dataList.add(data);
    }

    public void sendData(String AID){
        Log.d(TAG, "sendData()");
        selectAID = Converter.BuildSelectApdu(AID);
        setupIDGo();
        new AsyncCommunication().execute();
    }

    public void disconnect(){
        if(mApduSessionId != 0){
            mApdu.APDU_Finalize(mApduSessionId);
            IDGoMain.release();
        }
    }

    private void setupIDGo(){
        IDGoMain.initialize(_ctx);

        Log.i(TAG, "IDGO ctx: " + String.valueOf(IDGoMain.getContext().getPackageName()));
        Log.i(TAG, "IDGO isInit: " + String.valueOf(IDGoMain.isInitialized()));
        Log.i(TAG, "IDGO started: " + String.valueOf(IDGoMain.isStarted()));
        Log.i(TAG, "IDGO package: " + IDGoMain.getContext().getPackageName());


        Log.i(TAG, "IDGO package equals ctx package: " + _ctx.getPackageName().equals(IDGoMain.getContext().getPackageName()));

        Log.i(TAG, "PackageName: " + _ctx.getPackageName());
        Log.i(TAG, "APPSIGNATURE: " + Converter.ByteArrayToHexString(appSignature));

        mApduSessionId = mApdu.APDU_Init(_ctx.getPackageName(), appSignature);
        Log.i(TAG, "mApduSessionId: " + mApduSessionId);



        Log.i(TAG, "mApdu lastError: " + mApdu.APDU_GetLastError());
    }

    @Override
    public void onInfoReceived(String info) {
        Log.d(TAG, "onInfoReceived()");
        _smcInterface.get().mSDCallback(info);
    }

    private class AsyncCommunication extends AsyncTask<Void, Void, byte[]>{
        @Override
        protected byte[] doInBackground(Void... params) {
            byte[] response = null;

            try {
                int[] readerList;
                boolean isCardFound = false;


                readerList = mApdu.APDU_ListReaders(mApduSessionId);
                for(int j : readerList){
                    Log.d(TAG, "Reader: " + j);
                }
                if (readerList != null) {
                    for (int i = 0; i < readerList.length; i++) {
                        //Check if the reader is an MicroSD
                        if(readerList[i] == 5){
                            if (mApdu.APDU_Connect(mApduSessionId, readerList[i])) {
                                Log.d(TAG, "Connected to: " + i);
                                isCardFound = true;
                                break;
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "ReaderList is null.");
                    lastInfo = "ReaderList is null";
                }
                if (isCardFound) {
                    response = mApdu.APDU_Transmit(
                            mApduSessionId,
                            selectAID,
                            1000
                    );

                    int resultLength = response.length;
                    byte[] statusWord = {response[resultLength-2], response[resultLength-1]};
                    //Send commands
                    if (Arrays.equals(SELECT_OK_SW, statusWord)) {
                        Log.d(TAG, "OK");

                        Log.d(TAG, "isConnected: " + mApdu.APDU_IsConnected(mApduSessionId));
                        Log.d(TAG, "IDGO isInit: " + IDGoMain.isInitialized());
                        Log.d(TAG, "IDGO isStarted: " + IDGoMain.isStarted());
                        Log.d(TAG, "SelectAPDU: " + Converter.ByteArrayToHexString(selectAID));

                        for(byte[] payloadData : _dataList){
                            Log.d(TAG, "Sending: " + Converter.ByteArrayToHexString(payloadData));
                            response = mApdu.APDU_Transmit(
                                    mApduSessionId,
                                    payloadData,
                                    5000
                            );
                            Log.d(TAG, "Received: " + Converter.ByteArrayToHexString(response));




                            Log.d(TAG, "resp: " + response);
                            Log.d(TAG, "err: " + mApdu.APDU_GetLastError());
                            writeToFile(response);
                        }
                        storageHandler.closeOutputStream();
                    }
                } else {
                    Log.e(TAG, "No card found.");
                }

            }catch (Exception e){
                Log.e(TAG, e.toString());
            }

            return response;
        }

        @Override
        protected  void onPostExecute(byte[] result){
            storageHandler.closeOutputStream();
            onInfoReceived("Done");
        }

        private void writeToFile(byte[] data){
            storageHandler.writeToFile(data);

        }
    }
}
