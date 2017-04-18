package gr.bcw.business_card_wallet.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;

import gr.bcw.business_card_wallet.R;
import gr.bcw.business_card_wallet.SettingsActivity;

/**
 * Created by konstantinos on 17/3/2017.
 */

public class SettingsFragmentPrefs extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String PREFS_AUTO_LOGIN_KEY = "pref_key_auto_login";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(PREFS_AUTO_LOGIN_KEY)) {
            Preference autoLoginPref = findPreference(key);
            Log.d(SettingsActivity.TAG, "auto login pref is: " + sharedPreferences.getBoolean(key, false));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
