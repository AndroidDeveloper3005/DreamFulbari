package com.himel.androiddeveloper3005.dreamfulbari.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.himel.androiddeveloper3005.dreamfulbari.Activity.StudentSingleActivity;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Student;
import com.himel.androiddeveloper3005.dreamfulbari.MyFilter.StudentCustomFilter;
import com.himel.androiddeveloper3005.dreamfulbari.R;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> implements Filterable {
    private Auth mAuth;
    private DatabaseReference mDatabaseReference;
    private Context context;
    public ArrayList<Student> studentsList;
    private String number;
    private View mView;
    StudentCustomFilter filter;


    public StudentAdapter(Context context, ArrayList<Student> students) {
        this.context = context;
        this.studentsList = students;


    }


    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mView = inflater.from(parent.getContext()).inflate(R.layout.student_item_layout, parent, false);
        StudentViewHolder studentViewHolder = new StudentViewHolder(mView);
        return studentViewHolder;
    }

    @Override
    public void onBindViewHolder(final StudentViewHolder holder, final int position) {

        final Student student = studentsList.get(position);

        holder.setName("Name : "+student.getName());
       // holder.setPhone(student.getPhone());
        holder.setOrganization("Organization : "+student.getOrganization());
        holder.setEmail("Email : "+student.getEmail());
        holder.setImage(context, student.getImage());
        //number = getphoneNumber.getPhone().toString();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StudentSingleActivity.class);
                intent.putExtra("studentSingle",student);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);


            }
        });


    }

    @Override
    public int getItemCount() {


        if (studentsList !=null) {
            return studentsList.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new StudentCustomFilter(this, studentsList);
        }
        return filter;
    }


    //viewholder class
    public static  class StudentViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public ImageView profileImage;
        public TextView username,email,phone,organization;



        public StudentViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.student_card);
            profileImage = itemView.findViewById(R.id.user_image_view);
            username = itemView.findViewById(R.id.studentname_textview);
            email = itemView.findViewById(R.id.email_textview);
            organization = itemView.findViewById(R.id.organization_name_textview);

        }



        public void setName(String name) {
            username.setText(name);

        }

        public void setEmail(String e) {
            email.setText(e);
        }



        public String setPhone(String p) {
            phone.setText(p);
            return p;
        }



        public void setOrganization(String organizationname) {
            organization.setText(organizationname);

        }



        public void setImage(Context cntx ,String image) {
            Glide.with(cntx).load(image).into(profileImage);

        }



    }


}
