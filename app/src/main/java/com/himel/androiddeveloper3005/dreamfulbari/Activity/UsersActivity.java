package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.Model.User;
import com.himel.androiddeveloper3005.dreamfulbari.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView mUsersList;
    private DatabaseReference mDatabaseReference,mRootRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        intitView();
        initFireBase();

    }

    private void initFireBase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Constans.USER_DATABSE_PATH);
        mRootRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<User,UsersViewHolder>firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<User, UsersViewHolder>(
                        User.class,
                        R.layout.users_single_layout,
                        UsersViewHolder.class,
                        mDatabaseReference

                ) {
                    @Override
                    protected void populateViewHolder(UsersViewHolder viewHolder, User users, int position) {
                        viewHolder.setname(users.getName());
                        viewHolder.setstatus(users.getStatus());
                        viewHolder.setimage(getApplicationContext(),users.getImage());
                        String uid = getRef(position).getKey();
                        mRootRef.child(Constans.USER_DATABSE_PATH).child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String online = dataSnapshot.child("online").getValue().toString();
                                if (online.equals("true")){
                                    viewHolder.onlineIV.setVisibility(View.VISIBLE);

                                }else {
                                    viewHolder.onlineIV.setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent profile_intent = new Intent(UsersActivity.this, MessengerUserProfileActivity.class);
                                profile_intent.putExtra("UID",uid);
                                startActivity(profile_intent);

                            }
                        });

                    }
                };
        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class UsersViewHolder extends  RecyclerView.ViewHolder{
        View mView;
        ImageView onlineIV;

        public UsersViewHolder(View view) {
            super(view);
            mView = view;
            onlineIV = mView.findViewById(R.id.user_single_online_icon);
        }
        public void setname(String name){
            TextView user_name = mView.findViewById(R.id.user_single_name);
            user_name.setText(name);
        }
        public void setstatus(String status){
            TextView user_status = mView.findViewById(R.id.user_single_status);
            user_status.setText(status);
        }
        public void setimage(Context ctx,String image){
            CircleImageView user_image = mView.findViewById(R.id.circleImageView);
            Glide.with(ctx).load(image)
                    .into(user_image);

        }

    }

    private void intitView() {
        //toolBar
        mToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(UsersActivity.this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        //recyclerview
        mUsersList = findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(layoutManager);

    }
}