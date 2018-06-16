package com.himel.androiddeveloper3005.dreamfulbari.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.database.DatabaseReference;
import com.himel.androiddeveloper3005.dreamfulbari.Activity.EmployeeSingleActivity;
import com.himel.androiddeveloper3005.dreamfulbari.Activity.StudentSingleActivity;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Employee;
import com.himel.androiddeveloper3005.dreamfulbari.MyFilter.EmployeeCustomFilter;
import com.himel.androiddeveloper3005.dreamfulbari.MyFilter.StudentCustomFilter;
import com.himel.androiddeveloper3005.dreamfulbari.R;

import java.util.ArrayList;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> implements Filterable {
    private Auth mAuth;
    private DatabaseReference mDatabaseReference;
    private Context context;
    public ArrayList<Employee> employeeArrayList;
    private String number;
    private View mView;
    EmployeeCustomFilter filter;

    public EmployeeAdapter(Context context, ArrayList<Employee> employeeArrayList) {
        this.context = context;
        this.employeeArrayList = employeeArrayList;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mView = inflater.from(parent.getContext()).inflate(R.layout.emploee_item_layout, parent, false);
        EmployeeViewHolder employeeViewHolder = new EmployeeViewHolder(mView);
        return employeeViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeAdapter.EmployeeViewHolder holder, int position) {
        final Employee employee = employeeArrayList.get(position);
        holder.setName("Name : "+employee.getName());
        holder.setEmail("Email : "+employee.getEmail());
        holder.setOrganization("Organization : "+employee.getOrganization());
        holder.setImage(context,employee.getImage());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EmployeeSingleActivity.class);
                intent.putExtra("employeeSingle",employee);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {

        if (employeeArrayList !=null) {
            return employeeArrayList.size();
        }
        return 0;

    }

    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new EmployeeCustomFilter(this, employeeArrayList);
        }
        return filter;
    }

    public class EmployeeViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public ImageView profileImage;
        public TextView username,email,organization;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.employee_card);
            profileImage = itemView.findViewById(R.id.user_image_view);
            username = itemView.findViewById(R.id.employee_name_textview);
            email = itemView.findViewById(R.id.employee_email_textview);
            organization = itemView.findViewById(R.id.employee_organization_name_textview);
        }


        public void setName(String name) {
            username.setText(name);

        }

        public void setEmail(String e) {
            email.setText(e);
        }



        public void setOrganization(String organizationname) {
            organization.setText(organizationname);

        }

        public void setImage(Context cntx ,String image) {
            Glide.with(cntx).load(image).into(profileImage);

        }
    }
}
