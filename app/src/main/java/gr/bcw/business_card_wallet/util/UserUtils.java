package gr.bcw.business_card_wallet.util;

import android.content.Context;

/**
 * Created by konstantinos on 25/3/2017.
 */

public class UserUtils extends PrefUtils {

    private static final String PREFS_ID_KEY = "_ID_";
    private static final String PREFS_AUTO_LOGIN_KEY = "_AUTO_LOGIN_";

    public enum PrefState {

        TRUE(1), FALSE(0);

        private long state;

        PrefState(long state) {
            this.state = state;
        }

        public long getState() {
            return state;
        }
    }

    // ID
    public static boolean isIDExist(Context context) {
        long id = PrefUtils.getFromPrefs(context, PREFS_ID_KEY, -1);
        return !(id == -1);
    }

    public static void saveID(Context context, long id) {
        PrefUtils.saveToPrefs(context, PREFS_ID_KEY, id);
    }

    public static long getID(Context context) {
        return PrefUtils.getFromPrefs(context, PREFS_ID_KEY, -1);
    }

    public static void removeID(Context context) {
        PrefUtils.removeFromPrefs(context, PREFS_ID_KEY);
    }

    // Auto Login
    public static boolean isAutoLoginExist(Context context) {
        long autoLogin = PrefUtils.getFromPrefs(context, PREFS_AUTO_LOGIN_KEY, -1);
        return !(autoLogin == -1);
    }

    public static void saveAutoLogin(Context context, PrefState autoLogin) {
        PrefUtils.saveToPrefs(context, PREFS_AUTO_LOGIN_KEY, autoLogin.getState());
    }

    public static PrefState getAutoLogin(Context context) {
        long autoLogin = PrefUtils.getFromPrefs(context, PREFS_AUTO_LOGIN_KEY, - 1);
        return autoLogin == 1 ? PrefState.TRUE : PrefState.FALSE;
    }

    public static void removeAutoLogin(Context context) {
        PrefUtils.removeFromPrefs(context, PREFS_AUTO_LOGIN_KEY);
    }

}
