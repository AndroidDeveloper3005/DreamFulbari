package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himel.androiddeveloper3005.dreamfulbari.Adapter.EmployeeAdapter;
import com.himel.androiddeveloper3005.dreamfulbari.Adapter.StudentAdapter;
import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Employee;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Student;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import com.himel.androiddeveloper3005.dreamfulbari.Util.MyDividerItemDecoration;

import java.util.ArrayList;

public class EmployeeActivity extends AppCompatActivity {
    private RecyclerView employeeListShow;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseRef;
    private Employee employee;
    private ProgressBar bar;
    private FirebaseAuth mAuth;
    private View view;
    private Context mContext;
    private final String TAG ="MainActivity";
    private DatabaseReference employeeRef;
    private ArrayList<Employee> employeeArrayList;
    private ProgressDialog progressDialog;
    private EmployeeAdapter adapter;
    private Toolbar toolbar;
    private FirebaseAuth.AuthStateListener mAtuhListener;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
        initFireBase();
        initView();
        searchEmployee();
        showEmployee();
    }




    private void showEmployee() {

        employeeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                employeeArrayList = new ArrayList<>();
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

                    Employee employee = new Employee(name,email,phone,organization,image,address,currentLoc,gender,blood,profession);
                    if (profession.equals("Job Holder")){
                        employeeArrayList.add(employee);

                    }


                }


                employeeListShow.addItemDecoration(new MyDividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL, 16));
                adapter = new EmployeeAdapter(getApplicationContext(), employeeArrayList);
                employeeListShow.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




    private void searchEmployee() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
        });
    }


    private void initView(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        employeeListShow =(RecyclerView) findViewById(R.id.employeeRecyclerView);
        employeeListShow.setHasFixedSize(true);
        searchView = findViewById(R.id.employeeSearchView);

        LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                true);
        employeeListShow.setLayoutManager(manager);



    }

    public void initFireBase(){
        database = FirebaseDatabase.getInstance();
        employeeRef = database.getReference(Constans.USER_DATABSE_PATH);
    }
}
