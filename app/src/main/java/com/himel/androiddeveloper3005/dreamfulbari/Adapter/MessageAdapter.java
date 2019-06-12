package com.himel.androiddeveloper3005.dreamfulbari.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Messages;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import android.widget.RelativeLayout.LayoutParams;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;
    private Context mContext;

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

        String from_user = c.getFrom();
        String message_type = c.getType();



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
