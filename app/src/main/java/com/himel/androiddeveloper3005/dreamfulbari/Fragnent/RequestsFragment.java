package com.himel.androiddeveloper3005.dreamfulbari.Fragnent;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himel.androiddeveloper3005.dreamfulbari.Activity.MessengerUserProfileActivity;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.Model.FriendsRequest;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import com.himel.androiddeveloper3005.dreamfulbari.Util.AdUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {
    private RecyclerView mFriendsRequestList;
    private DatabaseReference mFriendsRequestDatabase,mFriendReqDatabase,mFriendDatabase,mRootRef;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View mMainView;
    private String mCurrent_state;
    private AdView mAdView;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);

        mFriendsRequestList = (RecyclerView) mMainView.findViewById(R.id.request_list);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mFriendsRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mFriendsRequestDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        mFriendsRequestList.setHasFixedSize(true);
        mFriendsRequestList.setLayoutManager(new LinearLayoutManager(getContext()));
        mCurrent_state = "not_friends";

        return mMainView;
    }


    @Override
    public void onResume() {
        super.onResume();
        AdUtils.getInstance(getContext()).showBannerAd((AdView)mMainView.findViewById(R.id.adView));
    }







    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<FriendsRequest,RequestViewHolder> requestFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FriendsRequest, RequestViewHolder>(
                FriendsRequest.class
                ,R.layout.request_single_item
                ,RequestViewHolder.class
                ,mFriendsRequestDatabase
        ) {
            @Override
            protected void populateViewHolder(RequestViewHolder viewHolder, FriendsRequest model, int position) {
                String request_type = model.getRequest_type().toString();
                final String list_user_id = getRef(position).getKey();



                if (request_type.equals("received")){
                    viewHolder.accept_btn.setVisibility(View.VISIBLE);
                    viewHolder.reject_btn.setVisibility(View.VISIBLE);
                    mCurrent_state = "req_received";
                }else {
                    viewHolder.reject_btn.setVisibility(View.VISIBLE);
                    mCurrent_state = "req_sent";

                }


                //get user status
                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child(Constans.USER_NAME).getValue().toString();
                        String userThumb = dataSnapshot.child(Constans.USER_IMAGE).getValue().toString();
                        String status = dataSnapshot.child(Constans.STATUS).getValue().toString();
                        viewHolder.setName(userName);
                        viewHolder.setUserImage(userThumb,getActivity());
                        viewHolder.setStatus(status);





                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //onclick
                viewHolder.accept_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mCurrent_state.equals("req_received")){
                            final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                            Map friendsMap = new HashMap();
                            friendsMap.put("Friends/" + mCurrent_user_id + "/" + list_user_id + "/date", currentDate);
                            friendsMap.put("Friends/" + list_user_id + "/"  +mCurrent_user_id + "/date", currentDate);
                            friendsMap.put("Friend_req/" + mCurrent_user_id + "/" + list_user_id, null);
                            friendsMap.put("Friend_req/" + list_user_id + "/" + mCurrent_user_id, null);

                            mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                    if(databaseError == null){
                                        mCurrent_state = "friends";


                                    } else {
                                        String error = databaseError.getMessage();
                                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

                                    }

                                }

                            });

                        }
                    }
                });

                viewHolder.reject_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mCurrent_state.equals("req_sent")){
                            mFriendReqDatabase.child(mCurrent_user_id).child(list_user_id).removeValue().addOnSuccessListener(
                                    new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendReqDatabase.child(list_user_id).child(mCurrent_user_id).removeValue().addOnSuccessListener(
                                                    new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            mCurrent_state = "not_friends";
                                                        }
                                                    }
                                            );

                                        }
                                    }
                            );

                        }else{
                            mFriendReqDatabase.child(mCurrent_user_id).child(list_user_id).removeValue().addOnSuccessListener(
                                    new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendReqDatabase.child(list_user_id).child(mCurrent_user_id).removeValue().addOnSuccessListener(
                                                    new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            mCurrent_state = "not_friends";
                                                        }
                                                    }
                                            );

                                        }
                                    }
                            );

                        }
                    }
                });




            }
        };

        //set adapter
        mFriendsRequestList.setAdapter(requestFirebaseRecyclerAdapter);


    }

    public static class RequestViewHolder extends  RecyclerView.ViewHolder{
        View mView;
        public ImageButton accept_btn,reject_btn;

        public RequestViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            accept_btn = mView.findViewById(R.id.accept_button);
            reject_btn = mView.findViewById(R.id.reject_button);

        }

        public void setName(String name){
            TextView userNameView = (TextView) mView.findViewById(R.id.request_user_name_text);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context ctx){
            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.request_user_image);

            Picasso.get().load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).into(userImageView, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(thumb_image).into(userImageView);

                }
            });

        }

        public void setStatus(String status){
            TextView userStatusView = (TextView) mView.findViewById(R.id.status_text);
            userStatusView.setText(status);

        }
    }
}
