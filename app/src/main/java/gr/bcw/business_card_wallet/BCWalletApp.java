package gr.bcw.business_card_wallet;

import android.app.Application;
import android.net.wifi.WifiManager;

import io.realm.Realm;

/**
 * Created by konstantinos on 19/4/2017.
 */

public class BCWalletApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Realm. Should only be done once when the application starts, according to official doc
        Realm.init(this);
        // Get IPV4
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
    }
}
