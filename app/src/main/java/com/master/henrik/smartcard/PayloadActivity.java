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

import com.master.henrik.controller.SmartcardController;
import com.master.henrik.controller.SmartcardControllerInterface;
import com.master.henrik.shared.Converter;
import com.master.henrik.shared.StorageHandler;
import com.master.henrik.shared.StringGenerator;

public class PayloadActivity extends AppCompatActivity implements SmartcardControllerInterface{
    final String TAG = "PayLoad";
    Button btnTransmit;
    EditText lblAID;
    TextView outputField;
    String message;
    SmartcardController scc;

    //TIMING
    long startTime;
    long endTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payload);
        setupFields();
        setupButtons();
        scc = new SmartcardController(this, this);
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
    }

    private void initNFCCommunication(){
        Log.i(TAG, "Initiated NFCCommunication.");
        String AID = lblAID.getText().toString();

        message = StringGenerator.generateRandomString(10000); //Random generated message


        StorageHandler storageHandler = new StorageHandler(getApplicationContext());

        message = storageHandler.readFromFileInAssets("smt_10000.txt");
        String hexMessage = Converter.StringToHex(message);

        Log.i(TAG, storageHandler.deleteFile("smartcardRunTest.txt")+ "");

        startTime = System.nanoTime();
        scc.sendDataToNFCCard(AID, "08", "00", "00", hexMessage);
    }

    @Override
    public void nfcCallback(final String status){
        endTime = System.nanoTime();
        final double totalTime = ((endTime-startTime)/1000000000.0);
        Log.i(TAG, "Total transaction time: " + totalTime + " seconds");

        StorageHandler storageHandler = new StorageHandler(getApplicationContext());

        String received = storageHandler.readFromFileAppDir("smartcardRunTest.txt");

        //TODO!
        Log.i(TAG, "Received and sent is equal: " + message.equals(received));


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                outputField.setText(totalTime + " seconds");
            }
        });

    }
}
