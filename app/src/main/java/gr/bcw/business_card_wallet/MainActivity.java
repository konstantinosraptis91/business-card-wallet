package gr.bcw.business_card_wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import gr.bcw.business_card_wallet.util.PrefUtils;

/**
 * Created by konstantinos on 5/3/2017.
 */

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button logoutButton = (Button) findViewById(R.id.logout_button);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefUtils.removeFromPrefs(MainActivity.this, PrefUtils.PREFS_LOGIN_ID_KEY);
                PrefUtils.removeFromPrefs(MainActivity.this, PrefUtils.PREFS_LOGIN_USERNAME_KEY);
                PrefUtils.removeFromPrefs(MainActivity.this, PrefUtils.PREFS_LOGIN_PASSWORD_KEY);
                PrefUtils.removeFromPrefs(MainActivity.this, PrefUtils.PREFS_LOGIN_FIRST_NAME_KEY);
                PrefUtils.removeFromPrefs(MainActivity.this, PrefUtils.PREFS_LOGIN_LAST_NAME_KEY);

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}
