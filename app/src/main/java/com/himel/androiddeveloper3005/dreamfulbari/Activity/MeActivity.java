package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Users;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import com.himel.androiddeveloper3005.dreamfulbari.Util.AdUtils;
import com.himel.androiddeveloper3005.dreamfulbari.Util.AppUtils;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import id.zelory.compressor.Compressor;

public class MeActivity extends BaseActivity {

    private DatabaseReference mDatabaseRef,mDatabaseVisitorRef, mDatabaseTotalFriendsRef;
    private FirebaseAuth mAuth;
    private ArrayList<Users> mUsers;
    private ImageView mUsersImageView,change_imageview;
    private TextView mName,mTotalFriends;
    private String mUID,name,image;
    private Context mContext;
    private Button mStatusChange,mPhotoChange;
    private ProgressDialog mProgressDialog_Change_Status,mProgressDialog_Change_Image;
    public static final int GALLERY_REQUEST = 1;
    private Bitmap thumb_bitmap,bitmap;
    private File thump_filepath;
    private Uri mImageUri,resultUri;
    private StorageReference mStorageReference;


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
        setContentView(R.layout.activity_me);
        initView();
        initVariable();
        initFirebase();
        getUserData();
        getToolbar();
        enableBackButton();
        setToolbarTitle("Profile");
        clickListenner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdUtils.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adView));
    }

    private void clickListenner() {
        mStatusChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MeActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.change_status, null);
                EditText change_et =  mView.findViewById(R.id.status_et);
                Button change_button =  mView.findViewById(R.id.change_btn);

                mBuilder.setView(mView);
                //progress Dialog
                mProgressDialog_Change_Status = new ProgressDialog(MeActivity.this);
                mProgressDialog_Change_Status.setTitle("Loading..");
                mProgressDialog_Change_Status.setMessage("Please wait ....");
                mProgressDialog_Change_Status.setCanceledOnTouchOutside(false);


                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                change_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String status = change_et.getText().toString();
                        mProgressDialog_Change_Status.show();
                        if (!TextUtils.isEmpty(status)){
                            HashMap updateData = new HashMap<>();
                            updateData.put("status", status);
                            mDatabaseRef.child(mUID).updateChildren(updateData).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        mProgressDialog_Change_Status.dismiss();

                                    } else {
                                        Toast.makeText(MeActivity.this, "There Was An Error.Please Wait... ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }else {
                            Toast.makeText(MeActivity.this, "You need to fill all field.", Toast.LENGTH_LONG).show();

                        }
                    }
                });

            }
        });
        mPhotoChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MeActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.change_photo, null);
                change_imageview =  mView.findViewById(R.id.change_imageView);
                Button change_image_button =  mView.findViewById(R.id.change_image_btn);
                mBuilder.setView(mView);
                //progress Dialog
                mProgressDialog_Change_Image = new ProgressDialog(MeActivity.this);
                mProgressDialog_Change_Image.setTitle("Loading..");
                mProgressDialog_Change_Image.setMessage("Please wait ....");
                mProgressDialog_Change_Image.setCanceledOnTouchOutside(false);

                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                change_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        galleryIntent.setType("image/*");
                        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), GALLERY_REQUEST);
                    }
                });

                change_image_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProgressDialog_Change_Image.show();
                        mStorageReference.child(Constans.USER_IMAGE_STOREAGE_PATH).child(mUID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete()){
                                    Toast.makeText(MeActivity.this, "Image Deleted.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,60,byteArrayOutputStream);
                        final byte[] thumb_byte = byteArrayOutputStream.toByteArray();

                        StorageReference filePath = mStorageReference.child(Constans.USER_IMAGE_STOREAGE_PATH).child(mUID);
                        UploadTask uploadTask = filePath.putBytes(thumb_byte);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final  String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                HashMap updateData = new HashMap<>();
                                updateData.put("imageUri",downloadUri);
                                mDatabaseRef.child(mUID).updateChildren(updateData).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()){
                                            mProgressDialog_Change_Image.dismiss();
                                        }
                                        else {
                                            Toast.makeText(MeActivity.this, "There Was An Error.Please Wait... "
                                                    , Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                });

                            }
                        });
                    }
                });


            }
        });
    }



    private void getUserData() {
        mUID = mAuth.getCurrentUser().getUid();
        mDatabaseRef.child(mUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = (String) dataSnapshot.child(Constans.USER_NAME).getValue();
                image = (String) dataSnapshot.child(Constans.USER_IMAGE).getValue();
                Glide.with(mContext).load(image)
                        .into(mUsersImageView);
                mName.setText(name);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //count user post
        mDatabaseTotalFriendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long number = dataSnapshot.child(mAuth.getCurrentUser().getUid()).getChildrenCount();
                if (number == null){
                    mTotalFriends.setText("0");
                }
                else {
                    mTotalFriends.setText("Total Friends -"+" "+number );
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void initFirebase() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constans.USER_DATABSE_PATH);
        mDatabaseTotalFriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        mAuth = FirebaseAuth.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();

    }

    private void initVariable() {
        mContext = getApplicationContext();

    }

    private void initView() {
        mName = findViewById(R.id.name);
        mUsersImageView = findViewById(R.id.round_user_image);
        mTotalFriends = findViewById(R.id.total_friend);
        mPhotoChange =  findViewById(R.id.change_image);
        mStatusChange =  findViewById(R.id.change_status);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {

            mImageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                change_imageview.setImageBitmap(bitmap);
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
}
