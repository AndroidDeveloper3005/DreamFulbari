package com.himel.androiddeveloper3005.dreamfulbari.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.himel.androiddeveloper3005.dreamfulbari.Model.BlogPost;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BlogPostAdapter extends RecyclerView.Adapter<BlogPostAdapter.BlogPostViewHolder> {

    private Context context;
    public ArrayList<BlogPost> blogPostsList;



    public BlogPostAdapter(Context context, ArrayList< BlogPost>blogPostArrayList){
        this.context =context;
        this.blogPostsList=blogPostArrayList;


    }



    @Override
    public BlogPostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view= inflater.from(parent.getContext()).inflate(R.layout.blog_post_row, parent, false);
        BlogPostViewHolder blogPostViewHolder = new BlogPostViewHolder(view);
        return blogPostViewHolder;
    }

    @Override
    public void onBindViewHolder(BlogPostViewHolder holder, int position) {

        final BlogPost post = blogPostsList.get(position);
        holder.title_textView.setText(post.getTitle());
        holder.description_textView.setText(""+post.getDescription());
        String fulurl = post.getImageUri();
      /* Picasso.with(context)
               .load(fulurl)
                .placeholder(R.drawable.ic_add_a_photo_black_24dp)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.postImage);
*/

        Glide.with(context).load(blogPostsList.get(position).getImageUri())
                .into(holder.postImage);
        /*holder.ivhotelimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HotelDetailsActivity.class);
                intent.putExtra("hotels",hotel);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                //Toast.makeText(context, "Image selected "+hotel.getHname(), Toast.LENGTH_SHORT).show();
            }
        });*/


    }

    @Override
    public int getItemCount() {


        if (blogPostsList !=null) {
            return blogPostsList.size();
        }
        return 0;
    }





    //viewholder class

    public static  class BlogPostViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView postImage;
        public TextView title_textView,description_textView;


        public BlogPostViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.blog_post_cardView);
            postImage = itemView.findViewById(R.id.post_Image);
            title_textView = itemView.findViewById(R.id.post_title_textView);
            description_textView = itemView.findViewById(R.id.post_desciption_textView);
        }


    }


}
