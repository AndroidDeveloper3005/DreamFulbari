package com.himel.androiddeveloper3005.dreamfulbari.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
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
import com.himel.androiddeveloper3005.dreamfulbari.Model.Employee;
import com.himel.androiddeveloper3005.dreamfulbari.MyFilter.EmployeeCustomFilter;
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
    private OnItemClickListener mListener;


    public EmployeeAdapter(Context context, ArrayList<Employee> employeeArrayList) {
        this.context = context;
        this.employeeArrayList = employeeArrayList;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mView = inflater.from(parent.getContext()).inflate(R.layout.emploee_item_layout, parent, false);
        EmployeeViewHolder employeeViewHolder = new EmployeeViewHolder(mView,context,employeeArrayList);
        return employeeViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeAdapter.EmployeeViewHolder holder, int position) {
        final Employee employee = employeeArrayList.get(position);
        String employee_details = employee.getEmail() + "\n"
                + employee.getCurrentLoc() + " \n"
                + employee.getOrganization();
        holder.setName("Name : "+employee.getName());
        holder.setDetails(employee_details);
        holder.setImage(context,employee.getImage());

    }

    @Override
    public int getItemCount() {

        if (employeeArrayList !=null) {
            return employeeArrayList.size();
        }
        return 0;

    }

    public ArrayList<Employee>getDataList(){
        return employeeArrayList;
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

        Context mContext;
        ArrayList<Employee>arrayList;
        public ImageView profileImage,phoneImage,smsImage;
        public TextView username,details;

        public EmployeeViewHolder(View itemView,Context mContext, ArrayList<Employee> arrayList) {
            super(itemView);
            this.mContext = mContext;
            this.arrayList = arrayList;
            profileImage = itemView.findViewById(R.id.user_image_view);
            username = itemView.findViewById(R.id.employee_name_textview);
            details = itemView.findViewById(R.id.employee_details);
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


        public void setName(String name) {
            username.setText(name);

        }

        public void setDetails(String e) {
            details.setText(e);
        }


        public void setImage(Context cntx ,String image) {
            Glide.with(cntx).load(image).into(profileImage);

        }
    }

    public void setItemClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }
}
