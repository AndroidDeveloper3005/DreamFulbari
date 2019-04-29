package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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
import com.himel.androiddeveloper3005.dreamfulbari.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class UserAccountSetupActivity extends BaseActivity implements View.OnClickListener {
    private CircleImageView user_setUp_imageView;
    private EditText user_name,user_address, user_email,userCurrentLoc,organization,institute;
    private Spinner user_professionSpinner,user_bloodgroup_spinner,gender_spinner,bloodDoner_spinner;
    private Button setup_button;
    public static final int GALLERY_REQUEST = 1;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private ProgressBar mProgressBar;
    private List<String> professionList,bloodList,genderList,donnerconfirmList;
    private String professionItemSelected,bloodgroupItemSelected,genderitemSelected, wantToDoneritemSelected;
    private ProgressDialog dialog;
    private static final int REQUEST_PHONE_CALL =1 ;
    private static final String TAG ="UserAccountSetupActivity";
    private Bitmap thumb_bitmap,bitmap;
    private File thump_filepath;
    private Uri mImageUri,resultUri;
    private boolean accountCreated = false;
    private String currentUserID;
    private DatabaseReference mDatabaseUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasAccount();
        setContentView(R.layout.activity_setup);
        initView();
        getToolbar();
        enableBackButton();
        setToolbarTitle("User Information");
        initFireBase();
        professionList = new ArrayList<String>();
        professionList.add("Select One");
        professionList.add("Job Holder");
        professionList.add("Student");


        donnerconfirmList = new ArrayList<String>();
        donnerconfirmList.add("Select One");
        donnerconfirmList.add("Yes");
        donnerconfirmList.add("No");

        bloodList = new ArrayList<String>();
        bloodList.add("Select One");
        bloodList.add("O-positive");
        bloodList.add("O-negative");
        bloodList.add("A-positive");
        bloodList.add("A-negative");
        bloodList.add("B-positive");
        bloodList.add("B-negative");
        bloodList.add("AB-positive");
        bloodList.add("AB-negative");

        genderList = new ArrayList<String>();
        genderList.add("Select One");
        genderList.add("Male");
        genderList.add("Female");

        spinnerSetAdapter();


    }



    private boolean hasAccount() {
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child(Constans.USER_DATABSE_PATH);
        currentUserID = mAuth.getCurrentUser().getUid().toString();

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(currentUserID)){
                    accountCreated = true;
                    Intent intent = new Intent(getApplicationContext(),HomePageActivity.class);
                    startActivity(intent);
                }
                else {
                    accountCreated = false;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return accountCreated;
    }


    private void spinnerSetAdapter() {
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterProfession = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, professionList);

        // Drop down layout style - list view with radio button
        dataAdapterProfession.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        user_professionSpinner.setAdapter(dataAdapterProfession);


        // Creating adapter for spinner
        ArrayAdapter<String> dataDonerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, donnerconfirmList);

        // Drop down layout style - list view with radio button
        dataDonerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodDoner_spinner.setAdapter(dataDonerAdapter);

        ArrayAdapter<String> dataAdapterBlood = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bloodList);

        // Drop down layout style - list view with radio button
        dataAdapterBlood.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        user_bloodgroup_spinner.setAdapter(dataAdapterBlood);

        ArrayAdapter<String> dataAdapterGender = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderList);

        // Drop down layout style - list view with radio button
        dataAdapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender_spinner.setAdapter(dataAdapterGender);

    }

    private void initView(){
        user_setUp_imageView = findViewById(R.id.user_profile_imageView);
        user_name = findViewById(R.id.userName_editText);
        user_address = findViewById(R.id.userAddress_editText);
        userCurrentLoc =findViewById(R.id.userCurrent_location_editText);
        //organization = findViewById(R.id.organization_name_editText);
        institute = findViewById(R.id.institute_name_editText);
        user_email = findViewById(R.id.useremail_editText);
        user_professionSpinner = findViewById(R.id.userProfession_spinner);
        user_bloodgroup_spinner = findViewById(R.id.userBloodGroup_spinner);
        gender_spinner = findViewById(R.id.userGender_spinner);
        bloodDoner_spinner = findViewById(R.id.wantto_donet_blood_spinner);
        setup_button = findViewById(R.id.userSetup_button);
        user_setUp_imageView.setOnClickListener(this);
        setup_button.setOnClickListener(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading..");



        bloodDoner_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                wantToDoneritemSelected = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        user_professionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                professionItemSelected = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        user_bloodgroup_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bloodgroupItemSelected = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        gender_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genderitemSelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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

            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_PHONE_CALL);
            }
            else
            {
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), GALLERY_REQUEST);
            }

        }

        else if (v == setup_button){
            dialog.show();
            startsetupaccount();
        }

    }


/*    @Override
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
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {

            mImageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                user_setUp_imageView.setImageBitmap(bitmap);
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


    private void startsetupaccount() {
        final   String name = user_name.getText().toString().trim();
        final   String address = user_address.getText().toString().trim();
        final   String curentLocation = userCurrentLoc.getText().toString().trim();
        final   String instituteName = institute.getText().toString().trim();
        final   String email = user_email.getText().toString().trim();
        final   String profession = professionItemSelected;
        final   String bloodgroup = bloodgroupItemSelected;
        final   String gender = genderitemSelected;
        final   String user_id = mAuth.getCurrentUser().getUid().toString().trim();
        final   String phone =   mAuth.getCurrentUser().getPhoneNumber().toString().trim();
        final   String wantToBllodDonate =   wantToDoneritemSelected;

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(wantToBllodDonate) &&  !TextUtils.isEmpty(instituteName)
                && !TextUtils.isEmpty(address)&& !TextUtils.isEmpty(curentLocation)
                && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(profession) && !TextUtils.isEmpty(bloodgroup)&& !TextUtils.isEmpty(gender) && mImageUri != null){

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,60,byteArrayOutputStream);
            final byte[] thumb_byte = byteArrayOutputStream.toByteArray();

            StorageReference filePath = mStorageReference.child(Constans.USER_IMAGE_STOREAGE_PATH).child(mImageUri.getLastPathSegment());
            UploadTask uploadTask = filePath.putBytes(thumb_byte);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                    mDatabaseRef.child(user_id).child(Constans.USER_NAME).setValue(name);
                    mDatabaseRef.child(user_id).child(Constans.USER_ADDRESS).setValue(address);
                    mDatabaseRef.child(user_id).child(Constans.CURRENT_LOCATION).setValue(curentLocation);
                    mDatabaseRef.child(user_id).child(Constans.INSTITUTE).setValue(instituteName);
                    mDatabaseRef.child(user_id).child(Constans.USER_PHONE).setValue(phone);
                    mDatabaseRef.child(user_id).child(Constans.USER_PROFESSION).setValue(profession);
                    mDatabaseRef.child(user_id).child(Constans.BLOODGROUP).setValue(bloodgroup);
                    mDatabaseRef.child(user_id).child(Constans.BLOODDONER).setValue(wantToBllodDonate);
                    mDatabaseRef.child(user_id).child(Constans.GENDER).setValue(gender);
                    mDatabaseRef.child(user_id).child(Constans.EMAIL).setValue(email);
                    mDatabaseRef.child(user_id).child(Constans.USER_IMAGE).setValue(downloadUri);
                    dialog.dismiss();
                    Intent goHome = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(goHome);
                    finish();


                }
            });

        }else {
            Toast.makeText(this, "You need to fill all field.", Toast.LENGTH_SHORT).show();

        }
    }
}
