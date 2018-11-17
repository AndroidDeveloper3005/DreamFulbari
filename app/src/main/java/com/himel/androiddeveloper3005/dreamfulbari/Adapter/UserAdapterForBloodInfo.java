package com.himel.androiddeveloper3005.dreamfulbari.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.Interface.OnItemClickListener;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Users;
import com.himel.androiddeveloper3005.dreamfulbari.MyFilter.BloodCustomFilter;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapterForBloodInfo extends RecyclerView.Adapter<UserAdapterForBloodInfo.BloodInfoViewHolder>
        implements Filterable {

    public Context mContext;
    public ArrayList<Users>usersList;
    private BloodCustomFilter filter;
    private OnItemClickListener mListener;

    public UserAdapterForBloodInfo(Context mContext, ArrayList<Users> usersList) {
        this.mContext = mContext;
        this.usersList = usersList;
    }

    public class BloodInfoViewHolder extends RecyclerView.ViewHolder{
        Context mContext;
        ArrayList<Users>arrayList;
        ImageView userImageView,phoneImage,smsImage;
        TextView userName,userDetails;
        DatabaseReference mDatabaseReference,mDataBaseUser;
        FirebaseAuth mAuth;

        public BloodInfoViewHolder(View mView, Context mContext, ArrayList<Users> arrayList) {
            super(mView);
            this.mContext = mContext;
            this.arrayList = arrayList;
            mDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child(Constans.BLOOD_DONER_DATABSE_PATH);
            mDataBaseUser = FirebaseDatabase.getInstance()
                    .getReference().child("Users");
            mAuth = FirebaseAuth.getInstance();
            userName = mView.findViewById(R.id.name_text);
            userImageView = mView.findViewById(R.id.profile_image);
            userDetails = mView.findViewById(R.id.user_details);
            phoneImage = mView.findViewById(R.id.phone_call_imgeview);
            smsImage = mView.findViewById(R.id.sms_send_imgeview);
            phoneImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemListener(view,getLayoutPosition());
                }
            });
            smsImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemListener(view,getLayoutPosition());
                }
            });

        }
    }

    @NonNull
    @Override
    public BloodInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent
            , int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.blood_donner_item_design,parent
                ,false);
        BloodInfoViewHolder viewholder = new BloodInfoViewHolder(view,mContext,usersList);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull BloodInfoViewHolder holder, int position) {
        Users users = usersList.get(position);

        String status ="Blood Group :" + users.getBloodgroup()
                +"\n"
                +"Want To Donate :"+users.getDonner_status()
                +"\n"
                + "Location :"+users.getLocation();

        holder.userName.setText(users.getName());
        //Glide.with(mContext).load(users.getImage()).into(holder.userImageView);
        Picasso.get()
                .load(users.getImage())
                .into(holder.userImageView);
        holder.userDetails.setText(status);


    }



    @Override
    public int getItemCount() {
        return usersList.size();
    }
    public ArrayList<Users> getDataList() {
        return usersList;
    }

    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new BloodCustomFilter(this, usersList);
        }
        return filter;
    }

    public void setItemClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

}
