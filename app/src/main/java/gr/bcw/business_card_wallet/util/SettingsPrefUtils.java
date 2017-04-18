package gr.bcw.business_card_wallet.util;

import android.content.Context;

import gr.bcw.business_card_wallet.fragment.SettingsFragmentPrefs;

/**
 * Created by konstantinos on 18/4/2017.
 */

public class SettingsPrefUtils extends PrefUtils {

    public static boolean isAutoLoginAllowed(Context context) {
        return getFromPrefs(context, SettingsFragmentPrefs.PREFS_AUTO_LOGIN_KEY, false);
    }

}
