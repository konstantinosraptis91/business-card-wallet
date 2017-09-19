package gr.bcw.business_card_wallet.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import gr.bcw.business_card_wallet.R;
import gr.bcw.business_card_wallet.fragment.CreateBusinessCardFragment2;

/**
 * Created by konstantinos on 19/9/2017.
 */

public class BusinessCardActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_card_2);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM);
//        actionBar.setCustomView(R.layout.action_bar_custom);
//
//        TextView activityTitle = (TextView) findViewById(R.id.custom_action_bar);
//        activityTitle.setText(R.string.action_bar_create_business_card_title);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            CreateBusinessCardFragment2 firstFragment = new CreateBusinessCardFragment2();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }
}
