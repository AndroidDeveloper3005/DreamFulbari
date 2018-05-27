package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Comment;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import com.himel.androiddeveloper3005.dreamfulbari.Util.MyDividerItemDecoration;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends BaseActivity implements View.OnClickListener{

    private RecyclerView commentsList;
    private ImageButton postCommentButton;
    private EditText commentInputText;
    private FirebaseDatabase database;
    private ProgressBar bar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mDatabaseUsers;
    private boolean mProcessComment = false;
    private DatabaseReference mDatabasePostRef;
    private DatabaseReference mDatabaseComment;
    private Handler mHandler;
    private String post_key,user_id;
    private ArrayList<String> setPostcommentIDs;
    private ArrayList<Comment>comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        getToolbar();
        initView();
        initFireBaseAuth();
        this.mHandler = new Handler();

        this.mHandler.postDelayed(m_Runnable,500);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (mAuth.getCurrentUser() == null) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }

            }
        };




    }


    public void initView() {
        bar = findViewById(R.id.progressBar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        commentsList = findViewById(R.id.comment_recyclerview);
        commentsList.setHasFixedSize(true);
        commentsList.setLayoutManager(layoutManager);
        postCommentButton =findViewById(R.id.comment_button);
        commentInputText = findViewById(R.id.et_comment);
        postCommentButton.setOnClickListener(this);


    }

    public void initFireBaseAuth(){
        post_key = getIntent().getExtras().get("post_key").toString();
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child(Constans.USER_DATABSE_PATH);
        mDatabaseUsers.keepSynced(true);
        mDatabaseComment = FirebaseDatabase.getInstance().getReference().child(Constans.COMMENT);
        mDatabaseComment.keepSynced(true);
        mDatabasePostRef =FirebaseDatabase.getInstance().getReference().child(Constans.POST_DATABSE_PATH).child(post_key).child("Comments");
        user_id = mAuth.getCurrentUser().getUid().toString();

    }



    private final Runnable m_Runnable = new Runnable()
    {
        public void run()

        {
            //Toast.makeText(MainActivity.this,"in runnable",Toast.LENGTH_SHORT).show();

            CommentActivity.this.mHandler.postDelayed(m_Runnable, 500);
        }

    };


    @Override
    public void onClick(View v) {
        if (v == postCommentButton){
          //String userComment =  commentInputText.getText().toString();

            mDatabaseUsers.child(user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        String  userName = dataSnapshot.child("name").getValue().toString();
                        String userImage = dataSnapshot.child("image").getValue().toString();
                        validateComment(userName,userImage);
                        commentInputText.setText("");
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });





        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        showComment();
    }

    public void showComment(){
        FirebaseRecyclerAdapter<Comment,CommentViewHolder>firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>

                (
                        Comment.class,
                        R.layout.blog_comment_row,
                        CommentViewHolder.class,
                        mDatabasePostRef

                ) {
            @Override
            protected void populateViewHolder(CommentViewHolder viewHolder, Comment model, int position) {
                viewHolder.setComment(model.getComment());
                viewHolder.setUsername(model.getUsername());
                viewHolder.set_Userimage(getApplicationContext(),model.getUserimage());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());

            }
        };

        commentsList.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));

        commentsList.setAdapter(firebaseRecyclerAdapter);

    }



    public static class CommentViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public CommentViewHolder(View itemView) {

            super(itemView);
             mView = itemView ;
        }

        public void setTime(String time){
            TextView timeTextView = itemView.findViewById(R.id.time_textView);
            timeTextView.setText("Time :"+time);

        }

        public void setDate(String date){
            TextView dateTextView = itemView.findViewById(R.id.date_textView);
            dateTextView.setText("Comment On :"+date);

        }

        public void setComment(String comment){
            TextView commenttextView = itemView.findViewById(R.id.comment_textView);
            commenttextView.setText(comment);

        }

        public void set_Userimage(Context mContx,String userimage){

            CircleImageView userImageView = itemView.findViewById(R.id.user_image_view);
            Glide.with(mContx).load(userimage)
                    .into(userImageView);

        }

        public void setUsername(String username){
            TextView userNameTextView = itemView.findViewById(R.id.username_textView);
            userNameTextView.setText(username);

        }
    }





    private void validateComment(String userName,String userImage) {
        String commentText = commentInputText.getText().toString();
        if (TextUtils.isEmpty(commentText)){
            Toast.makeText(this, "Enter Your Comment Please...", Toast.LENGTH_SHORT).show();
        }
        else {

            Calendar getDate = Calendar.getInstance();
            SimpleDateFormat curentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveDate = curentDate.format(getDate.getTime());
            Calendar get_Time = Calendar.getInstance();
            SimpleDateFormat curentTime = new SimpleDateFormat("HH:mm");
            final String saveTime = curentTime.format(get_Time.getTime());

            final String randomCommentKey = user_id + saveDate + saveTime;

            HashMap commentsMap  = new HashMap();
            commentsMap.put("uid",user_id);
            commentsMap.put("comment",commentText);
            commentsMap.put("date",saveDate);
            commentsMap.put("time",saveTime);
            commentsMap.put("username",userName);
            commentsMap.put("userimage",userImage);

            mDatabasePostRef.child(randomCommentKey).updateChildren(commentsMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                Toast.makeText(CommentActivity.this, "You Have Commented Successfully. ", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(CommentActivity.this, "There Was An Error.Please Wait... ", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });


        }
    }
}
