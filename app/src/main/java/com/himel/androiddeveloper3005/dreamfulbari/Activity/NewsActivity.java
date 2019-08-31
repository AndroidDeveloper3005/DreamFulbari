package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.himel.androiddeveloper3005.dreamfulbari.Model.BlogPost;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Comment;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import com.himel.androiddeveloper3005.dreamfulbari.Service.MyService;
import com.himel.androiddeveloper3005.dreamfulbari.Util.MyDividerItemDecoration;
import com.himel.androiddeveloper3005.dreamfulbari.Util.ToolBarAndStatusBar;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsActivity extends ToolBarAndStatusBar {
    private android.support.v7.widget.Toolbar toolbar;
    private FloatingActionButton add_post_fab_button;
    private RecyclerView blogListShow,commentListShow;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseRef,mDatabase;
    private BlogPost post;
    private ProgressBar bar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mDatabaseUsers;
    private boolean mProcessLike = false;
    private boolean mProcessComment = false;
    private DatabaseReference mDatabaseLikes;
    private DatabaseReference mDatabaseComment;
    private Handler mHandler;
    private ArrayList<Comment>commentArrayList;
    private String uid,dateTime;
    private ActionBar actionBar ;
    private Toolbar mToolbar;
    private ImageView post_view;
    private DatabaseReference mDatabaseLikesRef,mDatabaseBlogRef;
    private StorageReference mStorageReference;
    private String post_key;
    private ProgressDialog mProgressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        initView();
        initFireBaseAuth();
        startService(new Intent(this, MyService.class));
        this.mHandler = new Handler();
        this.mHandler.postDelayed(m_Runnable,500);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constans.POST_DATABSE_PATH);


        add_post_fab_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postIntent = new Intent(getApplicationContext(), PostActivity.class);
                startActivity(postIntent);
            }
        });
        showData();



    }





    private final Runnable m_Runnable = new Runnable()
    {
        public void run()

        {
            //Toast.makeText(NewsActivity.this,"in runnable",Toast.LENGTH_SHORT).show();

            NewsActivity.this.mHandler.postDelayed(m_Runnable, 500);
        }

    };



    @Override
    protected void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(mAuthStateListener);
        showData();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void logout() {
        mAuth.signOut();

    }

    public void initView() {
        bar = findViewById(R.id.progressBar);
        bar.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        blogListShow = findViewById(R.id.show_blog_Post_recyclerView);
        blogListShow.setHasFixedSize(true);
        blogListShow.setLayoutManager(layoutManager);
        //toolbar
        mToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        // getSupportActionBar().setDiscription(mUserName);
        // add back arrow to toolbar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
        }
        getSupportActionBar().setTitle("News Feed");
