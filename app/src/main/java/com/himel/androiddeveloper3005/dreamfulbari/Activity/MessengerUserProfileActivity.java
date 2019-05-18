package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Users;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessengerUserProfileActivity extends BaseActivity {
    private DatabaseReference mUsersDatabase,mFriendsRequestDatabase,mDatabase,mFriendDatabase;
    private ArrayList<Users>mUsers;
    private ImageView mImageView;
    private TextView mName, mStatus,mTotalFriends;
    private String name,status,image;
    private Context mContext;
    private String user_id;
    private Button mSendRequestButton,mDeclineButton;
    private ProgressDialog mProgressDialog;
    private String mCurrent_state;
    private FirebaseUser mCurrentUser;

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
        setContentView(R.layout.activity_messenger_users_profile);
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
        // mUID = mAuth.getCurrentUser().getUid();
        mUsersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = (String) dataSnapshot.child(Constans.USER_NAME).getValue();
                status = (String) dataSnapshot.child(Constans.STATUS).getValue();
                image = (String) dataSnapshot.child(Constans.USER_IMAGE).getValue();
                /*Glide.with(mContext).load(image)
                        .into(mImageView);*/
                Picasso.get().load(image)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(mImageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(image)
                                        .into(mImageView);

                            }
                        });
                mName.setText(name);
                mStatus.setText(status);

                //friend list/request feature
                mFriendsRequestDatabase.child(mCurrentUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(user_id)){
                                    String request_type = dataSnapshot.child(user_id)
                                            .child("request_type").getValue().toString();
                                    if (request_type.equals("received")){
                                        mCurrent_state ="request_received";
                                        mSendRequestButton.setText("Accept Friend Request");

                                    }else if (request_type.equals("sent")){
                                        mCurrent_state = "request_sent";
                                        mSendRequestButton.setText("Cancel Friend Request");

                                    }
                                    mProgressDialog.dismiss();

                                }else {
                                    mFriendDatabase.child(mCurrentUser.getUid())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChild(user_id)){
                                                        mCurrent_state ="friends";
                                                        mSendRequestButton.setText("Unfriend this Person");
                                                    }
                                                    mProgressDialog.dismiss();

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                    mProgressDialog.dismiss();
                                                }
                                            });

                                }

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
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child(Constans.USER_DATABSE_PATH);
        mUsersDatabase.keepSynced(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFriendsRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friends_Request");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
    }

    private void initVariable() {
        mContext = getApplicationContext();
        user_id = getIntent().getStringExtra("UID");


    }

    private void initView() {
        mName = findViewById(R.id.name);
        mStatus = findViewById(R.id.status);
        mImageView = findViewById(R.id.round_user_image);
        mTotalFriends = findViewById(R.id.total_friends);
        mSendRequestButton = findViewById(R.id.friends_request_button);
        mDeclineButton = findViewById(R.id.decline_button);

        mCurrent_state ="not_friends";

        //dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load the user data..");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


        //listener
        mSendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSendRequestButton.setEnabled(false);

                // not friend State
                notFriendState();

                // cancel friend request State
                cancelFriendRequest();

                //request receive state
                requestReceivedState();

                //unfriend state
                unFriendState();


            }
        });


    }

    private void unFriendState() {
        if (mCurrent_state.equals("friends")){
            mFriendDatabase.child(mCurrentUser.getUid())
            .child(user_id)
            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mFriendDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mSendRequestButton.setEnabled(true);
                                    mCurrent_state ="not_friends";
                                    mSendRequestButton.setText("Send Friend Request");
                                }
                            });

                }
            })
            ;


        }


    }

    private void requestReceivedState() {
        if (mCurrent_state.equals("request_received")){
           final String currentData = DateFormat.getDateTimeInstance()
                    .format(new Date());
            mFriendDatabase.child(mCurrentUser.getUid())
                    .child(user_id).setValue(currentData)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mFriendDatabase.child(user_id).child(mCurrentUser.getUid())
                            .setValue(currentData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendsRequestDatabase.child(mCurrentUser.getUid())
                                            .child(user_id).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mFriendsRequestDatabase.child(user_id)
                                                            .child(mCurrentUser.getUid()).removeValue()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    mSendRequestButton.setEnabled(true);
                                                                    mCurrent_state ="friends";
                                                                    mSendRequestButton.setText("Unfriend this Person");

                                                                }
                                                            });
                                                }
                                            });



                                }
                            });

                }
            });

        }


    }

    private void cancelFriendRequest() {

        if (mCurrent_state.equals("request_sent")){
            mFriendsRequestDatabase.child(mCurrentUser.getUid())
                    .child(user_id).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendsRequestDatabase.child(user_id)
                                    .child(mCurrentUser.getUid()).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mSendRequestButton.setEnabled(true);
                                            mCurrent_state ="not_friends";
                                            mSendRequestButton.setText("Send Friend Request");
                                            Toast.makeText(MessengerUserProfileActivity.this,
                                                    "Request Cancel Successfully.",Toast.LENGTH_LONG).show();

                                        }
                                    });
                        }
                    });

        }


    }

    private void notFriendState() {

        if (mCurrent_state.equals("not_friends")){
            mFriendsRequestDatabase.child(mCurrentUser.getUid())
                    .child(user_id).child("request_type")
                    .setValue("send").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        mFriendsRequestDatabase.child(user_id).child(mCurrentUser.getUid())
                                .child("request_type")
                                .setValue("received")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mCurrent_state ="request_sent";
                                        mSendRequestButton.setText("Cancel Friend Request");
                                        Toast.makeText(MessengerUserProfileActivity.this,
                                                "Request Send Successfully.",Toast.LENGTH_LONG).show();


                                    }
                                });

                    }else {
                        Toast.makeText(MessengerUserProfileActivity.this,"Failed Sending Request."
                                ,Toast.LENGTH_LONG).show();
                    }
                    mSendRequestButton.setEnabled(true);


                }
            });


        }


    }
}
