package com.example.hbookdemo.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.hbookdemo.fragments.MoiCapNhatFragment;
import com.example.hbookdemo.fragments.TheLoaiFragment;
import com.example.hbookdemo.fragments.TruyenFullFragment;
import com.example.hbookdemo.fragments.TruyenHotFragment;

public class ViewPagerHomeAdapter extends FragmentStatePagerAdapter {

    public ViewPagerHomeAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MoiCapNhatFragment();
            case 1:
                return new TruyenFullFragment();
            case 2:
                return new TruyenHotFragment();
            case 3:
                return new TheLoaiFragment();
            default:
                return new MoiCapNhatFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Mới cập nhật";
                break;
            case 1:
                title = "Truyện full";
                break;
            case 2:
                title = "Truyện hot";
                break;
            case 3:
                title = "Thể loại";
                break;
        }
        return title;
    }
}
