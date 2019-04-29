package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.google.firebase.auth.FirebaseAuth;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import com.himel.androiddeveloper3005.dreamfulbari.Util.CountryData;

public class PhoneAuthActivity extends AppCompatActivity {
    private Spinner mSpinner;
    private EditText mNumberEditText;
    private Button mContinue;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (mAuth.getCurrentUser() != null) {
                    startActivity(new Intent(PhoneAuthActivity.this, HomePageActivity.class));
                    finish();
                }

            }
        };

        setContentView(R.layout.activity_phone_auth);

        mAuth = FirebaseAuth.getInstance();
        mContinue = findViewById(R.id.next_btn);
        mNumberEditText = findViewById(R.id.edittext_phone);
        mSpinner = findViewById(R.id.spinnerCountries);
        mToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);
        mSpinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,
                CountryData.countryNames));

        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = CountryData.countryAreaCodes[mSpinner.getSelectedItemPosition()];
                String number = mNumberEditText.getText().toString().trim();
                if (number.isEmpty() || number.length()<10){
                    mNumberEditText.setError("Valid number is required");
                    mNumberEditText.requestFocus();
                    return;
                }
                String phoneNumber = "+"+ code +number;

                Intent mIntent = new Intent(getApplicationContext(),
                        VerifyOTPCode_Activity.class);
                mIntent.putExtra("phoneNumber",phoneNumber);
                startActivity(mIntent);

            }
        });


    }

}
