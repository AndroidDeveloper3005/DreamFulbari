package com.himel.androiddeveloper3005.dreamfulbari.Fragnent;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himel.androiddeveloper3005.dreamfulbari.Adapter.UsersAdapter;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.Model.User;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Users;
import com.himel.androiddeveloper3005.dreamfulbari.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private UsersAdapter mAdapter;
    private ArrayList<User>mUsers;
    private DatabaseReference mDatabaseRef;
    private FirebaseUser mAuth;


    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_users, container, false);

        mUsers = new ArrayList<>();
        readUsers();

        mRecyclerView = view.findViewById(R.id.users_chat_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return view;
    }

    private void readUsers() {
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(Constans.USER_DATABSE_PATH);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    String userID = snapshot.child(Constans.UID).getValue().toString();
                    String userName = snapshot.child(Constans.USER_NAME).getValue().toString();
                    String phone = snapshot.child(Constans.USER_PHONE).getValue().toString();
                    String userImage = snapshot.child(Constans.USER_IMAGE).getValue().toString();
                    String bloodGroup =snapshot.child(Constans.BLOODGROUP).getValue().toString();
                    String confirmDonerStatus = snapshot.child(Constans.BLOODDONER).getValue().toString();
                    String location =snapshot.child(Constans.CURRENT_LOCATION).getValue().toString();
                    User user = new User(userID,userName,phone,userImage,bloodGroup,confirmDonerStatus,location);

                    assert user !=null;
                    assert mAuth !=null;
                    if (!user.getId().equals(mAuth.getUid())){
                        mUsers.add(user);
                    }
                }

                //set adapter
                mAdapter = new UsersAdapter(getActivity(),mUsers);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
