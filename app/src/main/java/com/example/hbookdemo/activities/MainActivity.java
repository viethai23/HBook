package com.example.hbookdemo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.hbookdemo.R;
import com.example.hbookdemo.adapter.ViewPagerAdapter;
import com.example.hbookdemo.adapter.ViewPagerHomeAdapter;
import com.example.hbookdemo.fragments.BookShelfFragment;
import com.example.hbookdemo.fragments.KeSachFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mNavigationView;
    private ViewPager mViewPager;

    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationView = findViewById(R.id.bottom_nav);
        mViewPager = findViewById(R.id.viewpager);

        setUpViewPager();

        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_trangchu:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.action_timkiem:
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.action_kesach:
                        mViewPager.setCurrentItem(2);
                        break;
                    case R.id.action_caidat:
                        mViewPager.setCurrentItem(3);
                        break;
                    default:
                        mViewPager.setCurrentItem(0);
                }
                return true;
            }
        });

    }

    private void setUpViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT );
        mViewPager.setAdapter(viewPagerAdapter);

    }

}