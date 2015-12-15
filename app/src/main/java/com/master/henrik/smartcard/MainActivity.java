package com.master.henrik.smartcard;

import android.content.Intent;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyInfo;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

public class MainActivity extends AppCompatActivity {

    Button payloadButton;
    Button cryptoButton;
    Button keystoreButton;
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
        keystoreButton = (Button)findViewById(R.id.btnKeyStore);
        keystoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keystoreIntent = new Intent(getApplicationContext(), AndroidKeysActivity.class);
                startActivity(keystoreIntent);
            }
        });
    }
}
