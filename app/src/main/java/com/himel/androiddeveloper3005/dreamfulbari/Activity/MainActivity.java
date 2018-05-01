package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himel.androiddeveloper3005.dreamfulbari.Adapter.BlogPostAdapter;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.Model.BlogPost;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import com.himel.androiddeveloper3005.dreamfulbari.Util.MyDividerItemDecoration;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    private android.support.v7.widget.Toolbar toolbar;
    private FloatingActionButton fab;
    private RecyclerView blogListShow;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseRef;
    private ArrayList<BlogPost> blogPost;
    private BlogPostAdapter adapter;
    private BlogPost post;
    private ProgressBar bar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mDatabaseUsers;
    private boolean mProcessLike = false;
    private DatabaseReference mDatabaseLikes;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

   /*     if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }*/
        setContentView(R.layout.activity_main);

        getToolbar();
        initView();
        initFireBaseAuth();
        this.mHandler = new Handler();

        this.mHandler.postDelayed(m_Runnable,8000);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (mAuth.getCurrentUser() == null) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }

            }
        };


       // initLoader();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(Constans.POST_DATABSE_PATH);

       // checkUser();

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


       // showDataOnRecyclerView();
        showData();

    }

    private final Runnable m_Runnable = new Runnable()
    {
        public void run()

        {
            //Toast.makeText(MainActivity.this,"in runnable",Toast.LENGTH_SHORT).show();

            MainActivity.this.mHandler.postDelayed(m_Runnable, 8000);
        }

    };



    @Override
    protected void onStart() {
        super.onStart();
        //showDataOnRecyclerView();

        mAuth.addAuthStateListener(mAuthStateListener);

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
            Intent accountIntent = new Intent(getApplicationContext(),SetupActivity.class);
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
        mDatabaseLikes.keepSynced(true);




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

                final String post_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.set_Description(model.getDescription());
                viewHolder.set_Image(getApplicationContext(),model.getImageUri());
                viewHolder.setUserName(model.getUsername());
                viewHolder.set_UserImage(getApplicationContext(),model.getUserImage());
                viewHolder.setLkeButton(post_key);
               // viewHolder.countLike(post_key);

                /*viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "Click Post  "+post_key, Toast.LENGTH_SHORT).show();
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
                                            viewHolder.like_count.setText(""+dataSnapshot.child(post_key).getChildrenCount());
                                            mProcessLike = false;

                                        }

                                        else {
                                            viewHolder.like_count.setText(""+dataSnapshot.child(post_key).getChildrenCount());
                                            mDatabaseLikes.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("0");
                                            mProcessLike = false;
                                        }

                                    }

                                    //startActivity(new Intent(getApplicationContext(),MainActivity.class));

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });




                    }
                });



                bar.setVisibility(View.GONE);

            }
        };
        blogListShow.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));

        blogListShow.setAdapter(fireBaseRecyclerAdapter);


    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageButton mLike;
        TextView like_count;
        DatabaseReference mDatabaseR;
        DatabaseReference mDatabase;
        DatabaseReference mDatabaseLikesCounts;


        FirebaseAuth mAuth;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mLike =mView.findViewById(R.id.like_button);
            like_count = mView.findViewById(R.id.like_count);
            mDatabaseR = FirebaseDatabase.getInstance().getReference().child(Constans.LIKES);

            mAuth =FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference().child(Constans.POST_DATABSE_PATH);
            mDatabaseLikesCounts = FirebaseDatabase.getInstance().getReference().child(Constans.LIKES);


            mDatabaseR.keepSynced(true);
        }


/*        public void countLike(final String post_key){

            mDatabaseLikesCounts.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String mvalue = dataSnapshot.getKey();
                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                        for (DataSnapshot snap : dataSnapshot.getChildren()) {

                            like_count.setText("" + snap.getChildrenCount());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }*/



        public void setLkeButton(final String post_key){
            mDatabaseR.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                        mLike.setImageResource(R.drawable.likered);
                    }
                    else {
                        mLike.setImageResource(R.drawable.likeblack);


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

    }




}
