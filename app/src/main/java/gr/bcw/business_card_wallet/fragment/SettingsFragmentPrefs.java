package gr.bcw.business_card_wallet.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import gr.bcw.business_card_wallet.R;

/**
 * Created by konstantinos on 17/3/2017.
 */

public class SettingsFragmentPrefs extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
