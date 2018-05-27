package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAccountSetupActivity extends BaseActivity implements View.OnClickListener {
    private CircleImageView user_setUp_imageView;
    private EditText user_name,user_address,user_phone,userCurrentLoc,organization;
    private Spinner user_professionSpinner,user_bloodgroup_spinner,gender_spinner;
    private Button setup_button;
    public static final int GALLERY_REQUEST = 1;
    private Uri mImageUri = null;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private ProgressBar mProgressBar;
    private List<String> professionList,bloodList,genderList;
    private String professionItemSelected,bloodgroupItemSelected,genderitemSelected;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        initView();
        getToolbar();
        enableBackButton();
        initFireBase();
        professionList = new ArrayList<String>();
        professionList.add("Select One");
        professionList.add("Job Holder");
        professionList.add("Student");

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

    private void spinnerSetAdapter() {
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterProfession = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, professionList);

        // Drop down layout style - list view with radio button
        dataAdapterProfession.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        user_professionSpinner.setAdapter(dataAdapterProfession);

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
        organization = findViewById(R.id.organization_name_editText);
        user_phone = findViewById(R.id.userPhone_editText);
        user_professionSpinner = findViewById(R.id.userProfession_spinner);
        user_bloodgroup_spinner = findViewById(R.id.userBloodGroup_spinner);
        gender_spinner = findViewById(R.id.userGender_spinner);
        setup_button = findViewById(R.id.userSetup_button);
        user_setUp_imageView.setOnClickListener(this);
        setup_button.setOnClickListener(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading..");


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
            startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), GALLERY_REQUEST);
        }

        else if (v == setup_button){
            dialog.show();
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
        final   String curentLocation = userCurrentLoc.getText().toString().trim();
        final   String organizationName = organization.getText().toString().trim();
        final   String phone = user_phone.getText().toString().trim();
        final   String profession = professionItemSelected;
        final   String bloodgroup = bloodgroupItemSelected;
        final   String gender = genderitemSelected;
        final  String user_id = mAuth.getCurrentUser().getUid().toString().trim();
        final  String email =   mAuth.getCurrentUser().getEmail().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(address)&& !TextUtils.isEmpty(curentLocation)&& !TextUtils.isEmpty(organizationName)&& !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(profession) && !TextUtils.isEmpty(bloodgroup)&& !TextUtils.isEmpty(gender) && mImageUri != null){
            StorageReference filePath = mStorageReference.child(Constans.USER_IMAGE_STOREAGE_PATH).child(mImageUri.getLastPathSegment());
            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                    mDatabaseRef.child(user_id).child(Constans.USER_NAME).setValue(name);
                    mDatabaseRef.child(user_id).child(Constans.USER_ADDRESS).setValue(address);
                    mDatabaseRef.child(user_id).child(Constans.CURRENT_LOCATION).setValue(curentLocation);
                    mDatabaseRef.child(user_id).child(Constans.ORGANIZATION).setValue(organizationName);
                    mDatabaseRef.child(user_id).child(Constans.USER_PHONE).setValue(phone);
                    mDatabaseRef.child(user_id).child(Constans.USER_PROFESSION).setValue(profession);
                    mDatabaseRef.child(user_id).child(Constans.BLOODGROUP).setValue(bloodgroup);
                    mDatabaseRef.child(user_id).child(Constans.GENDER).setValue(gender);
                    mDatabaseRef.child(user_id).child(Constans.EMAIL).setValue(email);
                    mDatabaseRef.child(user_id).child(Constans.USER_IMAGE).setValue(downloadUri);
                    dialog.dismiss();
                    Intent goHome = new Intent(getApplicationContext(),PostActivity.class);
                    startActivity(goHome);
                    finish();


                }
            });

        }else {
            Toast.makeText(this, "You need to fill all field.", Toast.LENGTH_SHORT).show();

        }
    }
}
