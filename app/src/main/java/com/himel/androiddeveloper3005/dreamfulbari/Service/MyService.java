package com.himel.androiddeveloper3005.dreamfulbari.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MyService extends Service {
    private DatabaseReference mDatabaseRef,mRootRef,mDatabaseLikesRef,newDataBaseRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private StorageReference mStorageReference;
    private String currentUserID;
    private String TAG = "MyService";
    private String sender_key,receiver_key,key;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() !=null){
            currentUserID = mAuth.getCurrentUser().getUid();

        }
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constans.POST_DATABSE_PATH);
        mStorageReference = FirebaseStorage.getInstance().getReference().child(Constans.POST_STOREAGE_PATH);
        mDatabaseLikesRef = FirebaseDatabase.getInstance().getReference().child(Constans.LIKES);
        mStorageReference = FirebaseStorage.getInstance().getReference().child("message_images");
        mRootRef= FirebaseDatabase.getInstance().getReference();
        //delete post
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String user_id = snapshot.child("uid").getValue(String.class);
                    //String timeStamp = snapshot.child("time_stamp"). getValue().toString();
                    if (user_id !=null  && user_id.equals(currentUserID)){
                        String post_key =  snapshot.getKey();
                        long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS);
                        Query oldItems = mDatabaseRef.orderByChild("time_stamp").endAt(cutoff);
                        oldItems.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                                    itemSnapshot.getRef().removeValue();
                                    mDatabaseLikesRef.child(post_key).removeValue();
                                    mStorageReference.child(post_key).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG,"Storeage Data Deleted : ID "+ post_key);


                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        //Toast.makeText(getApplicationContext(),"Message: "+post_key,Toast.LENGTH_LONG).show();


                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //delete message
        mRootRef.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    sender_key = itemSnapshot.getKey();

                    mRootRef.child("messages").child(sender_key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                                receiver_key = itemSnapshot.getKey();
                                newDataBaseRef = mRootRef.child("messages").child(sender_key).child(receiver_key);

                                newDataBaseRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                                            key = itemSnapshot.getKey();
                                            long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);
                                            Query oldItems = newDataBaseRef.orderByChild("time").endAt(cutoff);
                                            oldItems.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                                                        String storeage_ref = itemSnapshot.getKey();
                                                        mStorageReference.child(storeage_ref).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d(TAG,"Message Image Deleted : ID -"+ storeage_ref);

                                                            }
                                                        });
                                                        itemSnapshot.getRef().removeValue();
                                                        Log.d(TAG,"Message Data Deleted : ID -"+ key);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    throw databaseError.toException();
                                                }
                                            });


                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });





                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
