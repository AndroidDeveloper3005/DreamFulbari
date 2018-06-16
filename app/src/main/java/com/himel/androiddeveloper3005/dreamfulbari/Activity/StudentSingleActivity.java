package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Student;
import com.himel.androiddeveloper3005.dreamfulbari.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentSingleActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private TextView name, phone, email, blood, institute, address, profession;
    private ImageView callNumber;
    private ImageView studentImage;
    private Student student;
    private String uid, number;
    private static final int REQUEST_PHONE_CALL =1 ;
    private static final String TAG ="StudentSingleActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_single);
        initVariable();
        showSingleStudent();
        getToolbar();
        enableBackButton();
        initFirebase();


        mDatabaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String userGender = dataSnapshot.child("gender").getValue().toString();

                if (getIntent().getSerializableExtra("studentSingle") != null) {
                    student = (Student) getIntent().getSerializableExtra("studentSingle");
                    final String tempGender = student.getGender().toString();


                    if (userGender.equals(tempGender)) {
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

        callNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(StudentSingleActivity.this, "Call Button Clicked", Toast.LENGTH_SHORT).show();

                Log.d(TAG,"   : Call Button Clicked");
                String phoneNumber = phone.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                if (ContextCompat.checkSelfPermission(StudentSingleActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(StudentSingleActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                }
                else
                {
                    startActivity(callIntent);
                }

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
        if (getIntent().getSerializableExtra("studentSingle") != null) {
            student = (Student) getIntent().getSerializableExtra("studentSingle");
            final String studentName = student.getName().toString();
            final String parmanentAddress = student.getAddress().toString();
            final String studentEmail = student.getEmail().toString();
            final String studentPhone = student.getPhone().toString();
            final String organization = student.getOrganization().toString();
            final String bloodGroup = student.getBloodgroup().toString();
            final String studentProfession = student.getProfession().toString();
            final String sImage = student.getImage().toString();
            final String gender = student.getGender().toString();
            final String singleStudentinfo = "Address :\t"+ parmanentAddress + "\n" +"Email :\t" +studentEmail
                    + "\n"+"Profession : \t" + studentProfession + "\n" +"Organization :\t" +organization + "\n"
                    +"Blood Group:\t"+ bloodGroup + "\n"+"Gender : \t" +gender;
            name.setText(studentName );
            address.setText(singleStudentinfo);
            phone.setText(studentPhone);
            Glide.with(getApplicationContext()).load(sImage).into(studentImage);



        }
    }

    private void initVariable() {
        name = findViewById(R.id.studentname_textview);
        //email = findViewById(R.id.email_textview);
        address = findViewById(R.id.address_textview);
        //institute = findViewById(R.id.organization_name_textview);
        //profession = findViewById(R.id.profession_textview);
        //blood = findViewById(R.id.blood_group_textview);
        phone = findViewById(R.id.phone_textview);
        callNumber = findViewById(R.id.phonecall_imageview);
        studentImage = findViewById(R.id.student_image_view);



    }


    }

