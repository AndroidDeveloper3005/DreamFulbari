package com.himel.androiddeveloper3005.dreamfulbari.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment>mFragments;
    private ArrayList<String>titles;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        this.mFragments = new ArrayList<>();
        this.titles = new ArrayList<>();

    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    public void addFragment(Fragment fragment, String title){
        mFragments.add(fragment);
        titles.add(title);

        //

    }
}
