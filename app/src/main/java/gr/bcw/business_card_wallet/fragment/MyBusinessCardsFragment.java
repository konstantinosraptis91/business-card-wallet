package gr.bcw.business_card_wallet.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import gr.bcw.business_card_wallet.webservice.BusinessCardWebService;
import gr.bcw.business_card_wallet.webservice.BusinessCardWebServiceImpl;
import gr.bcw.business_card_wallet.webservice.exception.WebServiceException;
import io.realm.Realm;

/**
 * Created by konstantinos on 5/5/2017.
 */

public class MyBusinessCardsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = MyBusinessCardsFragment.class.getSimpleName();

    private ListView cardListView;
    private BusinessCardAdapter cardAdapter;
    private MyBusinessCardsFragment.GetBusinessCardsByUserIdTask getBusinessCardsByUserIdTask = null;

    private Realm realm;

    private View progressView;
    private View getBusinessCardsByIdView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_business_cards, container, false);
        cardListView = (ListView) rootView.findViewById(R.id.businessCardsListView);

        cardAdapter = new BusinessCardAdapter(getActivity(), new ArrayList<BusinessCard>(), BusinessCardAdapter.CardType.MY_BUSINESS_CARD);
        cardListView.setAdapter(cardAdapter);

        progressView = rootView.findViewById(R.id.progress);
        getBusinessCardsByIdView = rootView.findViewById(R.id.businessCardsListView);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        attemptGetBusinessCardsByUserId();

        // Inflate layout for this fragment
        return rootView;
    }

    public void attemptGetBusinessCardsByUserId() {
        if (getBusinessCardsByUserIdTask != null) {
            return;
        }

        long id = UserUtils.getID(getActivity());
        String token = TokenUtils.getToken(getActivity());

        getBusinessCardsByUserIdTask = new GetBusinessCardsByUserIdTask(id, token);
        getBusinessCardsByUserIdTask.execute(new BusinessCardWebServiceImpl());
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

            getBusinessCardsByIdView.setVisibility(show ? View.GONE : View.VISIBLE);
            getBusinessCardsByIdView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    getBusinessCardsByIdView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            getBusinessCardsByIdView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        cardAdapter.clear();
        cardAdapter.notifyDataSetChanged();
        attemptGetBusinessCardsByUserId();
    }

    private class GetBusinessCardsByUserIdTask extends AsyncTask<BusinessCardWebService, Void, List<BusinessCard>> {

        private long id;
        private String token;
        private String message = "";

        public GetBusinessCardsByUserIdTask(long id, String token) {
            this.id = id;
            this.token = token;
        }

        @Override
        protected List<BusinessCard> doInBackground(BusinessCardWebService... params) {
            BusinessCardWebService service = params[0];
            List<BusinessCard> cardList = null;

            try {
                cardList = service.findByUserId(id, token);
            } catch (WebServiceException ex) {
                message = ex.getMessage();
            }

            return cardList;
        }

        @Override
        protected void onPostExecute(List<BusinessCard> cardList) {
            getBusinessCardsByUserIdTask = null;
            showProgress(false);
            mSwipeRefreshLayout.setRefreshing(false);

            if (cardList != null) {
                // user updated successfully
                Log.d(TAG, "get user's personal business cards performed successfully");
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
            getBusinessCardsByUserIdTask = null;
            showProgress(false);
            mSwipeRefreshLayout.setRefreshing(false);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}