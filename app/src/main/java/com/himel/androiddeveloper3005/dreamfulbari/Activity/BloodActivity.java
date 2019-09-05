package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himel.androiddeveloper3005.dreamfulbari.Adapter.UserAdapterForBloodInfo;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.Interface.OnItemClickListener;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Users;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import com.himel.androiddeveloper3005.dreamfulbari.Util.ActivityUtils;
import com.himel.androiddeveloper3005.dreamfulbari.Util.AdUtils;
import com.himel.androiddeveloper3005.dreamfulbari.Util.MyDividerItemDecoration;
import com.himel.androiddeveloper3005.dreamfulbari.Util.ToolBarAndStatusBar;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BloodActivity extends ToolBarAndStatusBar {
    private RecyclerView recyclerView;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private String randomKey = null,saveDate,saveTime,current_uid;
    public ArrayList<Users>usersList,filterdata;
    public UserAdapterForBloodInfo adapter;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    String sms = null,phone = null;
    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariable();
        initFirebase();
        intView();
        setToolbarTitle("Blood Information");
        getDataFromDatabase();
        sentPI = PendingIntent.getBroadcast(BloodActivity.this, 0, new Intent(SENT), 0);
        deliveredPI = PendingIntent.getBroadcast(BloodActivity.this, 0, new Intent(DELIVERED), 0);
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
                        AdUtils.getInstance(BloodActivity.this).showFullScreenAd();
                        AdUtils.getInstance(BloodActivity.this).loadFullScreenAd(BloodActivity.this);

                    }
                });

            }
        }, 3 , 3, TimeUnit.MINUTES);
    }

/*    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AdUtils.getInstance(BloodActivity.this).showFullScreenAd();
        AdUtils.getInstance(BloodActivity.this).loadFullScreenAd(BloodActivity.this);

    }*/

    @Override
    protected void onResume() {
        super.onResume();
        //The deliveredPI PendingIntent does not fire in the Android emulator.
        //You have to test the application on a real device to view it.
        //However, the sentPI PendingIntent works on both, the emulator as well as on a real device.

        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent successfully!", Toast.LENGTH_SHORT).show();
                        break;

                    //Something went wrong and there's no way to tell what, why or how.
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure!", Toast.LENGTH_SHORT).show();
                        break;

                    //Your device simply has no cell reception. You're probably in the middle of
                    //nowhere, somewhere inside, underground, or up in space.
                    //Certainly away from any cell phone tower.
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "No service!", Toast.LENGTH_SHORT).show();
                        break;

                    //Something went wrong in the SMS stack, while doing something with a protocol
                    //description unit (PDU) (most likely putting it together for transmission).
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "Null PDU!", Toast.LENGTH_SHORT).show();
                        break;

                    //You switched your device into airplane mode, which tells your device exactly
                    //"turn all radios off" (cell, wifi, Bluetooth, NFC, ...).
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio off!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch(getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS delivered!", Toast.LENGTH_SHORT).show();
                        break;

                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS not delivered!", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        };

        //register the BroadCastReceivers to listen for a specific broadcast
        //if they "hear" that broadcast, it will activate their onReceive() method
        registerReceiver(smsSentReceiver, new IntentFilter(SENT));
        registerReceiver(smsDeliveredReceiver, new IntentFilter(DELIVERED));
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
    }

    private void initListener() {
        adapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemListener(View view, int position) {
                loadData();
                phone = filterdata.get(position).getPhone();
                switch (view.getId()) {
                    case R.id.phone_call_imgeview:
                        ActivityUtils.getInstance().invokePhoneCall(BloodActivity.this
                                ,phone);
                        break;
                    case R.id.sms_send_imgeview:
                        if (ContextCompat.checkSelfPermission(BloodActivity.this
                                , Manifest.permission.SEND_SMS)
                                != PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(BloodActivity.this, new String []
                                            {Manifest.permission.SEND_SMS},
                                    Constans.MY_PERMISSIONS_REQUEST_SEND_SMS);
                        }else {
                            smsPopUp();
                        }
                        break;
                }

            }
        });

    }

    private void smsPopUp() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(BloodActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.sms_dialog, null);
        final EditText message = (EditText) mView.findViewById(R.id.sms_edittext);
        Button sms_send = (Button) mView.findViewById(R.id.sms_send_btn);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        sms_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sms = message.getText().toString();
                if(!sms.isEmpty()){
                    smsBroadCastReceiver();
                    dialog.dismiss();
                }else {
                    Toast.makeText(BloodActivity.this, "Please Enter A Message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void smsBroadCastReceiver() {
        SmsManager smsManager = SmsManager.getDefault();
        //phone - Recipient's phone number
        //address - Service Center Address (null for default)
        //message - SMS message to be sent
        //piSent - Pending intent to be invoked when the message is sent
        //piDelivered - Pending intent to be invoked when the message is delivered to the recipient
        smsManager.sendTextMessage(phone, null, sms, sentPI, deliveredPI);

    }

    private void loadData() {
        if (!filterdata.isEmpty()){
            filterdata.clear();
        }
        filterdata.addAll(adapter.getDataList());
    }
    private void initVariable() {
        usersList = new ArrayList<>();
        filterdata = new ArrayList<>();
    }
    private void intView() {
        setContentView(R.layout.activity_blood);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL
                ,16));
    }
    private void getDataFromDatabase() {
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot data:dataSnapshot.getChildren()) {
                    String userName = data.child(Constans.USER_NAME).getValue().toString();
                    String phone = data.child(Constans.USER_PHONE).getValue().toString();
                    String userImage = data.child(Constans.USER_IMAGE).getValue().toString();
                    String bloodGroup =data.child(Constans.BLOODGROUP).getValue().toString();
                    String confirmDonerStatus = data.child(Constans.BLOODDONER).getValue().toString();
                    String location =data.child(Constans.CURRENT_LOCATION).getValue().toString();
                    //pass value through model class user
                    Users users = new Users(userName,userImage,phone,bloodGroup,confirmDonerStatus,location);
                    if (confirmDonerStatus.equals("Yes")){
                        usersList.add(users);
                    }
                }
                adapter = new UserAdapterForBloodInfo(getApplicationContext(),usersList);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.INVISIBLE);
                initListener();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        current_uid = mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference(Constans.USER_DATABSE_PATH);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constans
                .BLOOD_DONER_DATABSE_PATH);
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
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        //toolbar search view ar id find
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //adapter.getFilter().filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                 adapter.getFilter().filter(query);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
