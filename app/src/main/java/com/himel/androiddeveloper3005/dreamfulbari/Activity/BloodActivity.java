package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Users;
import com.himel.androiddeveloper3005.dreamfulbari.R;

public class BloodActivity extends AppCompatActivity {

    private EditText mSearchField;
    private ImageButton mSearchBtn;
    private RecyclerView mResultList;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood);
        initFirebase();
        intView();
        setButtonClick();
    }

    private void setButtonClick() {
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = mSearchField.getText().toString();

                firebaseUserSearch(searchText);

            }
        });
    }

    private void firebaseUserSearch(String searchText) {

        Toast.makeText(BloodActivity.this, "Started Search", Toast.LENGTH_LONG).show();

        Query firebaseSearchQuery = mUserDatabase.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                Users.class,
                R.layout.list_layout,
                UsersViewHolder.class,
                firebaseSearchQuery

        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users model, int position) {


                viewHolder.setDetails(getApplicationContext()
                        , model.getName(),
                       // model.getStatus(),
                        model.getImage());
                viewHolder.setStatus(getApplicationContext());

            }
        };

        mResultList.setAdapter(firebaseRecyclerAdapter);

    }

    // View Holder Class

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        DatabaseReference mDatabaseReference;
        FirebaseAuth mAuth;

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Constans.BLOOD_DONER_DATABSE_PATH);
            mAuth = FirebaseAuth.getInstance();

        }

        public void  setStatus(Context ctx){
            final TextView user_status = (TextView) mView.findViewById(R.id.status_text);
            final ImageView phoneCall = (ImageView) mView.findViewById(R.id.phone_call_imgeview);
            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())){
                        user_status.setText("Sorry ,Recently Donated My Blood.So I can not Donate my blood right now"
                                +"\nI can  Donate my blood after four month.");
                    }
                    else {
                        user_status.setText("You Can Call Me....");
                        phoneCall.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }



        public void setDetails(Context ctx, String userName, String userImage){

            TextView user_name = (TextView) mView.findViewById(R.id.name_text);

            ImageView user_image = (ImageView) mView.findViewById(R.id.profile_image);


            user_name.setText(userName);
            Glide.with(ctx).load(userImage).into(user_image);


        }




    }


    private void intView() {
        mSearchField = (EditText) findViewById(R.id.search_field);
        mSearchBtn = (ImageButton) findViewById(R.id.search_btn);
        mResultList = (RecyclerView) findViewById(R.id.result_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initFirebase() {
        mUserDatabase = FirebaseDatabase.getInstance().getReference("Users");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constans.BLOOD_DONER_DATABSE_PATH);
    }
}
