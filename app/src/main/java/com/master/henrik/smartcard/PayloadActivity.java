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

import com.master.henrik.controller.CommunicationController;
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

    CommunicationController cc = new CommunicationController();


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
        if(msdscc != null) {
            msdscc.disconnectReader();
        }
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
                nfcButtonClick();
            }
        });

        btnTransmitDatamSD = (Button)findViewById(R.id.btnTransmitDatamSD);
        btnTransmitDatamSD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msdButtonClick();
            }
        });
    }





    private void nfcButtonClick(){
        String AID = lblAID.getText().toString();
        StorageHandler storageHandler = new StorageHandler(getApplicationContext());
        message = storageHandler.readFromFileInAssets("smt_250.txt");
        Log.i(TAG, storageHandler.deleteFile(FilePaths.tempStorageFileName)+ "");
        String hexMessage = Converter.StringToHex(message);

        cc.setupNFCController(this, this);
        cc.initNFCCommunication(AID, "08", "00", "00", hexMessage);
    }

    private void msdButtonClick(){
        String AID = lblAID.getText().toString();
        StorageHandler storageHandler = new StorageHandler(this);

        Log.i(TAG, storageHandler.deleteFile(FilePaths.tempStorageFileName) + "");
        String hexMessage = "04";


        cc.setupmSDController(this, this);
        cc.initmSDCommunication(AID, "08", "00", "00", hexMessage);
    }



    @Override
    public void nfcCallback(final String completionStatus){
        StorageHandler storageHandler = new StorageHandler(getApplicationContext());

        String received = storageHandler.readFromFileAppDir(FilePaths.tempStorageFileName);
        Log.i(TAG, "got: " + received);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                outputField.setText("Useful text here");
            }
        });

    }

    @Override
    public void mSDCallback(String completionStatus) {
        StorageHandler storageHandler = new StorageHandler(getApplicationContext());

        String received = storageHandler.readFromFileAppDir(FilePaths.tempStorageFileName);
        Log.d(TAG, "got: " + received);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                outputField.setText("Useful text here");
            }
        });
    }
}
