package com.example.hbookdemo.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.hbookdemo.R;
import com.example.hbookdemo.activities.MainActivity;
import com.example.hbookdemo.manager.FontManager;
import com.example.hbookdemo.manager.LanguageManager;

public class SettingFragment extends Fragment {

    public static boolean nightMode;
    private Switch themeSwitch, languageSwitch;
    private Spinner fontSpinner;
    private SharedPreferences nightModePref, fontIndexPref;
    private LanguageManager languageManager;
    private FontManager fontManager;
    private String lang;
    private int fontIndex;
    private boolean isFirstLoad = true;
    private Button versionButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        versionButton = view.findViewById(R.id.version_info);
        versionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyDialog();
            }
        });

        themeSwitch = view.findViewById(R.id.switch_theme_switch);
        nightModePref = getActivity().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = nightModePref.getBoolean("night", false);
        if (nightMode) {
            themeSwitch.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        languageManager = new LanguageManager(getActivity());
        languageSwitch = view.findViewById(R.id.switch_language_switch);
        lang = languageManager.getLang();
        if (lang.equals("en")) {
            languageSwitch.setChecked(true);
            languageManager.upddateLanguage(lang);
        }


        fontSpinner = view.findViewById(R.id.select_font_spinner);
        fontIndexPref = getActivity().getSharedPreferences("FONT INDEX", Context.MODE_PRIVATE);
        fontIndex = fontIndexPref.getInt("font index", 1);
        fontManager = new FontManager(getActivity());
        fontSpinner.setSelection(fontIndex);

        fontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isFirstLoad) {
                    String selectedFont = parent.getItemAtPosition(position).toString();
                    fontManager.changeAppFont(selectedFont);
                    fontIndexPref.edit().putInt("font index", position).apply();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
                isFirstLoad = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        themeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nightMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    nightModePref.edit().putBoolean("night", false).apply();
                    nightMode = false;
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    nightModePref.edit().putBoolean("night", true).apply();
                    nightMode = true;
                }
            }
        });


        languageSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LanguageSwitch", "Language Switch Clicked");
                if (lang.equals("vi")) {
                    languageManager.upddateLanguage("en");
                    lang = "en";
                } else {
                    languageManager.upddateLanguage("vi");
                    lang = "vi";
                }
                getActivity().recreate();
            }
        });

        return view;
    }

    public void showMyDialog() {
        // Create the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.app_version_diaglog, null);

        // Set the message text
        TextView messageText = dialogView.findViewById(R.id.message_text);
        messageText.setText("Your message goes here");

        // Set the OK button click listener


        // Set the dialog view and show it
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
        Button okButton = dialogView.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close the dialog
                dialog.dismiss();
            }
        });
    }
}
