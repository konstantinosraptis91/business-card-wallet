package gr.bcw.business_card_wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import gr.bcw.business_card_wallet.util.PrefUtils;

/**
 * Created by konstantinos on 5/3/2017.
 */

public class SplashActivity extends AppCompatActivity {

    public static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user already signed in before
        // !!! BAD PRACTISE FOR A PRODUCTION APPLICATION !!! ONLY FOR DEMONSTRATION REASONS
        String loggedInUserName = PrefUtils.getFromPrefs(SplashActivity.this, PrefUtils.PREFS_LOGIN_USERNAME_KEY, null);
        String loggedInPassword = PrefUtils.getFromPrefs(SplashActivity.this, PrefUtils.PREFS_LOGIN_PASSWORD_KEY, null);

        if (loggedInUserName == null || loggedInPassword == null) {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

}
