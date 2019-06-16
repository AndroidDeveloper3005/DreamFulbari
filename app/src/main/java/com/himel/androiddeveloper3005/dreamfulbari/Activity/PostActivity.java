package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
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
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import id.zelory.compressor.Compressor;

public class PostActivity extends BaseActivity implements View.OnClickListener{
    public static final int GALLERY_REQUEST = 1;
    private ImageView selectImageButton;
    private EditText postTitle,postDescription;
    private Button postSubmitBtn;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseRef,mDatabaseUserRef,mDatabaseRefPostCount;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseReferenceUser;
    private String postRandomKey = null,saveDate,saveTime,current_uid;
    private String currentUserID,currentEmail;
    private Bitmap thumb_bitmap,bitmap;
    private File thump_filepath;
    private Uri mImageUri,resultUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFireBase();
        checkUser();
        setContentView(R.layout.activity_post);
        initView();
        initListener();
        getToolbar();
        enableBackButton();
        setToolbarTitle("Posts");


    }

    private void initListener() {

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                selectImageButton.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            CropImage.activity(mImageUri)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                thump_filepath = new File(resultUri.getPath());
                try {
                    //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);

                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(300)
                            .setMaxHeight(300)
                            .setQuality(50)
                            .compressToBitmap(thump_filepath);


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
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
        SimpleDateFormat curentTime = new SimpleDateFormat("hh:mm a");
        saveTime = curentTime.format(get_Time.getTime());
        postRandomKey = current_uid + saveDate +saveTime ;


        if (!TextUtils.isEmpty(titleValue) &&  !TextUtils.isEmpty(descriptionValue) &&  mImageUri !=null){

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,60,byteArrayOutputStream);
            final byte[] thumb_byte = byteArrayOutputStream.toByteArray();

            StorageReference filePath = mStorageReference.child(Constans.POST_STOREAGE_PATH).child(mImageUri.getLastPathSegment());
            UploadTask uploadTask = filePath.putBytes(thumb_byte);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                  final  String downloadUri = taskSnapshot.getDownloadUrl().toString();

                  //savePostInfo();

                  final  DatabaseReference newPost = mDatabaseRef.child(postRandomKey);

                  //store user how many  post
                  final  DatabaseReference post_count = mDatabaseRefPostCount.child(mAuth.getCurrentUser().getUid());
                  post_count.child(postRandomKey).setValue("0");


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
                                        startActivity(new Intent(PostActivity.this,NewsActivity.class));
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
                                        startActivity(new Intent(PostActivity.this,NewsActivity.class));
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


        }else {
            Toast.makeText(this, "Fill all field first.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }

    }


    public void initView(){
        selectImageButton = findViewById(R.id.addimage_ImageView);
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
        mDatabaseRefPostCount = FirebaseDatabase.getInstance().getReference().child(Constans.USER_POST_COUNT_PATH);

    }





}

