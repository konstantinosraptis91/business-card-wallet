package gr.bcw.business_card_wallet.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import gr.bcw.business_card_wallet.R;
import gr.bcw.business_card_wallet.adapter.BusinessCardResponseAdapter;
import gr.bcw.business_card_wallet.model.WalletEntry;
import gr.bcw.business_card_wallet.model.retriever.BusinessCardResponse;
import gr.bcw.business_card_wallet.util.TokenUtils;
import gr.bcw.business_card_wallet.util.UserUtils;
import gr.bcw.business_card_wallet.webservice.WalletEntryWebService;
import gr.bcw.business_card_wallet.webservice.WalletEntryWebServiceImpl;
import gr.bcw.business_card_wallet.webservice.exception.WebServiceException;
import io.realm.Realm;

/**
 * Created by konstantinos on 26/3/2017.
 */

public class AddedBusinessCardsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = AddedBusinessCardsFragment.class.getSimpleName();
    public static final String ARG_OBJECT = "object";

    private ListView cardListView;
    private BusinessCardResponseAdapter cardAdapter;
    private GetWalletTask getWalletTask = null;
    private SaveWalletEntryTask saveWalletEntryTask = null;

    private Realm realm;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LinearLayout addCardByIdLayout;
    private ImageButton hideButton;
    private ImageButton saveCardButton;
    private EditText cardIdEditText;

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
        cardAdapter = new BusinessCardResponseAdapter(getActivity(), new ArrayList<BusinessCardResponse>(), BusinessCardResponseAdapter.CardType.ADDED_BUSINESS_CARD);
        cardListView.setAdapter(cardAdapter);

//        View progressView = rootView.findViewById(R.id.progress);
//        View getWalletView = rootView.findViewById(R.id.businessCardsListView);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        addCardByIdLayout = (LinearLayout) rootView.findViewById(R.id.add_card_by_id_layout);
        hideButton = (ImageButton) rootView.findViewById(R.id.hide_button);
        saveCardButton = (ImageButton) rootView.findViewById(R.id.save_card_by_id_button);
        cardIdEditText = (EditText) rootView.findViewById(R.id.card_id_editText);

        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHideAddCardByIdLayout();
                // hide soft keyboard
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(hideButton.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        saveCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSaveWalletEntry();
            }
        });

        attemptGetWallet();

        return rootView;
    }

    private boolean isCardIdValid(String cardId) {
        try {
            Long.parseLong(cardId);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public void attemptSaveWalletEntry() {
        if (saveWalletEntryTask != null) {
            return;
        }

        // Reset errors.
        cardIdEditText.setError(null);

        // Store values
        String cardId = cardIdEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // check for a valid card id (should be a decimal number)
        if (TextUtils.isEmpty(cardId)) {
            cardIdEditText.setError(getString(R.string.error_field_required));
            focusView = cardIdEditText;
            cancel = true;
        } else if (!isCardIdValid(cardId)) {
            cardIdEditText.setError(getString(R.string.error_invalid_card_id));
            focusView = cardIdEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {

            long id = UserUtils.getID(getActivity());
            String token = TokenUtils.getToken(getActivity());

            // entry conf here
            WalletEntry entry = new WalletEntry();
            entry.setUserId(id);
            entry.setBusinessCardId(Long.parseLong(cardId));

            showProgress(true);

            saveWalletEntryTask = new SaveWalletEntryTask(entry, token);
            saveWalletEntryTask.execute(new WalletEntryWebServiceImpl());
        }

    }

    public void attemptGetWallet() {
        if (getWalletTask != null) {
            return;
        }

        showProgress(true);

        long id = UserUtils.getID(getActivity());
        String token = TokenUtils.getToken(getActivity());

        getWalletTask = new GetWalletTask(id, token);
        getWalletTask.execute(new WalletEntryWebServiceImpl());
    }

    private void showProgress(boolean progress) {
        mSwipeRefreshLayout.setRefreshing(progress);
    }

    @Override
    public void onRefresh() {
        cardAdapter.clear();
        cardAdapter.notifyDataSetChanged();
        attemptGetWallet();
    }

    private class SaveWalletEntryTask extends AsyncTask<WalletEntryWebService, Void, BusinessCardResponse> {

        private String token;
        private final WalletEntry entry;
        private String message = "";

        public SaveWalletEntryTask(WalletEntry entry, String token) {
            this.token = token;
            this.entry = entry;
        }

        @Override
        protected BusinessCardResponse doInBackground(WalletEntryWebService... params) {
            WalletEntryWebService service = params[0];
            BusinessCardResponse card = null;

            try {
                card = service.saveWalletEntry(entry, token);
            } catch (WebServiceException ex) {
                message = ex.getMessage();
            }

            return card;
        }

        @Override
        protected void onPostExecute(BusinessCardResponse card) {
            saveWalletEntryTask = null;
            showProgress(false);

            if (card != null) {
                // wallet entry saved successfully
                Log.d(TAG, "wallet entry saved successfully");
                Log.d(TAG, card.toString());

                cardIdEditText.setText("");

                cardAdapter.add(card);
                // not sure if needed here
                cardAdapter.notifyDataSetChanged();
            } else {
                // wallet entry wasn't saved successfully
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onCancelled() {
            saveWalletEntryTask = null;
            showProgress(false);
        }

    }

    private class GetWalletTask extends AsyncTask<WalletEntryWebService, Void, List<BusinessCardResponse>> {

        private long id;
        private String token;
        private WebServiceException webServiceException;

        public GetWalletTask(long id, String token) {
            this.id = id;
            this.token = token;
        }

        @Override
        protected List<BusinessCardResponse> doInBackground(WalletEntryWebService... params) {
            WalletEntryWebService service = params[0];
            List<BusinessCardResponse> cardList = null;

            try {
                cardList = service.getWallet(id, token);
            } catch (WebServiceException ex) {
                this.webServiceException = ex;
            }

            return cardList;
        }

        @Override
        protected void onPostExecute(List<BusinessCardResponse> cardList) {
            getWalletTask = null;
            showProgress(false);

            if (cardList != null) {
                // user updated successfully
                Log.d(TAG, "get wallet performed successfully");
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
            getWalletTask = null;
            showProgress(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }


    public void showHideAddCardByIdLayout() {

        int visibility = addCardByIdLayout.getVisibility();

        switch (visibility) {
            case View.GONE:
                addCardByIdLayout.setVisibility(View.VISIBLE);
                break;
            case View.VISIBLE:
                addCardByIdLayout.setVisibility(View.GONE);
                cardIdEditText.setText("");
                break;
        }

    }

}
