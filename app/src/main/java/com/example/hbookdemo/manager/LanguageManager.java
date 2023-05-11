package com.example.hbookdemo.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LanguageManager {
    private Context context;
    private SharedPreferences sharedPreferences;

    public LanguageManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("LANG",Context.MODE_PRIVATE);
    }
    public void upddateLanguage(String countryCode){
        Locale locale = new Locale(countryCode);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration,resources.getDisplayMetrics());
        setLang(countryCode);
    }
    public String getLang(){
        return sharedPreferences.getString("lang","en");
    }
    public void setLang(String countryCode){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lang",countryCode);
        editor.apply();
    }
}
