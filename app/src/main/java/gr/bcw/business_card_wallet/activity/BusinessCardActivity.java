package gr.bcw.business_card_wallet.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import gr.bcw.business_card_wallet.R;
//import gr.bcw.business_card_wallet.fragment.CreateBusinessCardFragment;
import gr.bcw.business_card_wallet.fragment.SearchBusinessCardFragment;

/**
 * Created by konstantinos on 11/9/2017.
 */

public class BusinessCardActivity extends AppCompatActivity {

    public static final String TAG = BusinessCardActivity.class.getSimpleName();
    private static final String FRAGMENT_TYPE = "FRAGMENT_TYPE";

    public enum BusinessCardFragmentType {

//        CREATE_BC(FRAGMENT_TYPE, CreateBusinessCardFragment.class.getSimpleName()),
        SEARCH_BC(FRAGMENT_TYPE, SearchBusinessCardFragment.class.getSimpleName());

        private String type;
        private String value;

        BusinessCardFragmentType(String type, String value) {
            this.type = type;
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_custom);

        TextView activityTitle = (TextView) findViewById(R.id.custom_action_bar);

        setContentView(R.layout.activity_business_card);

        // Extract data here from bundle
        Bundle b = getIntent().getExtras();

        if (b.containsKey(FRAGMENT_TYPE)) {

            String value = b.getString(FRAGMENT_TYPE);

            // create bc fragment conf here
//            if (value.equals(BusinessCardFragmentType.CREATE_BC.getValue())) {
//
//                activityTitle.setText(R.string.action_bar_create_business_card_title);
//                findViewById(R.id.search_business_card_fragment_1).setVisibility(View.GONE);
//
//            }
            // search bc fragment conf here
//            else if (value.equals(BusinessCardFragmentType.SEARCH_BC.getValue())) {
              if (value.equals(BusinessCardFragmentType.SEARCH_BC.getValue())) {

                activityTitle.setText(R.string.action_bar_search_business_card_title);
                findViewById(R.id.create_business_card_fragment_1).setVisibility(View.GONE);

            }

        } else {
            return;
        }
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }
}
