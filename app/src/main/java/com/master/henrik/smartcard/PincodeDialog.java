package com.master.henrik.smartcard;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Henri on 07.03.2016.
 */
public class PincodeDialog extends DialogFragment {

    private EditText mPINCode;
    Button mButton;
    onSubmitListener mListener;
    int numberOfTries = -1;

    public PincodeDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pincodefragment, container);
        mPINCode = (EditText) view.findViewById(R.id.txt_your_PIN);
        mButton = (Button) view.findViewById(R.id.btnSubmit);
        getDialog().setTitle("Enter Pincode (" + numberOfTries + ")");

        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mListener.setOnSubmitListener(mPINCode.getText().toString());
                dismiss();
            }
        });

        return view;
    }

}
