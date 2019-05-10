package com.himel.androiddeveloper3005.dreamfulbari.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Users;
import com.himel.androiddeveloper3005.dreamfulbari.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private Context mContext;
    private List<Users>users;

    public UsersAdapter(Context mContext, List<Users> users) {
        this.mContext = mContext;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.users_recycler_item,parent,false);
        return new UsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users user = users.get(position);
        holder.userName.setText(user.getName());
        Glide.with(mContext).load(user.getImage())
                .into(holder.profileImage);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView userName;
        private CircleImageView profileImage;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            profileImage = itemView.findViewById(R.id.user_profile_imageView);

        }
    }
}
