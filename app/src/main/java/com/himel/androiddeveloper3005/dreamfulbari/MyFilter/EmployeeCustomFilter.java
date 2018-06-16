package com.himel.androiddeveloper3005.dreamfulbari.MyFilter;

import android.widget.Filter;

import com.himel.androiddeveloper3005.dreamfulbari.Adapter.EmployeeAdapter;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Employee;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Student;

import java.util.ArrayList;

public class EmployeeCustomFilter extends Filter {

     EmployeeAdapter employeeAdapter;
     ArrayList<Employee> employeeArrayList;


    public EmployeeCustomFilter(EmployeeAdapter employeeAdapter, ArrayList<Employee> employeeLists) {
        this.employeeAdapter = employeeAdapter;
        this.employeeArrayList = employeeLists;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        FilterResults results=new FilterResults();

        //CHECK CONSTRAINT VALIDITY
        if(constraint != null && constraint.length() > 0)
        {
            //CHANGE TO UPPER
            constraint=constraint.toString().toUpperCase();
            //STORE OUR FILTERED PLAYERS
            ArrayList<Employee> filteredStudent=new ArrayList<>();


            for (int i = 0; i< employeeArrayList.size(); i++)
            {
                //CHECK
                if(employeeArrayList.get(i).getOrganization().toUpperCase().contains(constraint)||employeeArrayList.get(i).getName().toUpperCase().contains(constraint) )
                {
                    //ADD PLAYER TO FILTERED PLAYERS
                    filteredStudent.add(employeeArrayList.get(i));
                }
            }

            results.count=filteredStudent.size();
            results.values=filteredStudent;
        }else
        {
            results.count= employeeArrayList.size();
            results.values= employeeArrayList;

        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        employeeAdapter.employeeArrayList = (ArrayList<Employee>) results.values;

        //REFRESH
        employeeAdapter.notifyDataSetChanged();

    }
}
