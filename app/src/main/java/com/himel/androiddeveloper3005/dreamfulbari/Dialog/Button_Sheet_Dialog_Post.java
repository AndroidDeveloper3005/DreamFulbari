package com.himel.androiddeveloper3005.dreamfulbari.Dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.R;

public class Button_Sheet_Dialog_Post extends BottomSheetDialogFragment {
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private Button delete,edit;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.button_sheet_post_layout,container,false);
        String post_id = getArguments().getString("post_key");
        delete = mView.findViewById(R.id.delete);
        edit = mView.findViewById(R.id.edit);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef =FirebaseDatabase.getInstance().getReference().child(Constans.POST_DATABSE_PATH);

        mDatabaseRef.child(post_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uid = (String) dataSnapshot.child(Constans.UID).getValue();
                if (mAuth.getCurrentUser().getUid().equals(uid)){
                    delete.setVisibility(View.VISIBLE);
                    edit.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return mView;
    }
}
