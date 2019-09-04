package com.himel.androiddeveloper3005.dreamfulbari.Adapter;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.himel.androiddeveloper3005.dreamfulbari.Activity.NewsActivity;
import com.himel.androiddeveloper3005.dreamfulbari.Activity.PostUpdateActivity;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Messages;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;
    private Context mContext;
    private String push_id;
    private DatabaseReference mRootRef,newDataBaseRef;
    private StorageReference mStorageReference,mDownloadRef;
    private String currentUserID;
    private String TAG = "MessageAdapter";
    private String sender_key,receiver_key,key;
    private ProgressDialog mProgressDialog,mProgressDialog_download;


    public MessageAdapter(List<Messages> mMessageList, Context mContext) {
        this.mMessageList = mMessageList;
        this.mContext = mContext;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.MessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.MessageViewHolder(view);
        }



    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message;
        public CircleImageView profile_image;
        public TextView txt_seen;
        public ImageView messageImage;




        public MessageViewHolder(View view) {
            super(view);
            show_message = view.findViewById(R.id.show_message);
            profile_image = view.findViewById(R.id.profile_image);
            txt_seen = view.findViewById(R.id.txt_seen);
            messageImage = view.findViewById(R.id.message_image_layout);



        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {
        Messages c = mMessageList.get(i);
        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        String push_id = c.getPush_id();

        String from_user = c.getFrom();
        String message_type = c.getType();
        viewHolder.messageImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getRootView().getContext());

                // Setting Dialog Title
                alertDialog.setTitle("Confirm ...");

                // Setting Dialog Message
                alertDialog.setMessage("Are you sure you want to do this?");

                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.ic_info_48dp);

                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        mDownloadRef = FirebaseStorage.getInstance().getReference().child("message_images").child(push_id);
                        mDownloadRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                downloadFile(v.getRootView().getContext(),""+push_id,".jpeg",DIRECTORY_DOWNLOADS,url);


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                        Log.d(TAG,"Download : ID -"+ push_id);

                    }
                });

                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //progress Dialog
                        mProgressDialog = new ProgressDialog(v.getRootView().getContext());
                        mProgressDialog.setTitle("Loading..");
                        mProgressDialog.setMessage("Please wait ....");
                        mProgressDialog.setCanceledOnTouchOutside(false);
                        mProgressDialog.show();

                        mStorageReference = FirebaseStorage.getInstance().getReference().child("message_images");
                        mRootRef= FirebaseDatabase.getInstance().getReference();
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
                                                newDataBaseRef = mRootRef.child("messages").child(sender_key).child(receiver_key).child(push_id);
                                                newDataBaseRef.removeValue();
                                                Log.d("MessageAdapter","Selected Message Deleted : "+ push_id );
                                                mStorageReference.child(push_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG,"Selected Message Image Deleted : ID -"+ push_id);
                                                        mProgressDialog.dismiss();

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
                        dialog.cancel();
                    }
                });

                // Showing Alert Message
                alertDialog.show();


                return true;
            }
        });




        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();


                //viewHolder.displayName.setText(name);
                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.profile_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(image).into(viewHolder.profile_image);

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(message_type.equals("text")) {
            viewHolder.show_message.setVisibility(View.VISIBLE);
            viewHolder.show_message.setText(c.getMessage());
            //viewHolder.messageImage.setVisibility(View.INVISIBLE);


        } else {
            viewHolder.messageImage.setVisibility(View.VISIBLE);
            Picasso.get().load(c.getMessage()).networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.messageImage, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(c.getMessage()).into(viewHolder.messageImage);

                }
            });


        }

    }
//download file
    private void downloadFile(Context context, String fileName, String fileExtention, String directoryDownloads, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,directoryDownloads,fileName + fileExtention);
        downloadManager.enqueue(request);

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        mAuth = FirebaseAuth.getInstance();
        if (mMessageList.get(position).getFrom().equals(mAuth.getCurrentUser().getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }






}
