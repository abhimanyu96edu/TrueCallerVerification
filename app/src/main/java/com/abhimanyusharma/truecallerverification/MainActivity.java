package com.abhimanyusharma.truecallerverification;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.truecaller.android.sdk.ITrueCallback;
import com.truecaller.android.sdk.TrueButton;
import com.truecaller.android.sdk.TrueClient;
import com.truecaller.android.sdk.TrueError;
import com.truecaller.android.sdk.TrueProfile;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ITrueCallback, View.OnClickListener{

    EditText name, email, number;
    TrueClient mTrueClient;
    private String mTruecallerRequestNonce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (EditText) findViewById(R.id.name);
        number = (EditText) findViewById(R.id.number);
        email = (EditText) findViewById(R.id.email);

        TrueButton trueButton = (TrueButton) findViewById(R.id.com_truecaller_android_sdk_truebutton);
        boolean usable = trueButton.isUsable();

        if (usable) {
            mTrueClient = new TrueClient(this, this);
            trueButton.setTrueClient(mTrueClient);
        } else {
            trueButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTruecallerRequestNonce = mTrueClient.generateRequestNonce();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (null != mTrueClient && mTrueClient.onActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccesProfileShared(@NonNull TrueProfile trueProfile) {

        final String fullName = trueProfile.firstName + " " + trueProfile.lastName;
        name.setText(fullName);
        number.setText(trueProfile.phoneNumber);
        email.setText(trueProfile.email);

        if (mTruecallerRequestNonce.equals(trueProfile.requestNonce)) {
            Toast.makeText(this, "The request nonce matches", Toast.LENGTH_SHORT).show();
        }
        Intent i= new Intent(this, VerificationActivity.class);
        i.putExtra("number",trueProfile.phoneNumber);
        i.putExtra("email",trueProfile.email);
        startActivity(i);

    }

    @Override
    public void onFailureProfileShared(@NonNull TrueError trueError) {

        Toast.makeText(this, "Failed sharing - Reason: " + trueError.getErrorType(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onClick(View v) {

    }
}
