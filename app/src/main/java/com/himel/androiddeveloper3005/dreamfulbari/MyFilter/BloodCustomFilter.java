package com.himel.androiddeveloper3005.dreamfulbari.MyFilter;

import android.widget.Filter;
import com.himel.androiddeveloper3005.dreamfulbari.Adapter.UserAdapterForBloodInfo;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Users;
import java.util.ArrayList;

public class BloodCustomFilter extends Filter {
    UserAdapterForBloodInfo adapter;
    ArrayList<Users>usersArrayList;

    public BloodCustomFilter(UserAdapterForBloodInfo adapter
            , ArrayList<Users> usersArrayList) {
        this.adapter = adapter;
        this.usersArrayList = usersArrayList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        //CHECK CONSTRAINT VALIDITY
        if(constraint != null && constraint.length() >0) {
            //CHANGE TO UPPER
            constraint=constraint.toString().toUpperCase();
            //STORE OUR FILTERED PLAYERS
            ArrayList<Users> filteredBlood=new ArrayList<>();
            for (int i = 0; i< usersArrayList.size(); i++)
            {//CHECK
                if(usersArrayList.get(i).getBloodgroup().toUpperCase().contains(constraint)
                        || usersArrayList.get(i).getLocation().toUpperCase().contains(constraint))
                {//ADD PLAYER TO FILTERED PLAYERS
                    filteredBlood.add(usersArrayList.get(i));
                }
            }
            results.count=filteredBlood.size();
            results.values=filteredBlood;
        }else {
            results.count= usersArrayList.size();
            results.values= usersArrayList;
        }
        return results;
    }
    @Override
    protected void publishResults(CharSequence charSequence, FilterResults results) {
        adapter.usersList = (ArrayList<Users>) results.values;
        //REFRESH
        adapter.notifyDataSetChanged();
    }
}
