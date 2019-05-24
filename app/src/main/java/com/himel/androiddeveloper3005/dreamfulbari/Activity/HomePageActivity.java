package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.Model.HelpLine;
import com.himel.androiddeveloper3005.dreamfulbari.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePageActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG ="HomePageActivity" ;
    private static  final int ERROR_DIALOG_REQUEST = 9001;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mDatabaseUsers,mDatabaseUserRef,mDatabaseRefhelp;
    private CircleImageView userImageView;
    private TextView user_name, user_address;
    private String currentUserID,currentEmail;
    private View navView;
    private Button history, news,student, employer,bloodgroup, helpline;
    private boolean accountCreated = false;
    private ArrayList<HelpLine>helpLines;
    private String uno,ambulance,chairman,police,fireservice;
    private DatabaseReference mDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initFireBaseAuth();
        initView();
        onClickMethod();
        getHelpLineData();

        //get token
        String deviceTokenId = FirebaseInstanceId.getInstance().getToken();
        mDatabaseReference.child(mAuth.getCurrentUser().getUid()).child("device_token").setValue(deviceTokenId);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (mAuth.getCurrentUser() == null) {
                    //startActivity(new Intent(getApplicationContext(), PhoneAuthActivity.class));
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }


            }
        };


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navView = navigationView.inflateHeaderView(R.layout.nav_header_home_page);

        navigationView.setItemIconTintList(null);

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void getHelpLineData() {
        mDatabaseRefhelp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                     uno=snapshot.child(Constans.UNO).getValue().toString();
                     ambulance=snapshot.child(Constans.AMBULANCE).getValue().toString();
                     chairman=snapshot.child(Constans.CHAIRMAN).getValue().toString();
                     police=snapshot.child(Constans.POLICE).getValue().toString();
                     fireservice=snapshot.child(Constans.FIRESERVICE).getValue().toString();

                    HelpLine mHelpLine = new HelpLine(ambulance,chairman,fireservice,uno,police);
                    helpLines.add(mHelpLine);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean isServiceOk(){
        Log.d(TAG,"isServiceOK : checking google service version");
        int avialbale = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomePageActivity.this);
        if (avialbale == ConnectionResult.SUCCESS){
            Log.d(TAG,"isServiceOK : google service is Working");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(avialbale)){
            Log.d(TAG,"isServiceOK :  google service is not working!!! An Error Occured");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomePageActivity.this,avialbale,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(this, "We can not make map request", Toast.LENGTH_SHORT).show();
        }
        return false;

    }

    private void initView() {
        history = findViewById(R.id.history_button);
        news = findViewById(R.id.news_button);
        student = findViewById(R.id.student_button);
        employer = findViewById(R.id.employer_button);
        bloodgroup = findViewById(R.id.bloodgroup_button);
        helpline = findViewById(R.id.helpline_button);
        helpLines = new ArrayList<>();

    }


    private boolean hasAccount() {
        currentUserID = mAuth.getCurrentUser().getUid().toString();

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(currentUserID)){
                    accountCreated = true;
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

    private void onClickMethod(){
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomePageActivity.this, "History", Toast.LENGTH_SHORT).show();

            }
        });
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),NewsActivity.class));

            }
        });
        employer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),EmployeeActivity.class));

            }
        });
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),StudentActivity.class));

            }
        });
        bloodgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),BloodActivity.class));
                //alartDialog();

            }
        });
        helpline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomePageActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.emargency_dialog, null);

                Button ambulance_btn =  (Button) mView.findViewById(R.id.ambulance_btn);
                Button chairman_btn =  (Button) mView.findViewById(R.id.chairman_btn);
                Button fire_service_btn =  (Button) mView.findViewById(R.id.fireservice_btn);
                Button police_btn =  (Button) mView.findViewById(R.id.police_btn);
                Button uno_btn =  (Button) mView.findViewById(R.id.uno_btn);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                ambulance_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+ambulance));
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) HomePageActivity.this, new String[]{Manifest.permission.CALL_PHONE}, Constans.REQUEST_PHONE_CALL);
                            return;
                        }
                        else {
                            startActivity(callIntent);
                        }
                    }
                });
                chairman_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+chairman));
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) HomePageActivity.this, new String[]{Manifest.permission.CALL_PHONE}, Constans.REQUEST_PHONE_CALL);
                            return;
                        }
                        else {
                            startActivity(callIntent);
                        }
                    }
                });
                fire_service_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+fireservice));
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) HomePageActivity.this, new String[]{Manifest.permission.CALL_PHONE}, Constans.REQUEST_PHONE_CALL);
                            return;
                        }
                        else {
                            startActivity(callIntent);
                        }
                    }
                });
                police_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+police));
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) HomePageActivity.this, new String[]{Manifest.permission.CALL_PHONE}, Constans.REQUEST_PHONE_CALL);
                            return;
                        }
                        else {
                            startActivity(callIntent);
                        }
                    }
                });
                uno_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+uno));
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.CALL_PHONE}, Constans.REQUEST_PHONE_CALL);
                            return;
                        }
                        else {
                            startActivity(callIntent);
                        }
                    }
                });


            }
        });
    }

    private void alartDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, You wanted to make decision");
        alertDialogBuilder.setIcon(R.drawable.ic_save_black_24dp);
                alertDialogBuilder.setPositiveButton("Show All Doner", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                             startActivity(new Intent(getApplicationContext(), BloodActivity.class));                            }
                        });
                alertDialogBuilder.setNegativeButton("Blood Donation Complete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(getApplicationContext(), DonateBloodActivity.class));

                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);

    }

    @Override
    public void onBackPressed() {

        final android.support.v7.app.AlertDialog.Builder alertDialogBuilder =
                new android.support.v7.app.AlertDialog.Builder(this);

        alertDialogBuilder.setMessage(getResources().getString(R.string.dialoge_text));
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.no),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();



    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        }  else if (id == R.id.nav_me) {
            startActivity(new Intent(this,MeActivity.class));

        } else if (id == R.id.nav_map) {
            startActivity(new Intent(this,MapBoxActivity.class));

            /*if (isServiceOk()){nav_chats
                startActivity(new Intent(this,MapActivity.class));
            }*/

        }else if (id == R.id.nav_chats) {
            startActivity(new Intent(this,MessengerActivity.class));
        }else if (id == R.id.nav_logout) {
            logOut();

        }
        else if (id == R.id.nav_about_us) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public void initFireBaseAuth(){
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRefhelp = FirebaseDatabase.getInstance().getReference(Constans.HELPLINE_DATABSE_PATH);
        mDatabaseRef =FirebaseDatabase.getInstance().getReference().child(Constans.POST_DATABSE_PATH);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child(Constans.USER_DATABSE_PATH);
        mDatabaseUserRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Constans.USER_DATABSE_PATH);
        mDatabaseReference.child(mAuth.getCurrentUser().getUid()).child("device_token").setValue(null);
        mDatabaseRef.keepSynced(true);
        mDatabaseUsers.keepSynced(true);


    }

    private void logOut() {
        mAuth.signOut();
    }


}
