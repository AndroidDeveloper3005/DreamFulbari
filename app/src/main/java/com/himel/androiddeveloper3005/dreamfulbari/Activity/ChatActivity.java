package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himel.androiddeveloper3005.dreamfulbari.Adapter.ViewPagerAdapter;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.Fragnent.ChatsFragment;
import com.himel.androiddeveloper3005.dreamfulbari.Fragnent.UsersFragment;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Users;
import com.himel.androiddeveloper3005.dreamfulbari.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseRef;
    private FirebaseUser mAuth;
    private Toolbar mToolbar;
    private CircleImageView mUserImage;
    private TextView mUserName;
    private Context mContext;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        intiView();
        fireBase();
        getDatabaseValue();
    }

    private void getDatabaseValue() {
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child(Constans.USER_NAME).getValue().toString().trim();
                String image = dataSnapshot.child(Constans.USER_IMAGE).getValue().toString().trim();
                //Users users = new Users();

                mUserName.setText(name);
                Glide.with(mContext).load(image)
                        .into(mUserImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fireBase() {
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(Constans.USER_DATABSE_PATH).child(mAuth.getUid());
    }

    private void intiView() {
        mContext = getApplicationContext();
        mToolbar = findViewById(R.id.toolBar);
        mUserImage = findViewById(R.id.user_profile_imageView);
        mUserName = findViewById(R.id.user_name);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        mTabLayout = findViewById(R.id.tablayout);
        mViewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        mViewPagerAdapter.addFragment(new ChatsFragment(),"Chats");
        mViewPagerAdapter.addFragment(new UsersFragment(),"Users");

        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);




    }
}
