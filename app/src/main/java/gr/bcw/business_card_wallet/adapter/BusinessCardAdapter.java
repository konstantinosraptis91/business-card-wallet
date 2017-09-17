package gr.bcw.business_card_wallet.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Set;

import gr.bcw.business_card_wallet.R;
import gr.bcw.business_card_wallet.model.BusinessCard;
import gr.bcw.business_card_wallet.model.WalletEntry;
import gr.bcw.business_card_wallet.util.TokenUtils;
import gr.bcw.business_card_wallet.util.UserUtils;
import gr.bcw.business_card_wallet.webservice.BusinessCardWebService;
import gr.bcw.business_card_wallet.webservice.BusinessCardWebServiceImpl;
import gr.bcw.business_card_wallet.webservice.WalletEntryWebService;
import gr.bcw.business_card_wallet.webservice.WalletEntryWebServiceImpl;
import gr.bcw.business_card_wallet.webservice.exception.WebServiceException;

/**
 * Created by konstantinos on 26/3/2017.
 */

public class BusinessCardAdapter extends ArrayAdapter<BusinessCard> {

    public static final String TAG = BusinessCardAdapter.class.getSimpleName();
    private Context context;
    private Set<Long> currentWalletCardsIdSet;
    private final CardType type;

    public enum CardType {
        MY_BUSINESS_CARD, ADDED_BUSINESS_CARD, SEARCH_BUSINESS_CARD
    }

    public BusinessCardAdapter(@NonNull Context context, List<BusinessCard> cards, CardType type) {
        super(context, R.layout.row_business_card, cards);
        this.type = type;
        this.context = context;
    }

    public void setCurrentWalletCardsIdSet(Set<Long> currentWalletCardsIdSet) {
        this.currentWalletCardsIdSet = currentWalletCardsIdSet;
    }

    /**
     * @param position
     * @param convertView
     * @param parent
     * @return Return a Card View
     */
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View rowView = inflater.inflate(R.layout.row_business_card, parent, false);
        CardView cardView = (CardView) rowView.findViewById(R.id.card_view);
        // same button for my and added business cards
        ImageButton deleteBCButton = (ImageButton) rowView.findViewById(R.id.deleteBCButton);
        ImageButton addToWalletButton = (ImageButton) rowView.findViewById(R.id.saveCardToWalletButton);

        final BusinessCard card = getItem(position);
        final TextView businessCardId = (TextView) rowView.findViewById(R.id.info_card_id_text_view);
        final TextView fullName = (TextView) rowView.findViewById(R.id.info_name_text_view);
        final TextView email1 = (TextView) rowView.findViewById(R.id.info_email_text_view);
        final TextView website = (TextView) rowView.findViewById(R.id.info_website_text_view);
        final TextView profession = (TextView) rowView.findViewById(R.id.info_profession_text_view);

        businessCardId.setText(getContext().getString(R.string.text_view_bc_id) + " " + card.getId());
        fullName.setText("User ID: " + card.getUserId());
        email1.setText(card.getEmail1());
        website.setText(card.getWebsite());
        profession.setText("Profession ID: " + card.getProfessionId());

