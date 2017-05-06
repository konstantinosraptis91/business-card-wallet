package gr.bcw.business_card_wallet.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import gr.bcw.business_card_wallet.R;
import gr.bcw.business_card_wallet.model.BusinessCard;

/**
 * Created by konstantinos on 26/3/2017.
 */

public class BusinessCardAdapter extends ArrayAdapter<BusinessCard> {


    public BusinessCardAdapter(@NonNull Context context, List<BusinessCard> cards) {
        super(context, R.layout.row_business_card, cards);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater =  LayoutInflater.from(getContext());
        View rowView = inflater.inflate(R.layout.row_business_card, parent, false);

        BusinessCard card = getItem(position);
        final TextView fullName = (TextView) rowView.findViewById(R.id.info_name_text_view);
        final TextView email1 = (TextView) rowView.findViewById(R.id.info_email_text_view);
        final TextView website = (TextView) rowView.findViewById(R.id.info_website_text_view);
        final TextView profession = (TextView) rowView.findViewById(R.id.info_profession_text_view);

        fullName.setText("User ID: " + card.getUserId());
        email1.setText(card.getEmail1());
        website.setText(card.getWebsite());
        profession.setText("Profession ID: " + card.getProfessionId());

        return rowView;
    }

}
