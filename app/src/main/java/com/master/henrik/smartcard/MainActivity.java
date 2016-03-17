package com.master.henrik.smartcard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.security.KeyStore;

public class MainActivity extends AppCompatActivity {

    Button payloadButton;
    Button cryptoButton;
    Button bindingButton;
    KeyStore ks;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupButtonNavigation();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void setupButtonNavigation(){
        //payload
        payloadButton = (Button)findViewById(R.id.btnPayload);
        payloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent PayloadIntent = new Intent(getApplicationContext(), PayloadActivity.class);
                startActivity(PayloadIntent);
            }
        });

        //Crypto
        cryptoButton = (Button)findViewById(R.id.btnCrypto);
        cryptoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cryptoIntent = new Intent(getApplicationContext(), CryptoActivity.class);
                startActivity(cryptoIntent);
            }
        });

        //Keys
        bindingButton = (Button)findViewById(R.id.btnBinding);
        bindingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bindingIntent = new Intent(getApplicationContext(), BindingActivity.class);
                startActivity(bindingIntent);
            }
        });
    }
}
