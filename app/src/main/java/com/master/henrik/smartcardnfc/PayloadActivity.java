package com.master.henrik.smartcardnfc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.master.henrik.nfc.CardReaderFragment;
import com.master.henrik.shared.Converter;
import com.master.henrik.shared.StringGenerator;

public class PayloadActivity extends AppCompatActivity {
    final String TAG = "PayLoad";
    Button btnTransmit;
    EditText lblAID;
    TextView outputField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payload);
        setupFields();
        setupButtons();
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

        CardReaderFragment crf = new CardReaderFragment();
        crf.currentActivity = this; //Sender med context for å kunne skrive til output
        crf.AIDString = AID;


        String message = StringGenerator.generateRandomString(256); //Random generated message
        crf.transmitString = "8008000000" + Converter.StringToHex(message);



        crf.enableReaderMode();
        outputField.setText("Waiting for output from NFC...");
    }
}
