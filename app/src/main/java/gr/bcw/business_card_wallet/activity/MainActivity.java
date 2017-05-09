package gr.bcw.business_card_wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import gr.bcw.business_card_wallet.R;
import gr.bcw.business_card_wallet.fragment.AddedBusinessCardsFragment;
import gr.bcw.business_card_wallet.fragment.MyBusinessCardsFragment;
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
    private ViewPager viewPager;
    private BusinessCardPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_custom);

        TextView activityTitle = (TextView) findViewById(R.id.custom_action_bar);
        activityTitle.setText(R.string.action_bar_main_title);

        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();
        TextView mainInfoView = (TextView) findViewById(R.id.mainInfoTextView);
        pagerAdapter = new BusinessCardPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);

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

        User theUser = new UserStorageHandler().findUserById(realm, id);
        mainInfoView.setText(User.printUser(theUser));
    }

    public static class BusinessCardPagerAdapter extends FragmentStatePagerAdapter {

        public BusinessCardPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment;

            switch (position) {
                case 0:
                    fragment = new MyBusinessCardsFragment();
                    break;
                case 1:
                    fragment = new AddedBusinessCardsFragment();
                    break;
                default:
                    fragment = null;
            }

            if (fragment == null) {
                throw new RuntimeException();
            }

            Bundle args = new Bundle();
            args.putInt(AddedBusinessCardsFragment.ARG_OBJECT, position + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
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
