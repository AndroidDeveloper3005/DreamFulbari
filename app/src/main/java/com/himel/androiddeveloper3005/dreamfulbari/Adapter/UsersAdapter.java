package com.himel.androiddeveloper3005.dreamfulbari.Adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.himel.androiddeveloper3005.dreamfulbari.Model.User;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private Context context;
    private ArrayList<User>users;

    public UsersAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.users_recycler_item,parent
                ,false);
        UserViewHolder viewHolder = new UserViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.userName.setText(user.getName());
        Glide.with(context).load(user.getImage())
                .into(holder.profileImage);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public class UserViewHolder extends  RecyclerView.ViewHolder{

        private TextView userName;
        private CircleImageView profileImage;

        public UserViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            profileImage = itemView.findViewById(R.id.user_profile_imageView);
        }
    }
}