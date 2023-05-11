package com.example.hbookdemo.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.hbookdemo.R;
import com.example.hbookdemo.fragments.KeSachFragment;
import com.example.hbookdemo.fragments.LichSuFragment;

public class ViewPagerBookShelfAdapter extends FragmentStatePagerAdapter {

    private Context context;
    public ViewPagerBookShelfAdapter(@NonNull FragmentManager fm, int behavior, Context context) {
        super(fm, behavior);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new LichSuFragment();
            case 1:
                return new KeSachFragment();
            default:
                return new LichSuFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.lichSu);
            case 1:
                return context.getString(R.string.keSach);
            default:
                return null;
        }
    }
}
