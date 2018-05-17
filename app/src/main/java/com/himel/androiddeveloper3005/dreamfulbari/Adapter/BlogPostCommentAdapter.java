package com.himel.androiddeveloper3005.dreamfulbari.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.himel.androiddeveloper3005.dreamfulbari.Model.BlogPost;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Comment;
import com.himel.androiddeveloper3005.dreamfulbari.R;

import java.util.ArrayList;

public class BlogPostCommentAdapter extends RecyclerView.Adapter<BlogPostCommentAdapter.BlogPostCommenViewHolder> {

    private Context context;
    public ArrayList<Comment> comments;

    public BlogPostCommentAdapter(Context context, ArrayList<Comment>commentArrayList){
        this.context =context;
        this.comments =commentArrayList;


    }



    @Override
    public BlogPostCommenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view= inflater.from(parent.getContext()).inflate(R.layout.blog_comment_row, parent, false);
        BlogPostCommenViewHolder blogPostCommenViewHolder = new BlogPostCommenViewHolder(view);
        return blogPostCommenViewHolder;
    }

    @Override
    public void onBindViewHolder(BlogPostCommenViewHolder holder, int position) {

        final Comment comment = comments.get(position);
        holder.userName_text.setText(comment.getUser_name());
        holder.comment_text.setText(""+comment.getComment());
        Glide.with(context).load(comments.get(position).getUser_image())
                .into(holder.commentUserImage);


    }

    @Override
    public int getItemCount() {


        if (comments !=null) {
            return comments.size();
        }
        return 0;
    }





    //viewholder class

    public static  class BlogPostCommenViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView commentUserImage;
        public TextView userName_text,comment_text;
        public BlogPostCommenViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.post_comment_cardView);
            commentUserImage = itemView.findViewById(R.id.user_image_view);
            userName_text = itemView.findViewById(R.id.username_textView);
            comment_text = itemView.findViewById(R.id.comment_textView);
        }


    }


}
