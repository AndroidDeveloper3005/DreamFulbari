package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DonateBloodActivity extends AppCompatActivity {
    private EditText mConfirmField;
    private ImageButton mConfirmBtn;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private String randomKey = null,saveDate,saveTime,current_uid;
    private ProgressDialog dialog;

    private void intView() {
        mConfirmField = (EditText) findViewById(R.id.confirm_edittext);
        mConfirmBtn = (ImageButton) findViewById(R.id.confirm_btn);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait..");

    }
    private void initFirebase() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constans.BLOOD_DONER_DATABSE_PATH);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_blood);
        initFirebase();
        intView();
        setButtonClick();
    }

    private void confirmBloodDonate() {
       final String confirm = mConfirmField.getText().toString();

        current_uid = mAuth.getCurrentUser().getUid().toString();

        Calendar getDate = Calendar.getInstance();
        SimpleDateFormat curentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveDate = curentDate.format(getDate.getTime());

        Calendar get_Time = Calendar.getInstance();
        SimpleDateFormat curentTime = new SimpleDateFormat("HH:mm");
        saveTime = curentTime.format(get_Time.getTime());
        randomKey = current_uid + saveDate +saveTime ;

        if (!TextUtils.isEmpty(confirm)){
            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final  DatabaseReference c_donate = mDatabaseReference.child(randomKey);
                    c_donate.child(current_uid).setValue(confirm);
                    dialog.dismiss();


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        else {
            Toast.makeText(this, "Please Fill All field first.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }

    }


    private void setButtonClick() {
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                confirmBloodDonate();




            }
        });
    }

}
