package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.R;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText inputEmail, inputPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ProgressBar bar;
    private Button registrationButton, loginButton, resetButton;
    private Toolbar mToolbar;
    private CoordinatorLayout mCoordinatorLayout;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFireBaseAuth();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (mAuth.getCurrentUser() != null) {
                    startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
                    finish();
                }

            }
        };


        setContentView(R.layout.activity_login);
        initView();
        //getToolbar();








    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onClick(View v) {
        if (v == registrationButton){
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
        }
        else if (v == loginButton){
            bar.setVisibility(View.VISIBLE);
            loginMethod();

        }
        else  if (v == resetButton){
            startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
        }


    }

    private void loginMethod() {

        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        //get token
                        String deviceTokenId = FirebaseInstanceId.getInstance().getToken();
                        mDatabaseReference.child(mAuth.getUid()).child("device_token").setValue(deviceTokenId);
                        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                        bar.setVisibility(View.GONE);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Please Enter Correct Email And Password", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } else {
            Snackbar mSnackbar = Snackbar.make(mCoordinatorLayout,"You Need To Provide Your Email And Password"
                    ,Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            // Changing message text color
            mSnackbar.setActionTextColor(Color.RED);
            // Changing action button text color
            View sbView = mSnackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            mSnackbar.show();
        }
    }


    public void initView() {
        bar = findViewById(R.id.progressBar);
        loginButton = findViewById(R.id.login_button);
        registrationButton = findViewById(R.id.btn_signup);
        resetButton = findViewById(R.id.reset_password);
        inputEmail = findViewById(R.id.email_edittext);
        inputPassword = findViewById(R.id.password_edittext);
        loginButton.setOnClickListener(this);
        registrationButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        mToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);
        mCoordinatorLayout = findViewById(R.id.mCoordinatorLayout);




    }

    public void initFireBaseAuth(){
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Constans.USER_DATABSE_PATH);


    }
}
