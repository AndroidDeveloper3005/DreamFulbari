package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.R;

public class RegistrationActivity extends BaseActivity implements View.OnClickListener{
    EditText userEmail,userPassword;
    Button signInbButton,signupButton;
    private ProgressBar bar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initView();
        initFireBaseAuth();




    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    public void initView() {
        bar = findViewById(R.id.progressBar);
        userEmail = findViewById(R.id.email);
        userPassword = findViewById(R.id.password);
        signInbButton = findViewById(R.id.sign_in_button);
        signupButton = findViewById(R.id.sign_up_button);
        signInbButton.setOnClickListener(this);
        signupButton.setOnClickListener(this);



    }

    public void initFireBaseAuth(){
        mAuth = FirebaseAuth.getInstance();




    }

    @Override
    public void onClick(View v) {
        if (v== signInbButton){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }
        else if (v== signupButton){
            bar.setVisibility(View.VISIBLE);
            registration();

        }
    }

    private void registration() {
       String email = userEmail.getText().toString().trim();
       String password = userPassword.getText().toString().trim();
       if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
           mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
               @Override
               public void onComplete(@NonNull Task<AuthResult> task) {
                   if (task.isSuccessful()) {
                       bar.setVisibility(View.GONE);


                       startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                       finish();

                   } else {
                       bar.setVisibility(View.GONE);


                       Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                               Toast.LENGTH_SHORT).show();

                   }

               }
           });
       }

    }
}
