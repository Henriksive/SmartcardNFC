package com.master.henrik.smartcard;

import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Calendar;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

public class AndroidKeysActivity extends AppCompatActivity {
    KeyStore ks;
    private final String TAG = "KeyStore";
    private final String alias = "scotch";
    TextView outputField;
    Button btnShowKeys;
    Button btnStorageMode;
    PrivateKey k;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_keys);
        getSupportActionBar().setTitle("KeyStore");
        setupFields();
        setupButtons();
        setupKeyPair();
    }

    private void setupFields(){
        outputField = (TextView)findViewById(R.id.lblOutput);
    }

    private void setupButtons(){
        btnShowKeys = (Button)findViewById(R.id.btnShow);
        btnShowKeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeys();
            }
        });

        btnStorageMode = (Button)findViewById(R.id.btnStorageMode);
        btnStorageMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInsideSecureHardware();;
            }
        });

    }

    private void showKeys() {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(alias, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

            outputField.setText(privateKeyEntry.toString());
        }
        catch (Exception ex){
            outputField.setText(ex.toString());
        }
    }

    public void isInsideSecureHardware(){
        KeyInfo info;
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(alias, null);
            RSAPrivateKey prvkey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();
            PrivateKey key = (PrivateKey) prvkey;


            //KeyFactory factory = KeyFactory.getInstance(key.getAlgorithm(), "AndroidKeyStore");
            KeyFactory fa = KeyFactory.getInstance("RSA");
            PrivateKey pk = fa.generatePrivate(new RSAPrivateKeySpec(BigInteger.ONE, BigInteger.ONE));
            KeySpec kspec = fa.getKeySpec(pk, KeySpec.class);

            KeyInfo keyInfo;
            try {
                keyInfo = (KeyInfo) fa.getKeySpec(pk, KeyInfo.class);
            } catch (Exception e) {
                Log.d(TAG, "1: " + e.toString());
            }
            //info = kf.getKeySpec(s, KeyInfo.class);

        } catch (Exception e){
            Log.d(TAG,"2: " + e.toString());
        }

    }

    private void setupKeyPair() {
        try {
            ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null, null);
            if (!ks.containsAlias(alias)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(this)
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(spec);

                KeyPair keyPair = generator.generateKeyPair();
                k = keyPair.getPrivate();

            }
        } catch (Exception ex){
            Log.d(TAG, "ex1");
            Log.d(TAG, ex.toString());
            outputField.setText(ex.toString());
        }

        Log.d(TAG, "DONE.");
    }
}
