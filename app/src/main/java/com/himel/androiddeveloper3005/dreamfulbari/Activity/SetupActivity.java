package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends BaseActivity implements View.OnClickListener {
    private CircleImageView user_setUp_imageView;
    private EditText user_name,user_address,user_profession,user_phone;
    private Button setup_button;
    public static final int GALLERY_REQUEST = 1;
    private Uri mImageUri = null;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        initView();
        getToolbar();
        initFireBase();


    }

    private void initView(){
        user_name = findViewById(R.id.userName_editText);
        user_address = findViewById(R.id.userAddress_editText);
        user_profession = findViewById(R.id.userProfession_editText);
        user_phone = findViewById(R.id.userPhone_editText);
        user_setUp_imageView = findViewById(R.id.user_profile_imageView);
        setup_button = findViewById(R.id.userSetup_button);
        user_setUp_imageView.setOnClickListener(this);
        setup_button.setOnClickListener(this);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);


    }

    public void initFireBase(){
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constans.USER_DATABSE_PATH);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {

        if (v == user_setUp_imageView){
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), GALLERY_REQUEST);
        }

        else if (v == setup_button){
            mProgressBar.setVisibility(View.VISIBLE);
            startsetupaccount();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mImageUri = data.getData();

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
            // Log.d(TAG, String.valueOf(bitmap));


            user_setUp_imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void startsetupaccount() {

        final   String name = user_name.getText().toString().trim();
        final   String address = user_address.getText().toString().trim();
        final   String profession = user_profession.getText().toString().trim();
        final   String phone = user_phone.getText().toString().trim();

        final  String user_id = mAuth.getCurrentUser().getUid().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(address) && !TextUtils.isEmpty(profession) && !TextUtils.isEmpty(phone) && mImageUri != null){

            StorageReference filePath = mStorageReference.child(Constans.USER_IMAGE_STOREAGE_PATH).child(mImageUri.getLastPathSegment());
            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    mDatabaseRef.child(user_id).child(Constans.USER_NAME).setValue(name);
                    mDatabaseRef.child(user_id).child(Constans.USER_ADDRESS).setValue(address);
                    mDatabaseRef.child(user_id).child(Constans.USER_PROFESSION).setValue(profession);
                    mDatabaseRef.child(user_id).child(Constans.USER_PHONE).setValue(phone);
                    mDatabaseRef.child(user_id).child(Constans.USER_IMAGE).setValue(downloadUri);

                    mProgressBar.setVisibility(View.GONE);


                    finish();

                    Intent mainIntent = new Intent(SetupActivity.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                }
            });

        }
    }
}
