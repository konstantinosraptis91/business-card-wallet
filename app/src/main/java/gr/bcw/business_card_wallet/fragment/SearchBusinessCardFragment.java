package gr.bcw.business_card_wallet.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gr.bcw.business_card_wallet.R;
import gr.bcw.business_card_wallet.adapter.BusinessCardAdapter;
import gr.bcw.business_card_wallet.model.BusinessCard;
import gr.bcw.business_card_wallet.webservice.BusinessCardWebService;
import gr.bcw.business_card_wallet.webservice.BusinessCardWebServiceImpl;
import gr.bcw.business_card_wallet.webservice.exception.WebServiceException;
import io.realm.Realm;

/**
 * Created by konstantinos on 14/9/2017.
 */

public class SearchBusinessCardFragment extends Fragment {

    private static final String TAG = SearchBusinessCardFragment.class.getSimpleName();

    // Local DB
    private Realm realm;

    private BusinessCardAdapter cardAdapter;
    private SearchTask searchTask = null;

    // UI references
    private View progressView;
    private ListView cardListView;
    private EditText nameEditText;
    private ImageButton searchButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search_business_card, container, false);
        cardListView = (ListView) rootView.findViewById(R.id.businessCardsListView);
        cardAdapter = new BusinessCardAdapter(getActivity(), new ArrayList<BusinessCard>(), BusinessCardAdapter.CardType.SEARCH_BUSINESS_CARD);
        cardListView.setAdapter(cardAdapter);
        progressView = rootView.findViewById(R.id.progress);
        nameEditText = (EditText) rootView.findViewById(R.id.name_editText);

        // Change action button text
        Button saveButton = (Button) rootView.findViewById(R.id.button_action);
        saveButton.setText(R.string.button_action_save);

        searchButton = (ImageButton) rootView.findViewById(R.id.search_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSearch();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return rootView;
    }

    private void attemptSearch() {
        if (searchTask != null) {
            return;
        }

        // reset errors
        nameEditText.setError(null);

        // extract values
        String name = nameEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid First Name
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError(getString(R.string.error_field_required));
            focusView = nameEditText;
            cancel = true;
        }

        String firstName;
        String lastName = null;

        String[] results = name.split("\\s+");

        firstName = results[0];

        if (results.length > 1) {
            lastName = results[1];
        } else {
            nameEditText.setError(getString(R.string.error_invalid_name));
            focusView = nameEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            searchTask = new SearchTask(firstName, lastName);
            searchTask.execute(new BusinessCardWebServiceImpl());
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            cardListView.setVisibility(show ? View.GONE : View.VISIBLE);
            cardListView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    cardListView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            cardListView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class SearchTask extends AsyncTask<BusinessCardWebService, Void, List<BusinessCard>> {

        private String message = "";
        private String firstName;
        private String lastName;

        public SearchTask(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        @Override
        protected List<BusinessCard> doInBackground(BusinessCardWebService... params) {
            BusinessCardWebService service = params[0];
            List<BusinessCard> cards = null;

            try {
                cards = service.findByUserName(firstName, lastName);

                for (BusinessCard c : cards) {
                    Log.i(TAG, c.toString());
                }

            } catch (WebServiceException ex) {
                message = ex.getMessage();
            }

            return cards;
        }

        @Override
        protected void onPostExecute(List<BusinessCard> cards) {
            searchTask = null;
            showProgress(false);

            if (cards != null) {

                cardAdapter.clear();

                for (BusinessCard c : cards) {
                    cardAdapter.add(c);
                }

                cardAdapter.notifyDataSetChanged();

                // hide soft keyboard
                // hide soft keyboard
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(searchButton.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            } else {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            searchTask = null;
            showProgress(false);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}