        // draw card's background
        switch (type) {
            case MY_BUSINESS_CARD:
                cardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorMyBusinessCard));
                deleteBCButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String token = TokenUtils.getToken(context);

                        DeleteBusinessCardByIdTask deleteBCTask = new DeleteBusinessCardByIdTask(card.getId(), token, position);
                        deleteBCTask.execute(new BusinessCardWebServiceImpl());
                    }
                });
                break;
            case ADDED_BUSINESS_CARD:
                cardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAddedBusinessCard));
                deleteBCButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String token = TokenUtils.getToken(context);
                        long appUserId = UserUtils.getID(context);

                        WalletEntry entry = new WalletEntry();
                        entry.setUserId(appUserId);
                        entry.setBusinessCardId(card.getId());

                        DeleteWalletEntryTask deleteEntryTask = new DeleteWalletEntryTask(entry, token, position);
                        deleteEntryTask.execute(new WalletEntryWebServiceImpl());
                    }
                });
                break;
            case SEARCH_BUSINESS_CARD:
                // 3 cases for color here
                // 1 my card, 2 added card, 3 non added card

                long appUserId = UserUtils.getID(context);

                // display save button (only if card NOT in user's wallet and card is NOT his card)
                if (!currentWalletCardsIdSet.contains(card.getId()) && card.getUserId() != appUserId) {
                    addToWalletButton.setVisibility(View.VISIBLE);

                    // save card to wallet
                    addToWalletButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String token = TokenUtils.getToken(context);
                            long appUserId = UserUtils.getID(context);

                            WalletEntry entry = new WalletEntry();
                            entry.setUserId(appUserId);
                            entry.setBusinessCardId(card.getId());

                            SaveWalletEntryTask saveWalletEntryTask = new SaveWalletEntryTask(entry, token, position);
                            saveWalletEntryTask.execute(new WalletEntryWebServiceImpl());
                        }
                    });

                }

                // color case 1
                if (card.getUserId() == appUserId) {
                    cardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorMyBusinessCard));
                }
                // color case 2
                else if (currentWalletCardsIdSet.contains(card.getId())) {
                    cardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAddedBusinessCard));
                }
                // color case 2
                else {
                    cardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorNonSavedAndNonMineBC));
                }

                // hide delete image button
                deleteBCButton.setVisibility(View.GONE);
                break;
        }

        // add some extra margin at the bottom of the last card
        if (position == BusinessCardAdapter.this.getCount() - 1) {
            // cardWindow
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cardView.getLayoutParams();

            int dpValue = 10; // margin in dips
            float d = context.getResources().getDisplayMetrics().density;
            int margin = (int) (dpValue * d); // margin in pixels

            params.setMargins(margin, margin, margin, margin);
            cardView.setLayoutParams(params);
            cardView.requestLayout();
        }

        return rowView;
    }

    private class DeleteWalletEntryTask extends AsyncTask<WalletEntryWebService, Void, Boolean> {

        private WalletEntry entry;
        private String token;
        private String message = "";
        private int position;

        public DeleteWalletEntryTask(WalletEntry entry, String token, int position) {
            this.entry = entry;
            this.token = token;
            this.position = position;
        }

        @Override
        protected Boolean doInBackground(WalletEntryWebService... params) {
            WalletEntryWebService service = params[0];
            boolean result = false;

            try {
                service.deleteWalletEntry(entry, token);
                result = true;
            } catch (WebServiceException ex) {
                message = ex.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {
                // business card deleted successfully
                BusinessCardAdapter.this.remove(BusinessCardAdapter.this.getItem(position));
            } else {
                // user didn't updated successfully
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        }

    }

    private class DeleteBusinessCardByIdTask extends AsyncTask<BusinessCardWebService, Void, Boolean> {

        private long id;
        private String token;
        private String message = "";
        private int position;

        public DeleteBusinessCardByIdTask(long id, String token, int position) {
            this.id = id;
            this.token = token;
            this.position = position;
        }

        @Override
        protected Boolean doInBackground(BusinessCardWebService... params) {
            BusinessCardWebService service = params[0];
            boolean result = false;

            try {
                service.deleteBusinessCardById(id, token);
                result = true;
            } catch (WebServiceException ex) {
                message = ex.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {
                // business card deleted successfully
                BusinessCardAdapter.this.remove(BusinessCardAdapter.this.getItem(position));
            } else {
                // business card  didn't deleted successfully
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        }

    }

    private class SaveWalletEntryTask extends AsyncTask<WalletEntryWebService, Void, BusinessCard> {

        private String token;
        private final WalletEntry entry;
        private String message = "";
        private int position;

        public SaveWalletEntryTask(WalletEntry entry, String token, int position) {
            this.token = token;
            this.entry = entry;
            this.position = position;
        }

        @Override
        protected BusinessCard doInBackground(WalletEntryWebService... params) {
            WalletEntryWebService service = params[0];
            BusinessCard card = null;

            try {
                card = service.saveWalletEntry(entry, token);
            } catch (WebServiceException ex) {
                message = ex.getMessage();
            }

            return card;
        }

        @Override
        protected void onPostExecute(BusinessCard card) {

            if (card != null) {
                BusinessCardAdapter.this.remove(BusinessCardAdapter.this.getItem(position));
                Log.i(TAG, "added");
            } else {
                // wallet entry wasn't saved successfully
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }

        }

    }

}
