package gr.bcw.business_card_wallet.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import gr.bcw.business_card_wallet.R;

/**
 * Created by konstantinos on 17/3/2017.
 */

public class SettingsActivity extends AppCompatActivity {

    public static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_custom_2);

        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView activityTitle = (TextView) findViewById(R.id.custom_action_bar);
        activityTitle.setText(R.string.action_bar_settings_title);

        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginActivity.REQUEST_EXIT) {
            if (resultCode == LoginActivity.RESULT_OK) {
                finish();
            }
        }
    }

    public void showHideFragment(final Fragment fragment){

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);

        if (fragment != null) {
            if (fragment.isHidden()) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
        }
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // onBackPressed();
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
