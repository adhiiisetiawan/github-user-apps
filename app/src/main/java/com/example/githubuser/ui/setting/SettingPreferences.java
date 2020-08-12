package com.example.githubuser.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.example.githubuser.R;
import com.example.githubuser.receiver.AlarmReceiver;

public class SettingPreferences extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Preference languagePreferences;
    private SwitchPreference dailyPreferences;
    private String LANGUAGE;
    private String REMINDER;
    private AlarmReceiver alarmReceiver;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.setting_preferences);

        LANGUAGE = getResources().getString(R.string.key_language);
        REMINDER = getResources().getString(R.string.key_reminder);

        alarmReceiver = new AlarmReceiver();

        languagePreferences = (Preference) findPreference(LANGUAGE);
        dailyPreferences = (SwitchPreference) findPreference(REMINDER);

        languagePreferences.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intentSettings = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(intentSettings);
                return true;
            }
        });

        SharedPreferences sh = getPreferenceManager().getSharedPreferences();
        dailyPreferences.setChecked(sh.getBoolean(REMINDER, false));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(REMINDER)){
            boolean check = sharedPreferences.getBoolean(REMINDER, false);

            if (check){
                alarmReceiver.setRepeating(requireContext());
                Toast.makeText(getContext(), "Enable", Toast.LENGTH_SHORT).show();
            } else {
                alarmReceiver.cancelAlarm(requireContext());
                Toast.makeText(getContext(), "Disable", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
