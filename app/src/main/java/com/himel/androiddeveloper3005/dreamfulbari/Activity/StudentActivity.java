package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himel.androiddeveloper3005.dreamfulbari.Adapter.StudentAdapter;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Student;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import com.himel.androiddeveloper3005.dreamfulbari.Util.MyDividerItemDecoration;

import java.util.ArrayList;

public class StudentActivity extends AppCompatActivity {
    private RecyclerView studentListShow;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseRef;
    private Student student;
    private ProgressBar bar;
    private FirebaseAuth mAuth;
    private View view;
    private Context mContext;
    private final String TAG ="MainActivity";
    private DatabaseReference studentRef;
    private ArrayList<Student> studentArrayList;
    private ProgressDialog progressDialog;
    private StudentAdapter adapter;
    private Toolbar toolbar;
    private FirebaseAuth.AuthStateListener mAtuhListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        initView();
        initFireBase();
        showStudent();
    }

    private void showStudent() {

        studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                studentArrayList = new ArrayList<>();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    String name=snapshot.child(Constans.USER_NAME).getValue().toString();
                    String phone=snapshot.child(Constans.USER_PHONE).getValue().toString();
                    String address=snapshot.child(Constans.USER_ADDRESS).getValue().toString();
                    String email=snapshot.child(Constans.EMAIL).getValue().toString();
                    String organization=snapshot.child(Constans.ORGANIZATION).getValue().toString();
                    String gender=snapshot.child(Constans.GENDER).getValue().toString();
                    String currentLoc=snapshot.child(Constans.CURRENT_LOCATION).getValue().toString();
                    String image=snapshot.child(Constans.USER_IMAGE).getValue().toString();
                    String blood=snapshot.child(Constans.BLOODGROUP).getValue().toString();
                    String profession=snapshot.child(Constans.USER_PROFESSION).getValue().toString();

                    Student student = new Student(name,email,phone,organization,image,address,currentLoc,gender,blood,profession);
                    if (profession.equals("Student")){
                        studentArrayList.add(student);

                    }


                }


                studentListShow.addItemDecoration(new MyDividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL, 16));
                adapter = new StudentAdapter(getApplicationContext(), studentArrayList);
                studentListShow.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void initView(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        studentListShow =(RecyclerView) findViewById(R.id.studentRecyclerView);
        studentListShow.setHasFixedSize(true);
        //guideSearch = findViewById(R.id.guide_search_view);

        LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                true);
        studentListShow.setLayoutManager(manager);



    }

    public void initFireBase(){
        database = FirebaseDatabase.getInstance();
        studentRef = database.getReference(Constans.USER_DATABSE_PATH);
    }


}
