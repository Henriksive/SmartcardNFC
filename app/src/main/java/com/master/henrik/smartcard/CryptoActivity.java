package com.master.henrik.smartcard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.master.henrik.controller.NFCSmartcardController;
import com.master.henrik.controller.NFCSmartcardControllerInterface;
import com.master.henrik.shared.Converter;
import com.master.henrik.shared.StorageHandler;
import com.master.henrik.statics.FilePaths;

public class CryptoActivity extends AppCompatActivity implements NFCSmartcardControllerInterface{

    final String TAG = "PayLoad";
    Button btnTransmit;
    Button btnTransmitDatamSD;
    EditText lblAID;
    TextView outputField;
    ToggleButton tglCryptType;
    NFCSmartcardController nfcscc;
    //TIMING
    long startTime;
    long endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto);
        getSupportActionBar().setTitle("Crypto");
        setupFields();
        setupButtons();
    }

    private void setupFields(){
        lblAID = (EditText)findViewById(R.id.edtAID);
        outputField = (TextView)findViewById(R.id.lblOutput);
        tglCryptType = (ToggleButton)findViewById(R.id.tglencrypt);
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
                //initmSDCommunication();
            }
        });
    }

    private void initNFCCommunication() {
        setupNFCController();

        Log.i(TAG, "Initiated NFCCommunication.");
        String AID = lblAID.getText().toString();

        StorageHandler storageHandler = new StorageHandler(getApplicationContext());

        String message = storageHandler.readFromFileInAssets("smt_32000.txt");

        String hexMessage = Converter.StringToHex(message);
        hexMessage = "01030307010303070103030701030307";
        hexMessage = "9BE1FE5ED9572ADDCC7A21ED3EA3E99B";
        Log.i(TAG, storageHandler.deleteFile(FilePaths.tempStorageFileName) + "");

        startTime = System.nanoTime();
        if(tglCryptType.isChecked()){
            //Decrypt
            //hexMessage = "47838AA8B4CED92FE9A9AC8A18B71D005E4F0C7C72EEC23EF04CF1D96B1398CBDFFC6189C60B54EF6533B8D22BDAB454B658868CCC73815CCFF4F229D099967EA6A0C672C70181CF42F9BD10CDB1C8028F4EAB93DF87BFE757A5C4A1BEAE026BE47E4334C3F608C353C843EB8379AB04FA7AA715BF8F1AEE985A2383CC559B195FAA60A5BA694C17B4AF6276DB347236DC4EAFE476466E92A711A0A7CA63FCC29A484B6832EB4DCCA6244338A03F0A09A58A7C5498E361F43DBE7BAFA66A3512E9EE85B8711EF3994C66C4B35A508E6AEE304B3D4F4E48F8850F2ED05E3AC2FF322392F3FAA927F93707212A2A3A1CC5A0AD598CA5400CA79233A31BEB5FB8DA";
            nfcscc.sendAESCryptoDataToNFCCard(AID, "09", "02", "00", hexMessage);
        }
        else{
            //Encrypt
            nfcscc.sendAESCryptoDataToNFCCard(AID, "09", "01", "00", hexMessage);
        }

    }

    private void setupNFCController() {
        if(nfcscc == null) {
            nfcscc = new NFCSmartcardController(this, this);
        }
    }

    @Override
    public void nfcCallback(String completionStatus) {
        endTime = System.nanoTime();
        final double totalTime = ((endTime-startTime)/1000000000.0);
        Log.i(TAG, "Total transaction time: " + totalTime + " seconds");


        StorageHandler storageHandler = new StorageHandler(getApplicationContext());

        //String received = storageHandler.readFromFileAppDir(FilePaths.tempStorageFileName);
        String received = "";
        //TODO!
        //final boolean isEqual = message.equals(received);
        //Log.i(TAG, "Received and sent is equal: " +isEqual);



        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                outputField.setText(totalTime + " seconds ");
            }
        });

    }
}
