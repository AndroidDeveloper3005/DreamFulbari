package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import com.himel.androiddeveloper3005.dreamfulbari.Service.MyMessageDeleteService;
import com.himel.androiddeveloper3005.dreamfulbari.Util.AdUtils;
import com.himel.androiddeveloper3005.dreamfulbari.Util.ViewPagerAdapter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessengerActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseRef;
    private FirebaseUser mAuth;
    private Toolbar mToolbar;
    private CircleImageView mUserImage;
    private TextView mUserName;
    private Context mContext;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private DatabaseReference mUserRef;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        intiView();
        fireBase();
        startService(new Intent(this, MyMessageDeleteService.class));
        ads();

    }

    private void ads() {
        AdUtils.getInstance(this).loadFullScreenAd(this);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AdUtils.getInstance(MessengerActivity.this).showFullScreenAd();
                        AdUtils.getInstance(MessengerActivity.this).loadFullScreenAd(MessengerActivity.this);

                    }
                });

            }
        }, 5 , 5, TimeUnit.MINUTES);
    }

/*    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AdUtils.getInstance(MessengerActivity.this).showFullScreenAd();
        AdUtils.getInstance(MessengerActivity.this).loadFullScreenAd(MessengerActivity.this);

    }*/

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth;
        if(currentUser == null){

        } else {

            mUserRef.child("online").setValue("true");

        }
    }



    @Override

    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth;
        if(currentUser != null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.chat_manu, menu);
         return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }else if (item.getItemId() == R.id.all_user){

            Intent usersintent = new Intent(getApplicationContext(),UsersActivity.class);
            usersintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(usersintent);

        }else if (item.getItemId() == R.id.account_setting){

           /* Intent accountIntent = new Intent(getApplicationContext(),UserAccountSetupActivity.class);
            accountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(accountIntent);*/
            Toast.makeText(getApplicationContext(), "Account", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }


    private void fireBase() {
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(Constans.USER_DATABSE_PATH).child(mAuth.getUid());
        if (mAuth != null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());

        }
    }

    private void intiView() {
        mContext = getApplicationContext();
        mToolbar =(Toolbar)findViewById(R.id.main_page_toolbar);
        mUserImage = findViewById(R.id.user_profile_imageView);
        mUserName = findViewById(R.id.user_name);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle("My Chats List");

        mTabLayout = findViewById(R.id.main_tabs);
        mViewPager = findViewById(R.id.tapPager);
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);


    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
