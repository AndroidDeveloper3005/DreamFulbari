package com.himel.androiddeveloper3005.dreamfulbari.Fragnent;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.R;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    private DatabaseReference mDatabaseLikesRef,mDatabaseBlogRef;
    private StorageReference mStorageReference;
    private String post_key;
    private ProgressDialog mProgressDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.news_dialog, container, false);
        ConstraintLayout update = view.findViewById(R.id.update_button);
        ConstraintLayout delete = view.findViewById(R.id.delete_button);
        Bundle mArgs = getArguments();
        post_key = mArgs.getString("post_key");
        mDatabaseBlogRef = FirebaseDatabase.getInstance().getReference().child(Constans.POST_DATABSE_PATH);
        mDatabaseLikesRef = FirebaseDatabase.getInstance().getReference().child(Constans.LIKES);
        mStorageReference = FirebaseStorage.getInstance().getReference().child(Constans.POST_STOREAGE_PATH);
        //progress Dialog
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("Deleting User Data");
        mProgressDialog.setMessage("Please wait while we delete the user post..");
        mProgressDialog.setCanceledOnTouchOutside(false);


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mProgressDialog.show();
                dismiss();

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                delete_post();
                dismiss();

            }
        });

        return view;
    }

    private void delete_post() {
        mDatabaseBlogRef.child(post_key).removeValue();
        mDatabaseLikesRef.child(post_key).removeValue();
        mStorageReference.child(post_key).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mProgressDialog.dismiss();

            }
        });

    }


}
