package com.example.hbookdemo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.hbookdemo.manager.FontManager;
import com.example.hbookdemo.manager.LanguageManager;

public class HBook extends Application {
    LanguageManager languageManager;
    FontManager fontManager;
    String lang,font;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences nightModePref = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        boolean nightMode = nightModePref.getBoolean("night",false);

        // Set the default night mode based on the saved state
        if(nightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        languageManager = new LanguageManager(this);
        lang = languageManager.getLang();
        languageManager.upddateLanguage(lang);

        fontManager = new FontManager(this);
        font = fontManager.getFont();
        fontManager.changeAppFont(font);

    }
}
