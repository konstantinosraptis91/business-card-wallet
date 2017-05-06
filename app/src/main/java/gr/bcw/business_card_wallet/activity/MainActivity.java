package gr.bcw.business_card_wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import gr.bcw.business_card_wallet.R;
import gr.bcw.business_card_wallet.fragment.AddedBusinessCardsFragment;
import gr.bcw.business_card_wallet.model.User;
import gr.bcw.business_card_wallet.storage.UserStorageHandler;
import gr.bcw.business_card_wallet.util.TokenUtils;
import gr.bcw.business_card_wallet.util.UserUtils;
import io.realm.Realm;

/**
 * Created by konstantinos on 5/3/2017.
 */

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_custom);

        TextView activityTitle = (TextView) findViewById(R.id.custom_action_bar);
        activityTitle.setText(R.string.action_bar_main_title);

        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();

        final Button addBusinessCardButton = (Button) findViewById(R.id.addBusinessCardButton);
        final Button createBusinessCardButton = (Button) findViewById(R.id.createBusinessCardButton);

        long id = getIntent().getLongExtra("id", -1);

        // User already authenticated
        if (id == -1) {
            // retrieve id from prefs
            if (UserUtils.isIDExist(MainActivity.this)) {
                id = UserUtils.getID(MainActivity.this);
            } else {
                // something went wrong, force logout

                // delete token
                TokenUtils.removeToken(MainActivity.this);

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }

        AddedBusinessCardsFragment addedBusinessCardsFragment = (AddedBusinessCardsFragment) getFragmentManager().findFragmentById(R.id.addedBusinessCardsFragment);
        addedBusinessCardsFragment.attemptGetWallet();

        User theUser = new UserStorageHandler().findUserById(realm, id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
