package com.himel.androiddeveloper3005.dreamfulbari.Adapter;

import android.content.Context;
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
import com.himel.androiddeveloper3005.dreamfulbari.Interface.OnItemClickListener;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Student;
import com.himel.androiddeveloper3005.dreamfulbari.MyFilter.StudentCustomFilter;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> implements Filterable {
    private Auth mAuth;
    private DatabaseReference mDatabaseReference;
    private Context context;
    public ArrayList<Student> studentsList;
    private StudentCustomFilter filter;
    private View mView;
    private OnItemClickListener mListener;

    public StudentAdapter(Context context, ArrayList<Student> students) {
        this.context = context;
        this.studentsList = students;
    }
    //viewholder class
    public   class StudentViewHolder extends RecyclerView.ViewHolder {
        Context mContext;
        ArrayList<Student>arrayList;
        public ImageView profileImage,phoneImage,smsImage;
        public TextView username, details_student;

        public StudentViewHolder(View mView, Context mContext, ArrayList<Student> arrayList) {
            super(mView);
            this.mContext = mContext;
            this.arrayList = arrayList;
            profileImage = mView.findViewById(R.id.user_image_view);
            username = mView.findViewById(R.id.studentname_textview);
            details_student = mView.findViewById(R.id.student_details);
            phoneImage = mView.findViewById(R.id.phone_call_imgeview);
            smsImage = mView.findViewById(R.id.sms_send_imgeview);
            phoneImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemListener(view,getLayoutPosition());

                }
            });
            smsImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemListener(view,getLayoutPosition());
                }
            });
        }
    }

    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        mView = inflater.from(parent.getContext()).inflate(R.layout.student_item_layout, parent, false);
        StudentViewHolder studentViewHolder = new StudentViewHolder(mView,context,studentsList);
        return studentViewHolder;
    }

    @Override
    public void onBindViewHolder(final StudentViewHolder holder, final int position) {
        final Student student = studentsList.get(position);
        String student_Details = student.getCurrentLoc() +"\n"
                +student.getGender() +"\n"
                +student.getEmail()+"\n"
                +student.getAddress();
        holder.username.setText(student.getName());
        holder.details_student.setText(student_Details);
        Glide.with(context).load(student.getImage()).into(holder.profileImage);

    }

    @Override
    public int getItemCount() {
        return studentsList.size();
    }

    public ArrayList<Student> getDataList() {
        return studentsList;
    }

    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new StudentCustomFilter(this, studentsList);
        }
        return filter;
    }


    public void setItemClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }


}
