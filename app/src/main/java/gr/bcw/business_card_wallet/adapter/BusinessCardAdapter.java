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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

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

    public enum CardType {
        MY_BUSINESS_CARD, ADDED_BUSINESS_CARD
    }

    private final CardType type;

    public BusinessCardAdapter(@NonNull Context context, List<BusinessCard> cards, CardType type) {
        super(context, R.layout.row_business_card, cards);
        this.type = type;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View rowView = inflater.inflate(R.layout.row_business_card, parent, false);
        CardView cardView = (CardView) rowView.findViewById(R.id.card_view);
        // same button for my and added business cards
        ImageButton deleteBCButton = (ImageButton) rowView.findViewById(R.id.deleteBCButton);

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

                        Log.i(TAG, entry.toString());

                        DeleteWalletEntryTask deleteEntryTask = new DeleteWalletEntryTask(entry, token, position);
                        deleteEntryTask.execute(new WalletEntryWebServiceImpl());
                    }
                });
                break;
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
                // user didn't updated successfully
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        }

    }

}
