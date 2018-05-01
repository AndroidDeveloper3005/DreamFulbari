package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.himel.androiddeveloper3005.dreamfulbari.R;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText inputEmail, inputPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ProgressBar bar;
    private Button registrationButton, loginButton, resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFireBaseAuth();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (mAuth.getCurrentUser() != null) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

            }
        };


        setContentView(R.layout.activity_login);
        initView();
        getToolbar();








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
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        bar.setVisibility(View.GONE);
                        startActivity(intent);
                        finish();
                    }

                }
            });
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




    }

    public void initFireBaseAuth(){
        mAuth = FirebaseAuth.getInstance();


    }
}
