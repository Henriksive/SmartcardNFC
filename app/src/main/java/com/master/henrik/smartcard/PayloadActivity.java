package com.master.henrik.smartcard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.master.henrik.controller.MSDSmartcardController;
import com.master.henrik.controller.MSDSmartcardControllerInterface;
import com.master.henrik.controller.NFCSmartcardController;
import com.master.henrik.controller.NFCSmartcardControllerInterface;
import com.master.henrik.shared.Converter;
import com.master.henrik.shared.StorageHandler;
import com.master.henrik.statics.FilePaths;

public class PayloadActivity extends AppCompatActivity implements NFCSmartcardControllerInterface, MSDSmartcardControllerInterface {
    final String TAG = "PayLoad";
    Button btnTransmit;
    Button btnTransmitDatamSD;
    EditText lblAID;
    TextView outputField;
    String message;
    NFCSmartcardController nfcscc;
    MSDSmartcardController msdscc;

    //TIMING
    long startTime;
    long endTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payload);
        setupFields();
        setupButtons();
    }

    @Override
    public void onStop(){
        super.onStop();
        msdscc.disconnectReader();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_payload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupFields(){
        lblAID = (EditText)findViewById(R.id.edtAID);
        outputField = (TextView)findViewById(R.id.lblOutput);
    }

    private void setupButtons(){
        //SendButton
        btnTransmit = (Button)findViewById(R.id.btnTransmitData);
        btnTransmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initNFCCommunication();
            }
        });

        btnTransmitDatamSD = (Button)findViewById(R.id.btnTransmitDatamSD);
        btnTransmitDatamSD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initmSDCommunication();
            }
        });
    }

    private void initmSDCommunication(){

        Log.d(TAG, "mSD");
        String AID = lblAID.getText().toString();
        setupmSDController();

        StorageHandler storageHandler = new StorageHandler(this);

        message = storageHandler.readFromFileInAssets("smt_1000.txt");
        String hexMessage = Converter.StringToHex(message);

        Log.i(TAG, storageHandler.deleteFile(FilePaths.tempStorageFileName)+ "");


        startTime = System.nanoTime();

        msdscc.sendDataTomSDCard(AID, "08", "00", "00", hexMessage);
    }

    private void setupmSDController() {
        if(msdscc == null) {
            msdscc = new MSDSmartcardController(this, this);
        }
    }

    private void initNFCCommunication(){

        setupNFCController();

        Log.i(TAG, "Initiated NFCCommunication.");
        String AID = lblAID.getText().toString();

        StorageHandler storageHandler = new StorageHandler(getApplicationContext());

        message = storageHandler.readFromFileInAssets("smt_10000.txt");

        String hexMessage = Converter.StringToHex(message);

        Log.i(TAG, storageHandler.deleteFile(FilePaths.tempStorageFileName)+ "");

        startTime = System.nanoTime();
        nfcscc.sendDataToNFCCard(AID, "08", "00", "00", hexMessage);
    }

    private void setupNFCController() {
        if(nfcscc == null) {
            nfcscc = new NFCSmartcardController(this, this);
        }
    }

    @Override
    public void nfcCallback(final String completionStatus){
        endTime = System.nanoTime();
        final double totalTime = ((endTime-startTime)/1000000000.0);
        Log.i(TAG, "Total transaction time: " + totalTime + " seconds");


        StorageHandler storageHandler = new StorageHandler(getApplicationContext());

        String received = storageHandler.readFromFileAppDir(FilePaths.tempStorageFileName);
        //String received = "";
        //TODO!
        final boolean isEqual = message.equals(received);
        Log.i(TAG, "Received and sent is equal: " +isEqual);



        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                outputField.setText(totalTime + " seconds \nSent and received is equal: " +isEqual);
            }
        });

    }

    @Override
    public void mSDCallback(String completionStatus) {
        endTime = System.nanoTime();
        final double totalTime = ((endTime-startTime)/1000000000.0);
        Log.d(TAG, "MSDCALLBACK");
        Log.d(TAG, completionStatus);
        StorageHandler storageHandler = new StorageHandler(getApplicationContext());

        String received = storageHandler.readFromFileAppDir(FilePaths.tempStorageFileName);
        Log.d(TAG, received);
        final boolean isEqual = message.equals(received);
        Log.i(TAG, "Received and sent is equal: " + isEqual);
        outputField.setText(totalTime + " seconds \nSent and received is equal: " +isEqual);
    }
}
