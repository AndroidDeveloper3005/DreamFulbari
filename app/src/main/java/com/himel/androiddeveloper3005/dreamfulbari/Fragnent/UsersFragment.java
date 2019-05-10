package com.himel.androiddeveloper3005.dreamfulbari.Fragnent;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private List<Users>mUsers;
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

        mRecyclerView = view.findViewById(R.id.users_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();
        readUsers();

        return view;
    }

    private void readUsers() {
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(Constans.USER_DATABSE_PATH).child(mAuth.getUid());
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child(Constans.USER_NAME).getValue().toString().trim();
                String image = dataSnapshot.child(Constans.USER_IMAGE).getValue().toString().trim();
                //Users users = new Users();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
