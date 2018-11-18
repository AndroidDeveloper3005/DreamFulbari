package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.himel.androiddeveloper3005.dreamfulbari.R;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener{
    private EditText inputEmail;
    private Button btnReset, btnBack;
    private FirebaseAuth mAuth;
    private ProgressBar bar;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initFireBaseAuth();
        initListener();


    }

    private void initListener() {
        btnBack.setOnClickListener(this);
        btnReset.setOnClickListener(this);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    public void initView() {
        setContentView(R.layout.activity_reset_password);
        bar = findViewById(R.id.progressBar);
        inputEmail = (EditText) findViewById(R.id.email);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        btnBack = (Button) findViewById(R.id.btn_back);
        mToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    public void initFireBaseAuth(){
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v) {
        if (v == btnBack){
            finish();
        }
        else if (v == btnReset){
            bar.setVisibility(View.VISIBLE);
            passwordReset();
        }
    }

    private void passwordReset() {
        String email = inputEmail.getText().toString().trim();
        if (!TextUtils.isEmpty(email)){
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPasswordActivity.this
                                        , "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ResetPasswordActivity.this
                                        , "Failed to send reset details_student!", Toast.LENGTH_SHORT).show();
                            }

                            bar.setVisibility(View.GONE);
                        }
                    });
        }
    }
}
