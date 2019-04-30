package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.R;

import java.util.concurrent.TimeUnit;

public class VerifyOTPCode_Activity extends BaseActivity {
    private String verificationID;
    private EditText mCode;
    private Button mSignIn;
    private FirebaseAuth mAuth;
    private ProgressBar mProgressBar;
    private String currentUserID;
    private DatabaseReference mDatabaseUsers;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otpcode_);
        String mPhoneNumber = getIntent().getStringExtra("phoneNumber");

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child(Constans.USER_DATABSE_PATH);
        //view
        mCode = findViewById(R.id.edittext_code);
        mSignIn = findViewById(R.id.sign_in_btn);
        mProgressBar = findViewById(R.id.progressBar);
        getToolbar();
        setToolbarTitle("Authentication Code");
        dialog = new ProgressDialog(this);
        dialog.setMessage("Checking Account Information...");


        //method
        sendVerificationCode(mPhoneNumber);


        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = mCode.getText().toString().trim();
                if (code.isEmpty() || code.length()<6){
                    mCode.setError("Enter Code...");
                    mCode.requestFocus();
                    return;

                }
                mProgressBar.setVisibility(View.VISIBLE);
                verifyCode(code);


            }
        });


    }

    private  void verifyCode(String code){
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                verificationID,code);
        signInWithCredential(phoneAuthCredential);

    }

    private void signInWithCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            dialog.show();
                            currentUserID = mAuth.getCurrentUser().getUid().toString();
                            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(currentUserID)){
                                        dialog.dismiss();
                                        Intent intent = new Intent(getApplicationContext(),HomePageActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                    else {
                                        dialog.dismiss();
                                        Intent intent = new Intent(getApplicationContext(),UserAccountSetupActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });


                        }else {
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }

    private void sendVerificationCode(String number){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number
                ,60
                , TimeUnit.SECONDS
                ,TaskExecutors.MAIN_THREAD
                ,mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationID =s;

        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code !=null){
                mProgressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();

        }
    };

}
