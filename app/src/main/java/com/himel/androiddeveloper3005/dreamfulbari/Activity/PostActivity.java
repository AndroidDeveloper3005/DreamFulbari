package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.firebase.database.ServerValue;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import id.zelory.compressor.Compressor;

public class PostActivity extends BaseActivity implements View.OnClickListener{
    public static final int GALLERY_REQUEST = 1;
    private ImageView show_image;
    private EditText postTitle,postDescription;
    private Button postSubmitBtn;
    private StorageReference mStorageReference;
    private DatabaseReference mBlogDatabaseRef,mDatabaseUserRef,mDatabaseRefPostCount,mPostNotificationRef,mRootRef;
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
    private ArrayList<String> all_user;
    private String user_id ;
    private long post_counter = 0;
    private LinearLayout photo_layout;
    private Toolbar mToolbar;
    private ActionBar actionBar ;
    private TextView mPost_Text;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFireBase();
        checkUser();
        setContentView(R.layout.activity_post);
        initView();
        initListener();


        all_user = new ArrayList<String>();
        //store data for send notifications
        //first get all user id

        mRootRef.child(Constans.USER_DATABSE_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapShot:dataSnapshot.getChildren()) {
                    //final String user_id = snapShot.child("uid").getValue().toString();
                    all_user.add(snapShot.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mBlogDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    post_counter = dataSnapshot.getChildrenCount();
                }else {
                    post_counter = 0;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void initListener() {
        photo_layout.setOnClickListener(this);
        mPost_Text.setOnClickListener(this);
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
        if (v== photo_layout){
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), GALLERY_REQUEST);

        }
        else if (v==mPost_Text){
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
                show_image.setVisibility(View.VISIBLE);
                show_image.setImageBitmap(bitmap);
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


    private void startPosting() {
        progressBar.setVisibility(View.VISIBLE);
        final String descriptionValue = postDescription.getText().toString().trim();
        current_uid = mAuth.getCurrentUser().getUid().toString();

        Calendar getDate = Calendar.getInstance();
        SimpleDateFormat curentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveDate = curentDate.format(getDate.getTime());

        Calendar get_Time = Calendar.getInstance();
        SimpleDateFormat curentTime = new SimpleDateFormat("hh:mm a");
        saveTime = curentTime.format(get_Time.getTime());

        postRandomKey = current_uid + saveDate +saveTime ;


        if (!TextUtils.isEmpty(descriptionValue) &&  mImageUri !=null){

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,60,byteArrayOutputStream);
            final byte[] thumb_byte = byteArrayOutputStream.toByteArray();
            //String key = mBlogDatabaseRef.push().getKey();

            StorageReference filePath = mStorageReference.child(Constans.POST_STOREAGE_PATH).child(postRandomKey);
            UploadTask uploadTask = filePath.putBytes(thumb_byte);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                  final  String downloadUri = taskSnapshot.getDownloadUrl().toString();

                  final  DatabaseReference newPost = mBlogDatabaseRef.child(postRandomKey);

                    //get one by one data and store inside database
                    for (int i =0;i<all_user.size();i++) {

                        user_id = all_user.get(i);
                        if (!user_id.equals(current_uid)) {
                            DatabaseReference newNotificationref = mRootRef.child("notifications_post").child(user_id).push();
                            String newNotificationId = newNotificationref.getKey();

                            HashMap<String, String> notificationData = new HashMap<>();
                            notificationData.put("from", current_uid);
                            notificationData.put("type", "new post");

                            Map requestMap = new HashMap();
                            requestMap.put("notifications_post/" + user_id + "/" + newNotificationId, notificationData);

                            mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        Toast.makeText(PostActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();

                                    } else {

                                    }

                                }
                            });

                        }



                    }

                        mDatabaseReferenceUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child(Constans.DESCRITION).setValue(descriptionValue);
                            newPost.child(Constans.IMAGE_URI).setValue(downloadUri);
                            newPost.child(Constans.UID).setValue(mCurrentUser.getUid());
                            newPost.child("date").setValue(saveDate);
                            newPost.child("time").setValue(saveTime);
                            newPost.child("time_stamp").setValue(ServerValue.TIMESTAMP);
                            newPost.child("counter").setValue(post_counter);
                            newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Intent news = new Intent(PostActivity.this,NewsActivity.class);
                                        startActivity(news);
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
                                        Intent news = new Intent(PostActivity.this,NewsActivity.class);
                                        startActivity(news);
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


        }else if (!TextUtils.isEmpty(descriptionValue)){

            final  DatabaseReference newPost = mBlogDatabaseRef.child(postRandomKey);
            //get one by one data and store inside database
            for (int i =0;i<all_user.size();i++) {

                user_id = all_user.get(i);
                if (!user_id.equals(current_uid)) {
                    DatabaseReference newNotificationref = mRootRef.child("notifications_post").child(user_id).push();
                    String newNotificationId = newNotificationref.getKey();

                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", current_uid);
                    notificationData.put("type", "new post");

                    Map requestMap = new HashMap();
                    requestMap.put("notifications_post/" + user_id + "/" + newNotificationId, notificationData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(PostActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();

                            } else {

                            }

                        }
                    });

                }



            }

            mDatabaseReferenceUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    newPost.child(Constans.DESCRITION).setValue(descriptionValue);
                    newPost.child(Constans.UID).setValue(mCurrentUser.getUid());
                    newPost.child("date").setValue(saveDate);
                    newPost.child("time").setValue(saveTime);
                    newPost.child("time_stamp").setValue(ServerValue.TIMESTAMP);
                    newPost.child("counter").setValue(post_counter);
                    newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Intent news = new Intent(PostActivity.this,NewsActivity.class);
                                startActivity(news);
                            }
                            else {
                                Toast.makeText(PostActivity.this, "Your Post Does Not Stored.!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            progressBar.setVisibility(View.GONE);


        } else
         {
            Toast.makeText(this, "Fill all field first.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }

    }


    public void initView(){
        //toolbar
        mToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        // add back arrow to toolbar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
        }
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.post_custom_bar,null);
        actionBar.setCustomView(action_bar_view);
        mPost_Text = (TextView) findViewById(R.id.custom_bar_post);
        show_image = findViewById(R.id.showimage_ImageView);
        postDescription = findViewById(R.id.post_Discription_editText);
        //postSubmitBtn = findViewById(R.id.post_Button);
        linearLayout = findViewById(R.id.conslayout);
        progressBar = findViewById(R.id.progressBar);
        photo_layout = findViewById(R.id.photo_layout);

    }
    public void initFireBase(){
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid().toString();
        currentEmail = mAuth.getCurrentUser().getEmail().toString();
        mCurrentUser = mAuth.getCurrentUser();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseReferenceUser =FirebaseDatabase.getInstance().getReference().child(Constans.USER_DATABSE_PATH).child(mCurrentUser.getUid());
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mBlogDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constans.POST_DATABSE_PATH);
        mDatabaseUserRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseRefPostCount = FirebaseDatabase.getInstance().getReference().child(Constans.USER_POST_COUNT_PATH);
        mPostNotificationRef = FirebaseDatabase.getInstance().getReference().child("notifications_post");

    }





}

