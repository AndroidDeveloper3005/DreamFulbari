package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.himel.androiddeveloper3005.dreamfulbari.Model.Users;
import com.himel.androiddeveloper3005.dreamfulbari.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileActivity extends BaseActivity {
    private DatabaseReference mDatabaseRef,mDatabaseVisitorRef,mDatabasePostCountRef;
    private FirebaseAuth mAuth;
    private ArrayList<Users>mUsers;
    private ImageView mUserImage;
    private CircleImageView mCircleImageView;
    private TextView mName,mEmail,mPhone,mVisitorCount,mPosts;
    private String mUID,name,email,phone,image;
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
        setContentView(R.layout.activity_profile);
        initView();
        initVariable();
        initFirebase();
        getUserData();
        setDataInLayout();
        getToolbar();
        enableBackButton();
        setToolbarTitle("Profile");
    }

    private void setDataInLayout() {

    }

    private void getUserData() {
        mUID = mAuth.getCurrentUser().getUid();
        mDatabaseRef.child(mUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = (String) dataSnapshot.child(Constans.USER_NAME).getValue();
                phone = (String) dataSnapshot.child(Constans.USER_PHONE).getValue();
                email = (String) dataSnapshot.child(Constans.EMAIL).getValue();
                image = (String) dataSnapshot.child(Constans.USER_IMAGE).getValue();
                Glide.with(mContext).load(image)
                        .into(mCircleImageView);
                mName.setText(name);
                mEmail.setText(email);
                mPhone.setText(phone);
                //Toast.makeText(mContext, "Working", Toast.LENGTH_SHORT).show();



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //visitor count information
        mDatabaseVisitorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long number = dataSnapshot.child(mAuth.getCurrentUser().getUid()).getChildrenCount();
                if (number == null){
                    mVisitorCount.setText("0");
                }
                else {
                    mVisitorCount.setText(number +"");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //count user post
        mDatabasePostCountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long number = dataSnapshot.child(mAuth.getCurrentUser().getUid()).getChildrenCount();
                if (number == null){
                    mPosts.setText("0");
                }
                else {
                    mPosts.setText(number +"");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void initFirebase() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constans.USER_DATABSE_PATH);
        mDatabaseVisitorRef = FirebaseDatabase.getInstance().getReference().child("Visitors");
        mDatabasePostCountRef = FirebaseDatabase.getInstance().getReference().child(Constans.USER_POST_COUNT_PATH);
        mAuth = FirebaseAuth.getInstance();
    }

    private void initVariable() {
        mContext = getApplicationContext();

    }

    private void initView() {
        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mPhone = findViewById(R.id.phone);
        mCircleImageView = findViewById(R.id.round_user_image);
        mVisitorCount = findViewById(R.id.numbers_visitor);
        mPosts = findViewById(R.id.number_posts);


    }
}
