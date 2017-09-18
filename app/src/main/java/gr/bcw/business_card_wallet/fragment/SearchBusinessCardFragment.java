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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import gr.bcw.business_card_wallet.R;
import gr.bcw.business_card_wallet.adapter.BusinessCardResponseAdapter;
import gr.bcw.business_card_wallet.model.retriever.BusinessCardResponse;
import gr.bcw.business_card_wallet.util.TokenUtils;
import gr.bcw.business_card_wallet.util.UserUtils;
import gr.bcw.business_card_wallet.webservice.BusinessCardWebService;
import gr.bcw.business_card_wallet.webservice.BusinessCardWebServiceImpl;
import gr.bcw.business_card_wallet.webservice.WalletEntryWebServiceImpl;
import gr.bcw.business_card_wallet.webservice.WebService;
import gr.bcw.business_card_wallet.webservice.exception.WebServiceException;
import io.realm.Realm;

/**
 * Created by konstantinos on 14/9/2017.
 */

public class SearchBusinessCardFragment extends Fragment {

    private static final String TAG = SearchBusinessCardFragment.class.getSimpleName();

    // Local DB
    private Realm realm;

    private BusinessCardResponseAdapter cardAdapter;
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
        cardAdapter = new BusinessCardResponseAdapter(getActivity(), new ArrayList<BusinessCardResponse>(), BusinessCardResponseAdapter.CardType.SEARCH_BUSINESS_CARD);
        cardListView.setAdapter(cardAdapter);
        progressView = rootView.findViewById(R.id.progress);
        nameEditText = (EditText) rootView.findViewById(R.id.name_editText);

        searchButton = (ImageButton) rootView.findViewById(R.id.search_button);
        ImageButton clearButton = (ImageButton) rootView.findViewById(R.id.clear_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSearch();
            }
        });

        // Clear name edit text here
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEditText.setText("");
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

            long id = UserUtils.getID(getActivity());
            String token = TokenUtils.getToken(getActivity());

            searchTask = new SearchTask(firstName, lastName, id, token);
            searchTask.execute(new WebService[] {new BusinessCardWebServiceImpl(), new WalletEntryWebServiceImpl()});
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

    private class SearchTask extends AsyncTask<WebService, Void, List<BusinessCardResponse>> {

        private long userId;
        private String authToken;
        private String message = "";
        private String firstName;
        private String lastName;
        private Set<Long> currentWalletCardsIdSet;

        public SearchTask(String firstName, String lastName, long userId, String authToken) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.userId = userId;
            this.authToken = authToken;
            currentWalletCardsIdSet = new LinkedHashSet<>();
        }

        @Override
        protected List<BusinessCardResponse> doInBackground(WebService... params) {
            BusinessCardWebService bcService = (BusinessCardWebServiceImpl) params[0];
            WalletEntryWebServiceImpl walletService = (WalletEntryWebServiceImpl) params[1];

            List<BusinessCardResponse> cards = null;

            try {
                cards = bcService.findByUserNameV2(firstName, lastName);

                // extract all card id from user's wallet
                for (BusinessCardResponse walletCard : walletService.getWallet(userId, authToken)) {
                    currentWalletCardsIdSet.add(walletCard.getBusinessCard().getId());
                }

            } catch (WebServiceException ex) {
                message = ex.getMessage();
            }

            return cards;
        }

        @Override
        protected void onPostExecute(List<BusinessCardResponse> cards) {
            searchTask = null;
            showProgress(false);

            if (cards != null) {

                cardAdapter.clear();
                cardAdapter.setCurrentWalletCardsIdSet(currentWalletCardsIdSet);

                for (BusinessCardResponse c : cards) {
                    cardAdapter.add(c);
                }

                cardAdapter.notifyDataSetChanged();

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
