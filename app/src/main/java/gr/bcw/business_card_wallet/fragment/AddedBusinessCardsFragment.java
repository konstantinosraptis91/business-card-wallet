package gr.bcw.business_card_wallet.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gr.bcw.business_card_wallet.R;
import gr.bcw.business_card_wallet.adapter.BusinessCardAdapter;
import gr.bcw.business_card_wallet.model.BusinessCard;
import gr.bcw.business_card_wallet.util.TokenUtils;
import gr.bcw.business_card_wallet.util.UserUtils;
import gr.bcw.business_card_wallet.webservice.WalletEntryWebService;
import gr.bcw.business_card_wallet.webservice.WalletEntryWebServiceImpl;
import gr.bcw.business_card_wallet.webservice.exception.WebServiceException;
import io.realm.Realm;

/**
 * Created by konstantinos on 26/3/2017.
 */

public class AddedBusinessCardsFragment extends Fragment {

    public static final String TAG = AddedBusinessCardsFragment.class.getSimpleName();

    private ListView cardListView;
    private BusinessCardAdapter cardAdapter;
    private GetWalletTask getWalletTask = null;

    private Realm realm;

    private View progressView;
    private View getWalletView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_business_cards, container, false);
        cardListView = (ListView) view.findViewById(R.id.businessCardsListView);
        cardAdapter = new BusinessCardAdapter(getActivity(), new ArrayList<BusinessCard>());
        cardListView.setAdapter(cardAdapter);

        progressView = view.findViewById(R.id.progress);
        getWalletView = view.findViewById(R.id.businessCardsListView);

        // attemptGetWallet();

        return view;
    }

    public void attemptGetWallet() {
        if (getWalletTask != null) {
            return;
        }

        long id = UserUtils.getID(getActivity());
        String token = TokenUtils.getToken(getActivity());

        getWalletTask = new GetWalletTask(id, token);
        getWalletTask.execute(new WalletEntryWebServiceImpl());
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

            getWalletView.setVisibility(show ? View.GONE : View.VISIBLE);
            getWalletView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    getWalletView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            getWalletView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class GetWalletTask extends AsyncTask<WalletEntryWebService, Void, List<BusinessCard>> {

        private long id;
        private String token;
        private String message = "";

        public GetWalletTask(long id, String token) {
            this.id = id;
            this.token = token;
        }

        @Override
        protected List<BusinessCard> doInBackground(WalletEntryWebService... params) {
            WalletEntryWebService service = params[0];
            List<BusinessCard> cardList = null;

            try {
                cardList = service.getWallet(id, token);
            } catch (WebServiceException ex) {
                message = ex.getMessage();
            }

            return cardList;
        }

        @Override
        protected void onPostExecute(List<BusinessCard> cardList) {
            getWalletTask = null;
            showProgress(false);

            if (cardList != null) {
                // user updated successfully
                Log.d(TAG, "get wallet performed successfully");
                Log.d(TAG, cardList.toString());

                for (BusinessCard card : cardList) {
                    cardAdapter.add(card);
                }

            } else {
                // user didn't updated successfully
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            getWalletTask = null;
            showProgress(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}
