package gr.bcw.business_card_wallet.util;

import android.content.Context;

/**
 * Created by konstantinos on 18/3/2017.
 */

public class TokenUtils extends PrefUtils {

    private static final String PREFS_TOKEN_KEY = "__TOKEN__" ;

    public static boolean isTokenExist(Context context) {
        String token = PrefUtils.getFromPrefs(context, PREFS_TOKEN_KEY, null);
        return !(token == null);
    }

    public static void saveToken(Context context, String token) {
        PrefUtils.saveToPrefs(context, PREFS_TOKEN_KEY, token);
    }

    public static String getToken(Context context) {
        return PrefUtils.getFromPrefs(context, PREFS_TOKEN_KEY, null);
    }

    public static void removeToken(Context context) {
        PrefUtils.removeFromPrefs(context, PREFS_TOKEN_KEY);
    }

}
