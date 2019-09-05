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

public class MyMessageDeleteService extends Service {
    private DatabaseReference mDatabaseRef,mRootRef,mDatabaseLikesRef,newDataBaseRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private StorageReference mStorageReference;
    private String currentUserID;
    private String TAG = "MyMessageDeleteService";
    private String sender_key,receiver_key,key;

    public MyMessageDeleteService() {
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
        mStorageReference = FirebaseStorage.getInstance().getReference().child("message_images");
        mRootRef= FirebaseDatabase.getInstance().getReference();
        deleteMessage();


    }

    private void deleteMessage() {
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
                                            long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS);
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
