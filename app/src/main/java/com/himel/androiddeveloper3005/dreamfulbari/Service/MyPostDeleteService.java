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

public class MyPostDeleteService extends Service {
    private DatabaseReference mDatabaseRef,mDatabaseLikesRef;
    private FirebaseAuth mAuth;
    private StorageReference mStorageReference;
    private String currentUserID;
    private String TAG = "MyPostDeleteService";

    public MyPostDeleteService() {
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
        deletePost();


    }



    private void deletePost() {
        //delete post
        long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS);
        Query oldItems = mDatabaseRef.orderByChild("time").endAt(cutoff);
        oldItems.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    String post_key = itemSnapshot.getRef().getKey();
                    mDatabaseLikesRef.child(post_key).removeValue();
                    mStorageReference.child(post_key).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG,"Post Storeage Data Deleted : ID "+ post_key);


                        }
                    });
                    itemSnapshot.getRef().removeValue();
                    Log.d(TAG,"Post Data Deleted : ID -"+ post_key);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}

