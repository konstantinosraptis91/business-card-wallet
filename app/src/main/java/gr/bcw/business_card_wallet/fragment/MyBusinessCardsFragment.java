package gr.bcw.business_card_wallet.fragment;

import android.os.AsyncTask;
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

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import gr.bcw.business_card_wallet.R;
import gr.bcw.business_card_wallet.adapter.BusinessCardResponseAdapter;
import gr.bcw.business_card_wallet.model.retriever.BusinessCardResponse;
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
    private BusinessCardResponseAdapter cardAdapter;
    private MyBusinessCardsFragment.GetBusinessCardsByUserIdTask getBusinessCardsByUserIdTask = null;

    private Realm realm;

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

        cardAdapter = new BusinessCardResponseAdapter(getActivity(), new ArrayList<BusinessCardResponse>(), BusinessCardResponseAdapter.CardType.MY_BUSINESS_CARD);
        cardListView.setAdapter(cardAdapter);

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

        showProgress(true);

        long id = UserUtils.getID(getActivity());
        String token = TokenUtils.getToken(getActivity());

        getBusinessCardsByUserIdTask = new GetBusinessCardsByUserIdTask(id, token);
        getBusinessCardsByUserIdTask.execute(new BusinessCardWebServiceImpl());
    }

    private void showProgress(boolean progress) {
        mSwipeRefreshLayout.setRefreshing(progress);
    }

    @Override
    public void onRefresh() {
        cardAdapter.clear();
        cardAdapter.notifyDataSetChanged();
        attemptGetBusinessCardsByUserId();
    }

    private class GetBusinessCardsByUserIdTask extends AsyncTask<BusinessCardWebService, Void, List<BusinessCardResponse>> {

        private long id;
        private String token;
        private WebServiceException webServiceException;

        public GetBusinessCardsByUserIdTask(long id, String token) {
            this.id = id;
            this.token = token;
        }

        @Override
        protected List<BusinessCardResponse> doInBackground(BusinessCardWebService... params) {
            BusinessCardWebService service = params[0];
            List<BusinessCardResponse> cardList = null;

            try {
                cardList = service.findByUserIdV2(id, token);
            } catch (WebServiceException ex) {
                webServiceException = ex;
            }

            return cardList;
        }

        @Override
        protected void onPostExecute(List<BusinessCardResponse> cardList) {
            getBusinessCardsByUserIdTask = null;
            showProgress(false);

            if (cardList != null) {
                // user updated successfully
                Log.d(TAG, "get user's personal business cards performed successfully");
                Log.d(TAG, cardList.toString());

                for (BusinessCardResponse card : cardList) {
                    cardAdapter.add(card);
                }

            } else {
                if (webServiceException.getHttpCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                    Log.d(TAG, webServiceException.getMessage());
                } else {
                    Toast.makeText(getActivity(), webServiceException.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            getBusinessCardsByUserIdTask = null;
            showProgress(false);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}