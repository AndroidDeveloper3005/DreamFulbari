package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePageActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG ="HomePageActivity" ;
    private static  final int ERROR_DIALOG_REQUEST = 9001;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mDatabaseUsers,mDatabaseUserRef;
    private CircleImageView userImageView;
    private TextView user_name, user_address;
    private String currentUserID,currentEmail;
    private View navView;
    private Button history,organization,student, employer,bloodgroup, helpline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initFireBaseAuth();
        initView();
        onClickMethod();




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (mAuth.getCurrentUser() == null) {
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

        castNavViewItem();
        navigationView.setItemIconTintList(null);

        navigationView.setNavigationItemSelectedListener(this);
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
        organization = findViewById(R.id.organization_button);
        student = findViewById(R.id.student_button);
        employer = findViewById(R.id.employer_button);
        bloodgroup = findViewById(R.id.bloodgroup_button);
        helpline = findViewById(R.id.helpline_button);
    }


    private void onClickMethod(){
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomePageActivity.this, "History", Toast.LENGTH_SHORT).show();

            }
        });
        organization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

            }
        });
        helpline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }



    private void castNavViewItem() {
        userImageView =navView.findViewById(R.id.user_imageView);
        user_name = navView.findViewById(R.id.userName_textview);
        user_address = navView.findViewById(R.id.userPhone_textView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_manu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.Account_Setup){
            Intent accountIntent = new Intent(getApplicationContext(),UserAccountSetupActivity.class);
            accountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(accountIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_news) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));

        } else if (id == R.id.nav_me) {

        } else if (id == R.id.nav_map) {
            if (isServiceOk()){
                startActivity(new Intent(this,MapActivity.class));
            }

        }else if (id == R.id.nav_logout) {
            logOut();

        } else if (id == R.id.nav_about_us) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void initFireBaseAuth(){
        mAuth = FirebaseAuth.getInstance();
/*        currentUserID = mAuth.getCurrentUser().getUid().toString();
        currentEmail = mAuth.getCurrentUser().getEmail().toString();*/
        mDatabaseRef =FirebaseDatabase.getInstance().getReference().child(Constans.POST_DATABSE_PATH);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child(Constans.USER_DATABSE_PATH);
        mDatabaseUserRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseRef.keepSynced(true);
        mDatabaseUsers.keepSynced(true);


    }

    private void logOut() {
        mAuth.signOut();
    }


}
