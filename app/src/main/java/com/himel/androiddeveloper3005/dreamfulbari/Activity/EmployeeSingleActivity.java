package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Employee;
import com.himel.androiddeveloper3005.dreamfulbari.R;

public class EmployeeSingleActivity extends BaseActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private TextView name,phone,email,blood,institute,address,profession;
    private ImageView callNumber;
    private ImageView employeeImage;
    private Employee employee;
    private String uid;
    private static final String TAG ="EmployeeSingleActivity";
    private static final int REQUEST_PHONE_CALL =1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_single);
        initVariable();
        showSingleStudent();
        getToolbar();
        enableBackButton();
        initFirebase();


        callNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(StudentSingleActivity.this, "Call Button Clicked", Toast.LENGTH_SHORT).show();

                Log.d(TAG,"   : Call Button Clicked");
                String phoneNumber = phone.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                if (ContextCompat.checkSelfPermission(EmployeeSingleActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EmployeeSingleActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                }
                else
                {
                    startActivity(callIntent);
                }

            }
        });


        mDatabaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String userGender = dataSnapshot.child("gender").getValue().toString();

                if (getIntent().getSerializableExtra("employeeSingle") != null) {
                    employee = (Employee) getIntent().getSerializableExtra("employeeSingle");
                    final String tempGender = employee.getGender().toString();


                    if (userGender.equals(tempGender)){
                        phone.setVisibility(View.VISIBLE);
                        callNumber.setVisibility(View.VISIBLE);
                    }

                }


                    /*else if (gender.equals("Male")){
                        phone.setVisibility(View.VISIBLE);
                        callNumber.setVisibility(View.VISIBLE);
                    }*/



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid().toString();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Constans.USER_DATABSE_PATH);


    }

    private void showSingleStudent() {
        if (getIntent().getSerializableExtra("employeeSingle") != null) {
            employee = (Employee) getIntent().getSerializableExtra("employeeSingle");
            final String employeeName = employee.getName().toString();
            final String parmanentAddress = employee.getAddress().toString();
            final String employeeEmail = employee.getEmail().toString();
            final String employeePhone = employee.getPhone().toString();
            final String organization = employee.getOrganization().toString();
            final String bloodGroup = employee.getBloodgroup().toString();
            final String employeeProfession = employee.getProfession().toString();
            final String sImage = employee.getImage().toString();
            final String gender = employee.getGender().toString();
            final String singleEmployeeinfo = "Address :\t"+ parmanentAddress + "\n" +"Email :\t" +employeeEmail
                    + "\n"+"Profession : \t" + employeeProfession + "\n" +"Organization :\t" +organization + "\n"
                    +"Blood Group:\t"+ bloodGroup + "\n"+"Gender : \t" +gender+ "\n"+"Phone : \t" +employeePhone;
            name.setText(employeeName );
            address.setText(singleEmployeeinfo);
            //phone.setText(employeePhone);
            Glide.with(getApplicationContext()).load(sImage).into(employeeImage);


        }
    }

    private void initVariable() {
        name = findViewById(R.id.employee_name_textview);
        //email = findViewById(R.id.email_textview);
        address = findViewById(R.id.employee_address_textview);
        //institute = findViewById(R.id.organization_name_textview);
        //profession = findViewById(R.id.profession_textview);
        //blood = findViewById(R.id.blood_group_textview);
        phone = findViewById(R.id.employee_phone_textview);
        callNumber = findViewById(R.id.employee_phonecall_imageview);
        employeeImage = findViewById(R.id.employee_image_view);


    }
}
