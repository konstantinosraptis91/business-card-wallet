package gr.bcw.business_card_wallet.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import gr.bcw.business_card_wallet.R;

/**
 * Created by konstantinos on 11/9/2017.
 */

public class BusinessCardActivity extends AppCompatActivity {

    public static final String TAG = BusinessCardActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_custom);

        TextView activityTitle = (TextView) findViewById(R.id.custom_action_bar);
        activityTitle.setText(R.string.action_bar_create_business_card_title);

        setContentView(R.layout.activity_business_card);
    }

}
