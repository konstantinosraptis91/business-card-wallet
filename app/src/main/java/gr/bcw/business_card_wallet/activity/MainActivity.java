package gr.bcw.business_card_wallet.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

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
    private FloatingActionButton fab;

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

        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);

        LinearLayout view = (LinearLayout) tabsStrip.getChildAt(0);
        TextView tab1 = (TextView) view.getChildAt(0);
        TextView tab2 = (TextView) view.getChildAt(1);

        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/MyriadPro-Regular.otf");
        tab1.setTypeface(customFont);
        tab2.setTypeface(customFont);

        // fab here
        fab = (FloatingActionButton) findViewById(R.id.fabMainActivity);

        // add listener to view pager (here we control fab icon update)
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_create_white_48dp));
                        break;
                    case 1:
                        fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_add_white_48dp));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPage = viewPager.getCurrentItem();

                switch (currentPage) {
                    case 0: // here fab event when click and MyBusinessCardsFragment is visible (position 0)

                        break;
                    case 1: // here fab event when click and AddedBusinessCardsFragment is visible (position 1)

                        break;
                }

            }
        });

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

        final int PAGE_COUNT = 2;
        private String tabTitles[] = new String[]{"My Business Cards", "Added Business Cards"};

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
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
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
