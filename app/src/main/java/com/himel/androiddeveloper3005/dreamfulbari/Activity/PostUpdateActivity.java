package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.HashMap;

import id.zelory.compressor.Compressor;

public class PostUpdateActivity extends AppCompatActivity implements View.OnClickListener{
    private DatabaseReference mDatabaseLikesRef,mDatabaseBlogRef;
    private StorageReference mStorageReference;
    private String post_key,time,date;
    private ProgressDialog mProgressDialog;
    public static final int GALLERY_REQUEST = 1;
    private ImageView selectImageButton;
    private EditText postDescription;
    private Button postSubmitBtn;
    private Bitmap thumb_bitmap,bitmap;
    private File thump_filepath;
    private Uri mImageUri,resultUri;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private Toolbar mToolbar;
    private ActionBar actionBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_update);
        getData();
        initView();
        firebase();
        onClickListener();
    }

    private void getData() {
        post_key = getIntent().getStringExtra("post_key");

    }

    private void onClickListener() {
        selectImageButton.setOnClickListener(this);
        postSubmitBtn.setOnClickListener(this);
        progressBar.setVisibility(View.GONE);

    }

    private void firebase() {
        mDatabaseBlogRef = FirebaseDatabase.getInstance().getReference().child(Constans.POST_DATABSE_PATH);
        mDatabaseLikesRef = FirebaseDatabase.getInstance().getReference().child(Constans.LIKES);
        mStorageReference = FirebaseStorage.getInstance().getReference().child(Constans.POST_STOREAGE_PATH);
    }

    private void initView() {
        selectImageButton = findViewById(R.id.showimage_ImageView);
        postDescription = findViewById(R.id.post_Discription_editText);
        postSubmitBtn = findViewById(R.id.post_Button);
        linearLayout = findViewById(R.id.conslayout);
        progressBar = findViewById(R.id.progressBar);
        //toolbar
        mToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        // getSupportActionBar().setDiscription(mUserName);
        // add back arrow to toolbar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
        }
        getSupportActionBar().setTitle("News Information Update");

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
            //postnotification();
            progressBar.setVisibility(View.VISIBLE);
            removeImage();
            updatePost();



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
                            .setQuality(60)
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


    private void removeImage() {
        mStorageReference.child(post_key).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(PostUpdateActivity.this, "Image Deleted.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void updatePost() {
        Calendar getDate = Calendar.getInstance();
        SimpleDateFormat curentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        date = curentDate.format(getDate.getTime());

        Calendar get_Time = Calendar.getInstance();
        SimpleDateFormat curentTime = new SimpleDateFormat("hh:mm a");
        time = curentTime.format(get_Time.getTime());
        final String descriptionValue = postDescription.getText().toString().trim();

        if ( !TextUtils.isEmpty(descriptionValue) &&  mImageUri !=null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,60,byteArrayOutputStream);
            final byte[] thumb_byte = byteArrayOutputStream.toByteArray();

            StorageReference filePath = mStorageReference.child(Constans.POST_STOREAGE_PATH).child(post_key);
            UploadTask uploadTask = filePath.putBytes(thumb_byte);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final  String downloadUri = taskSnapshot.getDownloadUrl().toString();
                    HashMap updateData = new HashMap<>();
                    updateData.put("description",descriptionValue);
                    updateData.put("imageUri",downloadUri);
                    updateData.put("date",date);
                    updateData.put("time",time);


                    mDatabaseBlogRef.child(post_key).updateChildren(updateData)
                            .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);


                            }
                            else {
                                Toast.makeText(PostUpdateActivity.this, "There Was An Error.Please Wait... "
                                        , Toast.LENGTH_SHORT).show();

                            }

                        }
                    });


                }
            });

        }



    }
}
