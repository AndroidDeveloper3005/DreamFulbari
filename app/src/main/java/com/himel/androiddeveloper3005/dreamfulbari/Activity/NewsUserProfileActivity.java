package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsUserProfileActivity extends BaseActivity {
    private DatabaseReference mDatabase,mDatabaseVisitorRef,mDatabasePostCountRef;
    private FirebaseAuth mAuth;
    private String post_key, uid, dateTime;
    private ImageView mUserImage;
    private CircleImageView mCircleImageView;
    private TextView mName, mEmail, mPhone, mVisitorCount, mPosts,mDetails;
    private String mUID, name, email, phone, image, saveDate, saveTime, current_loc,institute,details;
    private Context mContext;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        //get Data from Intent
        post_key = getIntent().getStringExtra("UID");

        initView();
        initFirebase();
        insertUserVisitor();
        getToolbar();
        enableBackButton();
        setToolbarTitle("Profile");

    }

    private void insertUserVisitor() {
        Calendar getDate = Calendar.getInstance();
        SimpleDateFormat curentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveDate = curentDate.format(getDate.getTime());

        Calendar get_Time = Calendar.getInstance();
        SimpleDateFormat curentTime = new SimpleDateFormat("hh:mm a");
        saveTime = curentTime.format(get_Time.getTime());

        dateTime = saveDate + saveTime;
        final DatabaseReference post_path = mDatabase.child(Constans.POST_DATABSE_PATH);
        post_path.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uid = (String) dataSnapshot.child(Constans.UID).getValue();
                //get User Data
                final DatabaseReference user_data = mDatabase.child(Constans.USER_DATABSE_PATH).child(uid);
                user_data.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        name = (String) dataSnapshot.child(Constans.USER_NAME).getValue();
                        email = (String) dataSnapshot.child(Constans.EMAIL).getValue();
                        phone = (String) dataSnapshot.child(Constans.USER_PHONE).getValue();
                        image = (String) dataSnapshot.child(Constans.USER_IMAGE).getValue();
                        institute = (String) dataSnapshot.child(Constans.INSTITUTE).getValue();
                        current_loc = (String) dataSnapshot.child(Constans.CURRENT_LOCATION).getValue();
                        details =  "Institute " + institute +"\n"+ "Present Address " + current_loc;
                        String phone_number = "+88"+phone;
                        mName.setText(name);
                        mEmail.setText(email);
                        mPhone.setText(phone_number);
                       // mDetails.setText(details);
                        Glide.with(mContext).load(image)
                                .into(mCircleImageView);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void initFirebase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();//.child(Constans.USER_VISITORS);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseVisitorRef = FirebaseDatabase.getInstance().getReference().child("Visitors");
        mDatabasePostCountRef = FirebaseDatabase.getInstance().getReference().child(Constans.USER_POST_COUNT_PATH);

    }

    private void initView() {
        mContext = getApplicationContext();
        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mPhone = findViewById(R.id.phone);
        mCircleImageView = findViewById(R.id.round_user_image);
        mVisitorCount = findViewById(R.id.numbers_visitor);
        mPosts = findViewById(R.id.number_posts);
        //mDetails = findViewById(R.id.details);


    }
}