/*        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.post_custom_bar,null);
        actionBar.setCustomView(action_bar_view);

        post_view = findViewById(R.id.custom_bar_post);*/
        add_post_fab_button = findViewById(R.id.add_news);

        blogListShow.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && add_post_fab_button.getVisibility() == View.VISIBLE) {
                    add_post_fab_button.hide();
                } else if (dy < 0 && add_post_fab_button.getVisibility() != View.VISIBLE) {
                    add_post_fab_button.show();
                }
            }
        });


    }

    public void initFireBaseAuth(){
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef =FirebaseDatabase.getInstance().getReference().child(Constans.POST_DATABSE_PATH);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child(Constans.USER_DATABSE_PATH);
        mDatabaseRef.keepSynced(true);
        mDatabaseUsers.keepSynced(true);
        mDatabaseLikes = FirebaseDatabase.getInstance().getReference().child(Constans.LIKES);
        mDatabaseComment = FirebaseDatabase.getInstance().getReference().child(Constans.COMMENT);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseLikes.keepSynced(true);
        mDatabaseComment.keepSynced(true);




    }

    public void showData(){
        Query sorting_data = mDatabaseRef.orderByChild("counter");

        FirebaseRecyclerAdapter<BlogPost,BlogViewHolder>fireBaseRecyclerAdapter = new FirebaseRecyclerAdapter<BlogPost, BlogViewHolder>(
                BlogPost.class,
                R.layout.blog_post_row,
                BlogViewHolder.class,
                sorting_data


        ) {
            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, BlogPost model, int position) {

                final String post_key  = getRef(position).getKey().toString();
                dateTime = (model.getDate() + " "+ model.getTime()).toString() ;

                viewHolder.setDiscription(model.getDescription());
                if (model.getImageUri() != null) {
                    viewHolder.set_Image(getApplicationContext(), model.getImageUri());
                }
                viewHolder.setUserName(model.getUsername());
                viewHolder.set_UserImage(getApplicationContext(),model.getUserImage());
                viewHolder.setDateTime(dateTime);
                viewHolder.setLkeButton(post_key);
                viewHolder.countLike(post_key);
                //click user image and Name to see user deatils
                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(NewsActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
                        ConstraintLayout update = mView.findViewById(R.id.update_button);
                        ConstraintLayout delete = mView.findViewById(R.id.delete_button);
                        mBuilder.setView(mView);
                        mDatabaseBlogRef = FirebaseDatabase.getInstance().getReference().child(Constans.POST_DATABSE_PATH);
                        mDatabaseLikesRef = FirebaseDatabase.getInstance().getReference().child(Constans.LIKES);
                        mStorageReference = FirebaseStorage.getInstance().getReference().child(Constans.POST_STOREAGE_PATH);
                        //progress Dialog
                        mProgressDialog = new ProgressDialog(NewsActivity.this);
                        mProgressDialog.setTitle("Loading..");
                        mProgressDialog.setMessage("Please wait ....");
                        mProgressDialog.setCanceledOnTouchOutside(false);

                        final AlertDialog dialog = mBuilder.create();
                        dialog.show();

                        update.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(),PostUpdateActivity.class);
                                intent.putExtra("post_key",post_key);
                                startActivity(intent);
                                dialog.dismiss();

                            }
                        });

                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mProgressDialog.show();
                                mDatabaseBlogRef.child(post_key).removeValue();
                                mDatabaseLikesRef.child(post_key).removeValue();
                                mStorageReference.child(post_key).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mProgressDialog.dismiss();
                                        dialog.dismiss();

                                    }
                                });

                            }


                        });

                        return true ;
                    }
                });




                viewHolder.mLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mProcessLike = true;

                        mDatabaseLikes.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (mProcessLike){

                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){



                                        mDatabaseLikes.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mProcessLike = false;

                                    }

                                    else {

                                        mDatabaseLikes.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("0");
                                        mProcessLike = false;
                                    }

                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });




                    }
                });



                // comment section start form here

                viewHolder.mComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent comments = new Intent(getApplicationContext(),CommentActivity.class);
                        comments.putExtra("post_key",post_key);
                        startActivity(comments);

                    }
                });



                bar.setVisibility(View.GONE);

            }
        };
        blogListShow.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 5));

        blogListShow.setAdapter(fireBaseRecyclerAdapter);


    }



    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        View mView;
        LinearLayout mUserLayout;
        CircleImageView mUserImage;
        ImageButton mLike,mComment;
        TextView like_count,date,username;
        DatabaseReference mDatabaseLike,mDatabaseComment;
        DatabaseReference mDatabase;
        DatabaseReference mDatabaseLikesCounts;
        FirebaseAuth mAuth;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mUserLayout = mView.findViewById(R.id.user_layout);
            mLike =mView.findViewById(R.id.like_button);
            mUserImage =mView.findViewById(R.id.user_image_view);
            username =mView.findViewById(R.id.username_textView);
            mComment =mView.findViewById(R.id.comment_button);
            like_count = mView.findViewById(R.id.like_count);
            date = mView.findViewById(R.id.date_textView);
            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child(Constans.LIKES);
            mDatabaseComment = FirebaseDatabase.getInstance().getReference().child(Constans.COMMENT);
            mAuth =FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference().child(Constans.POST_DATABSE_PATH);
            mDatabaseLikesCounts = FirebaseDatabase.getInstance().getReference().child(Constans.LIKES);
            mDatabaseLike.keepSynced(true);
        }

        public void countLike(final String post_key){

            mDatabaseLikesCounts.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Long number =dataSnapshot.child(post_key).getChildrenCount();
                    like_count.setText( number +" "+"likes");

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }



        public void setLkeButton(final String post_key){
            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                        mLike.setImageResource(R.drawable.like);
                    }
                    else {
                        mLike.setImageResource(R.drawable.dislike);


                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }




        public void setUserName(String username){
            TextView user_name = mView.findViewById(R.id.username_textView);
            user_name.setText(username);
        }

        public void setDiscription(String title){
            TextView postTite = mView.findViewById(R.id.post_title_textView);
            postTite.setText(title);
        }
        public void set_Image(Context cntx, String image){
            ImageView post_Image = mView.findViewById(R.id.post_Image);
            post_Image.setVisibility(View.VISIBLE);
            Glide.with(cntx).load(image)
                    .into(post_Image);

        }

        public void set_UserImage(Context cntx, String userImage){
            ImageView user_Image = mView.findViewById(R.id.user_image_view);

            Glide.with(cntx).load(userImage)
                    .into(user_Image);
        }

        public void setDateTime(String date){
            TextView dateText = mView.findViewById(R.id.date_textView);
            dateText.setText(date);
        }

    }




}
