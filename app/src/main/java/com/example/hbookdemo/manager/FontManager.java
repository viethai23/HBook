package com.example.hbookdemo.manager;

import android.content.Context;
import android.content.SharedPreferences;


import com.example.hbookdemo.R;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class FontManager {
    private static final String FONT_KEY = "fontKey";
    private Context context;
    private SharedPreferences sharedPreferences;

    public FontManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(FONT_KEY,Context.MODE_PRIVATE);
    }

    public void changeAppFont(String fontName) {
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/" + fontName + ".ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
        // Save the font preference for next time
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FONT_KEY, fontName);
        editor.apply();
    }

    public String getFont(){
        return sharedPreferences.getString(FONT_KEY,"Roboto");
    }
}
