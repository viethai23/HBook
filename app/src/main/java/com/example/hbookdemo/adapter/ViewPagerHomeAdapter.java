package com.example.hbookdemo.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.hbookdemo.R;
import com.example.hbookdemo.fragments.MoiCapNhatFragment;
import com.example.hbookdemo.fragments.TheLoaiFragment;
import com.example.hbookdemo.fragments.TruyenFullFragment;
import com.example.hbookdemo.fragments.TruyenHotFragment;

public class ViewPagerHomeAdapter extends FragmentStatePagerAdapter {

    private Context context;
    public ViewPagerHomeAdapter(@NonNull FragmentManager fm, int behavior,Context context) {

        super(fm, behavior);
        this.context = context;
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
        switch (position) {
            case 0:
                return context.getString(R.string.moiCapNhat);
            case 1:
                return context.getString(R.string.truyenFull);
            case 2:
                return context.getString(R.string.truyenHot);
            case 3:
                return context.getString(R.string.theLoai);
            default:
                return null;
        }
    }
}
