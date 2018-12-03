package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.Model.BlogPost;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Comment;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import com.himel.androiddeveloper3005.dreamfulbari.Util.MyDividerItemDecoration;
import com.himel.androiddeveloper3005.dreamfulbari.Util.ToolBarAndStatusBar;

import java.util.ArrayList;

public class NewsActivity extends ToolBarAndStatusBar {
    private android.support.v7.widget.Toolbar toolbar;
    private FloatingActionButton fab;
    private RecyclerView blogListShow,commentListShow;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseRef;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        inittoolBar();
        initView();
        initFireBaseAuth();
        this.mHandler = new Handler();

        this.mHandler.postDelayed(m_Runnable,500);

        /*mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (mAuth.getCurrentUser() == null) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }

            }
        };*/


        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constans.POST_DATABSE_PATH);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Upload Your Post", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_manu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logOutItem){
            logout();
        }
        else if (item.getItemId() == R.id.Account_Setup){
            Intent accountIntent = new Intent(getApplicationContext(),UserAccountSetupActivity.class);
            accountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(accountIntent);
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



    }

    public void initFireBaseAuth(){
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef =FirebaseDatabase.getInstance().getReference().child(Constans.POST_DATABSE_PATH);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child(Constans.USER_DATABSE_PATH);
        mDatabaseRef.keepSynced(true);
        mDatabaseUsers.keepSynced(true);
        mDatabaseLikes = FirebaseDatabase.getInstance().getReference().child(Constans.LIKES);
        mDatabaseComment = FirebaseDatabase.getInstance().getReference().child(Constans.COMMENT);
        mDatabaseLikes.keepSynced(true);
        mDatabaseComment.keepSynced(true);




    }

    public void showData(){

        FirebaseRecyclerAdapter<BlogPost,BlogViewHolder>fireBaseRecyclerAdapter = new FirebaseRecyclerAdapter<BlogPost, BlogViewHolder>(
                BlogPost.class,
                R.layout.blog_post_row,
                BlogViewHolder.class,
                mDatabaseRef


        ) {
            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, BlogPost model, int position) {

                final String post_key = getRef(position).getKey().toString();
                String dateTime =/*" has been posted on " +*/ (model.getDate() + " "+ model.getTime()).toString() ;

                viewHolder.setTitle(model.getTitle());
                viewHolder.set_Description(model.getDescription());
                viewHolder.set_Image(getApplicationContext(),model.getImageUri());
                viewHolder.setUserName(model.getUsername());
                viewHolder.set_UserImage(getApplicationContext(),model.getUserImage());
                viewHolder.setDateTime(dateTime);
                viewHolder.setLkeButton(post_key);
                viewHolder.countLike(post_key);

                /*viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(NewsActivity.this, "Click Post  "+post_key, Toast.LENGTH_SHORT).show();
                    }
                });*/

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
                //for post edit
                viewHolder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(NewsActivity.this, "Edited", Toast.LENGTH_SHORT).show();

                    }
                });

                //for post delete
                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(NewsActivity.this, "Deleted", Toast.LENGTH_SHORT).show();


                    }
                });



                bar.setVisibility(View.GONE);

            }
        };
        blogListShow.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 10));

        blogListShow.setAdapter(fireBaseRecyclerAdapter);


    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        View mView;
        Button edit,delete;
        ImageButton mLike,mComment;
        TextView like_count,date;
        DatabaseReference mDatabaseLike,mDatabaseComment;
        DatabaseReference mDatabase;
        DatabaseReference mDatabaseLikesCounts;
        FirebaseAuth mAuth;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mLike =mView.findViewById(R.id.like_button);
            mComment =mView.findViewById(R.id.comment_button);
            like_count = mView.findViewById(R.id.like_count);
            date = mView.findViewById(R.id.date_textView);
            edit = mView.findViewById(R.id.button_edit);
            delete = mView.findViewById(R.id.button_delete);
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

        public void setTitle(String title){
            TextView postTite = mView.findViewById(R.id.post_title_textView);
            postTite.setText(title);
        }

        public void set_Description(String description){
            TextView postDescrition = mView.findViewById(R.id.post_desciption_textView);
            postDescrition.setText(description);
        }
        public void set_Image(Context cntx, String image){
            ImageView post_Image = mView.findViewById(R.id.post_Image);

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
