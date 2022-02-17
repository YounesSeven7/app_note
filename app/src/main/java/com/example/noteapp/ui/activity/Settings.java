package com.example.noteapp.ui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.noteapp.R;
import com.example.noteapp.Utils.LocaleHelper;
import com.example.noteapp.Utils.SharedPreferenceHelper;
import com.example.noteapp.databinding.ActivitySettingsBinding;
import com.example.noteapp.databinding.AlertDialogChooseLanguageBinding;
import com.example.noteapp.databinding.AlertDialogChooseThemeBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

public class Settings extends AppCompatActivity {
    ActivitySettingsBinding activitySettingsBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.settingsActivity);
        activitySettingsBinding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(activitySettingsBinding.getRoot());
        menu();
        chooseTheme();
        changeLanguage();
    }

    void menu() {
        activitySettingsBinding.settingsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void chooseTheme() {
        View  view = getLayoutInflater().inflate(R.layout.alert_dialog_choose_theme, null);
        AlertDialogChooseThemeBinding binding = AlertDialogChooseThemeBinding.bind(view);
        setValueTheme(binding);
        activitySettingsBinding.llChangeTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view != null) {
                    @SuppressLint("UseCompatLoadingForDrawables") MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Settings.this)
                            .setTitle(R.string.choose_theme)
                            .setView(binding.getRoot())
                            .setBackground(getDrawable(R.drawable.background_border));

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    binding.rgTheme.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @SuppressLint("NonConstantResourceId")
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            switch (checkedId) {
                                case R.id.rb_light:
                                    SharedPreferenceHelper.saveThemeSetting(SharedPreferenceHelper.LIGHT_THEME, getBaseContext());
                                    break;
                                case R.id.rb_dark:
                                    SharedPreferenceHelper.saveThemeSetting(SharedPreferenceHelper.DARK_THEME, getBaseContext());
                                    break;
                                case R.id.rb_systemDefault:
                                    SharedPreferenceHelper.saveThemeSetting(SharedPreferenceHelper.SYSTEM_DEFAULT_THEME, getBaseContext());
                                    break;
                            }
                            alertDialog.dismiss();
                            setValueTheme(binding);
                            AppCompatDelegate.setDefaultNightMode(SharedPreferenceHelper.getThemeSetting(getBaseContext()));
                        }
                    });

                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (view.getParent() != null) {
                                ((ViewGroup)view.getParent()).removeView(view);
                            }
                        }
                    });

                }
            }
        });

    }

    void setValueTheme(AlertDialogChooseThemeBinding binding) {
        switch (SharedPreferenceHelper.getThemeSetting(getBaseContext())) {
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                binding.rbSystemDefault.setChecked(true);
                activitySettingsBinding.sATvValueThem.setText(getString(R.string.system_default));
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                binding.rbLight.setChecked(true);
                activitySettingsBinding.sATvValueThem.setText(getString(R.string.light));
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                binding.rbDark.setChecked(true);
                activitySettingsBinding.sATvValueThem.setText(getString(R.string.dark));
                break;
        }
    }

    void changeLanguage() {
        View  view = getLayoutInflater().inflate(R.layout.alert_dialog_choose_language, null);
        AlertDialogChooseLanguageBinding binding = AlertDialogChooseLanguageBinding.bind(view);
        setValueLanguage(binding);
        activitySettingsBinding.llChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                @SuppressLint("UseCompatLoadingForDrawables") MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Settings.this)
                        .setTitle(R.string.change_color)
                        .setView(view)
                        .setBackground(getDrawable(R.drawable.background_border));

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                binding.rgLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        String language = SharedPreferenceHelper.ARABIC_LANGUAGE;
                        switch (checkedId) {
                            case R.id.rb_arabic:
                               language = SharedPreferenceHelper.ARABIC_LANGUAGE;
                                break;
                            case R.id.rb_english:
                                language = SharedPreferenceHelper.ENGLISH_LANGUAGE;
                                break;
                            case R.id.rb_systemDefault:
                                language = SharedPreferenceHelper.SYSTEM_LANGUAGE;
                                break;
                        }
                        alertDialog.dismiss();
                        SharedPreferenceHelper.saveLanguageSetting(language, getBaseContext());
                        LocaleHelper.setLocale(getBaseContext(), SharedPreferenceHelper.getLanguageSetting(getBaseContext()));
                        Intent intent = new Intent(getApplicationContext(), Settings.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (view.getParent() != null) {
                            ((ViewGroup)view.getParent()).removeView(view);
                        }
                    }
                });
            }
        });

    }

    void setValueLanguage(AlertDialogChooseLanguageBinding binding) {
        switch (SharedPreferenceHelper.getLanguageSettingToSettingActivity(getBaseContext())) {
            case SharedPreferenceHelper.ARABIC_LANGUAGE:
                binding.rbArabic.setChecked(true);
                activitySettingsBinding.sATvValueLanguage.setText(R.string.arabic);
                break;
            case SharedPreferenceHelper.ENGLISH_LANGUAGE:
                binding.rbEnglish.setChecked(true);
                activitySettingsBinding.sATvValueLanguage.setText(R.string.english);
                break;
            case SharedPreferenceHelper.SYSTEM_LANGUAGE:
                binding.rbSystemDefault.setChecked(true);
                activitySettingsBinding.sATvValueLanguage.setText(R.string.system_default);
                break;
        }
    }
}