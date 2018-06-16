package com.himel.androiddeveloper3005.dreamfulbari.MyFilter;

import android.widget.Filter;
import com.himel.androiddeveloper3005.dreamfulbari.Adapter.StudentAdapter;
import com.himel.androiddeveloper3005.dreamfulbari.Model.Student;
import java.util.ArrayList;

public class StudentCustomFilter extends Filter {

     StudentAdapter studentAdapter;
     ArrayList<Student> studentArrayList;


    public StudentCustomFilter(StudentAdapter studentAdapter, ArrayList<Student> studentArrayList) {
        this.studentAdapter = studentAdapter;
        this.studentArrayList = studentArrayList;
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
            ArrayList<Student> filteredStudent=new ArrayList<>();


            for (int i = 0; i< studentArrayList.size(); i++)
            {
                //CHECK
                if(studentArrayList.get(i).getOrganization().toUpperCase().contains(constraint) )
                {
                    //ADD PLAYER TO FILTERED PLAYERS
                    filteredStudent.add(studentArrayList.get(i));
                }
            }

            results.count=filteredStudent.size();
            results.values=filteredStudent;
        }else
        {
            results.count= studentArrayList.size();
            results.values= studentArrayList;

        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        studentAdapter.studentsList = (ArrayList<Student>) results.values;

        //REFRESH
        studentAdapter.notifyDataSetChanged();

    }
}
