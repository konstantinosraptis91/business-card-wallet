package gr.bcw.business_card_wallet.util;

import android.content.Context;

/**
 * Created by konstantinos on 25/3/2017.
 */

public class UserUtils extends PrefUtils {

    private static final String PREFS_ID_KEY = "_ID_";

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

}
