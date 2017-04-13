package gr.bcw.business_card_wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import gr.bcw.business_card_wallet.model.User;
import gr.bcw.business_card_wallet.storage.UserStorageHandler;
import gr.bcw.business_card_wallet.util.TokenUtils;
import gr.bcw.business_card_wallet.util.UserUtils;

/**
 * Created by konstantinos on 5/3/2017.
 */

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_custom);

        TextView activityTitle = (TextView) findViewById(R.id.custom_action_bar);
        activityTitle.setText(R.string.action_bar_main_title);

        setContentView(R.layout.activity_main);

        TextView nameTextView = (TextView) findViewById(R.id.info_name_text_view);
        TextView emailTextView = (TextView) findViewById(R.id.info_email_text_view);

        Button myBusinessCardButton = (Button) findViewById(R.id.myBusinessCardButton);
        Button addedBusinessCardsButton = (Button) findViewById(R.id.addedBusinessCardsButton);
        final Button addBusinessCardButton = (Button) findViewById(R.id.addBusinessCardButton);
        Button createBusinessCardButton = (Button) findViewById(R.id.createBusinessCardButton);

        final CardView cardView = (CardView) findViewById(R.id.card_view);

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

        User theUser = new UserStorageHandler().findUserById(MainActivity.this, id);

        // if users business card id is not 0 hide create business card button
//        if (theUser.getBusinessCardId() != 0) { // if not 0 means user got a business card
//            createBusinessCardButton.setVisibility(View.GONE);

            // Set card view visibility
//            myBusinessCardButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    switch (cardView.getVisibility()) {
//                        case View.VISIBLE:
//                            cardView.setVisibility(View.GONE);
//                            break;
//                        case View.GONE:
//                            cardView.setVisibility(View.VISIBLE);
//                            break;
//                    }
//                }
//            });
//
//        } else { // in that case user does not have a business card
//            cardView.setVisibility(View.GONE);
//        }

        nameTextView.setText(theUser.getFirstName() + " " + theUser.getLastName());

        // Set add business card to wallet button visibility
        addedBusinessCardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (addBusinessCardButton.getVisibility()) {
                    case View.VISIBLE:
                        addBusinessCardButton.setVisibility(View.GONE);
                        break;
                    case View.GONE:
                        addBusinessCardButton.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
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
}
