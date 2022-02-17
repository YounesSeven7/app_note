package com.example.noteapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.example.noteapp.R;

import java.util.Locale;

public class SharedPreferenceHelper {
    // KEYS
    static final String KEY_LIST_ITEM_VISIBILITY = "LIST_ITEM_VISIBILITY";
    private static final String KEY_THEME = "KEY_THEME";
    private static final String KEY_LANGUAGE = "KEY_LANGUAGE";
    // value
    public static final String LIGHT_THEME = "LIGHT_THEME";
    public static final String DARK_THEME = "DARK_THEME";
    public static final String SYSTEM_DEFAULT_THEME = "SYSTEM_DEFAULT_THEME";
    public static final String ARABIC_LANGUAGE = "ar";
    public static final String ENGLISH_LANGUAGE = "en";
    public static final String SYSTEM_LANGUAGE = "SYSTEM_LANGUAGE";
    static SharedPreferences sp;
    static SharedPreferences.Editor editor;
    static void setSpInstance (Context context) {
        if (sp == null)
            sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    static void setSpEditorInstance () {
        if (editor == null) {
            editor = sp.edit();
        }
    }

    public static int getSystemTheme(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String systemThem = sp.getString(context.getString(R.string.key_them_list), SYSTEM_DEFAULT_THEME);
        if (systemThem.equals(context.getString(R.string.system_default))) {
            return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        } else if (systemThem.equals(context.getString(R.string.light))) {
            return AppCompatDelegate.MODE_NIGHT_NO;
        } else if (systemThem.equals(context.getString(R.string.dark))) {
            return AppCompatDelegate.MODE_NIGHT_YES;
        }
        return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    }

    public static void saveListItemVisibilitySetting(boolean listItemVisibility, Context context) {
        setSpInstance(context);
        setSpEditorInstance();
        editor.putBoolean(KEY_LIST_ITEM_VISIBILITY, listItemVisibility);
        editor.apply();
    }

    public static boolean getListItemVisibilitySetting(Context context) {
        setSpInstance(context);
        return sp.getBoolean(KEY_LIST_ITEM_VISIBILITY, true);
    }

    public static void saveThemeSetting(String theme,Context context) {
        setSpInstance(context);
        setSpEditorInstance();
        editor.putString(KEY_THEME, theme);
        editor.apply();
    }


    public static int getThemeSetting(Context context) {
        setSpInstance(context);
        String them = sp.getString(KEY_THEME, SYSTEM_DEFAULT_THEME);
        switch (them) {
            case SYSTEM_DEFAULT_THEME:
                return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            case LIGHT_THEME:
                return AppCompatDelegate.MODE_NIGHT_NO;
            case DARK_THEME:
                return AppCompatDelegate.MODE_NIGHT_YES;
        }
        return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    }

    public static void saveLanguageSetting(String language, Context context) {
        setSpInstance(context);
        setSpEditorInstance();
        editor.putString(KEY_LANGUAGE, language);
        editor.commit();
    }

    public static String getLanguageSettingToSettingActivity(Context context) {
        setSpInstance(context);
        return sp.getString(KEY_LANGUAGE, SYSTEM_LANGUAGE);
    }

    public static String getLanguageSetting(Context context) {
        setSpInstance(context);
        if (sp.getString(KEY_LANGUAGE, SYSTEM_LANGUAGE).equals(SYSTEM_LANGUAGE)) {
            return Resources.getSystem().getConfiguration().locale.getLanguage();
        }
        return sp.getString(KEY_LANGUAGE, SYSTEM_LANGUAGE);
    }
}