package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
    private TextView name,phone,email,blood,institute,address,profession;
    private ImageView callNumber;
    private ImageView studentImage;
    private Student student;
    private String uid;


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
            name.setText(studentName);
            address.setText(parmanentAddress);
            email.setText(studentEmail);
            phone.setText(studentPhone);
            institute.setText(organization);
            blood.setText(bloodGroup);
            profession.setText(studentProfession);
            Glide.with(getApplicationContext()).load(sImage).into(studentImage);


        }
    }

    private void initVariable() {
        name = findViewById(R.id.studentname_textview);
        email = findViewById(R.id.email_textview);
        address = findViewById(R.id.address_textview);
        institute = findViewById(R.id.organization_name_textview);
        profession = findViewById(R.id.profession_textview);
        blood = findViewById(R.id.blood_group_textview);
        phone = findViewById(R.id.phone_textview);
        callNumber = findViewById(R.id.phonecall_imageview);
        studentImage = findViewById(R.id.student_image_view);


    }
}
