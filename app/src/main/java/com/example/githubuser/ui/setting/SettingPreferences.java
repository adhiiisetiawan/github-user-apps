package com.example.githubuser.ui.setting;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.githubuser.R;

public class SettingPreferences extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.setting_preferences);
    }
}
