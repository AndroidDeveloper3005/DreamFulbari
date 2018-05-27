package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PostActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int GALLERY_REQUEST = 1;
    private ImageView selectImageButton;
    private EditText postTitle,postDescription;
    private Button postSubmitBtn;
    private Uri mImageUri = null;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseRef,mDatabaseUserRef;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseReferenceUser;
    private String postRandomKey = null,saveDate,saveTime,current_uid;
    private String currentUserID,currentEmail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFireBase();
        checkUser();
        setContentView(R.layout.activity_post);
        selectImageButton = findViewById(R.id.addimage_ImageView);

        initView();

        selectImageButton.setOnClickListener(this);
        postSubmitBtn.setOnClickListener(this);
        progressBar.setVisibility(View.GONE);

    }

    private void checkUser() {

        mDatabaseUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(Constans.USER_DATABSE_PATH).child(currentUserID).exists()){

                    Intent checkAccountExistOrNot = new Intent(getApplicationContext(),UserAccountSetupActivity.class);
                    startActivity(checkAccountExistOrNot);
                    finish();


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v== selectImageButton){
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), GALLERY_REQUEST);

        }
        else if (v==postSubmitBtn){
            //setProgressbar(progress);

            progressBar.setVisibility(View.VISIBLE);
            startPosting();

        }

    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {

            mImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                // Log.d(TAG, String.valueOf(bitmap));


                selectImageButton.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void startPosting() {
        //progressBar.setVisibility(View.VISIBLE);


        final String titleValue = postTitle.getText().toString().trim();
        final String descriptionValue = postDescription.getText().toString().trim();
        current_uid = mAuth.getCurrentUser().getUid().toString();
        Calendar getDate = Calendar.getInstance();
        SimpleDateFormat curentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveDate = curentDate.format(getDate.getTime());

        Calendar get_Time = Calendar.getInstance();
        SimpleDateFormat curentTime = new SimpleDateFormat("HH:mm");
        saveTime = curentTime.format(get_Time.getTime());
        postRandomKey = current_uid + saveDate +saveTime ;


        if (!TextUtils.isEmpty(titleValue) &&  !TextUtils.isEmpty(descriptionValue) &&  mImageUri !=null){

            StorageReference filePath = mStorageReference.child(Constans.POST_STOREAGE_PATH).child(mImageUri.getLastPathSegment());
            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                  final  String downloadUri = taskSnapshot.getDownloadUrl().toString();

                  //savePostInfo();

                  final  DatabaseReference newPost = mDatabaseRef.child(postRandomKey);


                    mDatabaseReferenceUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            newPost.child(Constans.TITLE).setValue(titleValue);
                            newPost.child(Constans.DESCRITION).setValue(descriptionValue);
                            newPost.child(Constans.IMAGE_URI).setValue(downloadUri);
                            newPost.child(Constans.UID).setValue(mCurrentUser.getUid());
                            newPost.child("date").setValue(saveDate);
                            newPost.child("time").setValue(saveTime);
                            newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        startActivity(new Intent(PostActivity.this,MainActivity.class));
                                    }
                                    else {
                                        Toast.makeText(PostActivity.this, "Your Post Does Not Stored.! ", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                            newPost.child("userImage").setValue(dataSnapshot.child("image").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        startActivity(new Intent(PostActivity.this,MainActivity.class));
                                    }
                                    else {
                                        Toast.makeText(PostActivity.this, "Your Post Does Not Stored.! ", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    progressBar.setVisibility(View.GONE);


                }
            });


        }

    }


    public void initView(){
        postTitle = findViewById(R.id.post_Title_editText);
        postDescription = findViewById(R.id.post_Discription_editText);
        postSubmitBtn = findViewById(R.id.post_Button);
        linearLayout = findViewById(R.id.conslayout);
        progressBar = findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.VISIBLE);


    }
    public void initFireBase(){
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid().toString();
        currentEmail = mAuth.getCurrentUser().getEmail().toString();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseReferenceUser =FirebaseDatabase.getInstance().getReference().child(Constans.USER_DATABSE_PATH).child(mCurrentUser.getUid());
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constans.POST_DATABSE_PATH);
        mDatabaseUserRef = FirebaseDatabase.getInstance().getReference();

    }





}

