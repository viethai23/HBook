package com.example.hbookdemo.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.hbookdemo.fragments.KeSachFragment;
import com.example.hbookdemo.fragments.LichSuFragment;

public class ViewPagerBookShelfAdapter extends FragmentStatePagerAdapter {

    public ViewPagerBookShelfAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
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
        String title = "";
        switch (position) {
            case 0:
                title = "Lịch sử";
                break;
            case 1:
                title = "Kệ sách";
                break;
        }
        return title;
    }
}
