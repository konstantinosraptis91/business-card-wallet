package gr.bcw.business_card_wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import gr.bcw.business_card_wallet.util.SettingsPrefUtils;
import gr.bcw.business_card_wallet.util.TokenUtils;

/**
 * Created by konstantinos on 5/3/2017.
 */

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (TokenUtils.isTokenExist(SplashActivity.this) && SettingsPrefUtils.isAutoLoginAllowed(SplashActivity.this)) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }

}
