package gr.bcw.business_card_wallet;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.List;

import gr.bcw.business_card_wallet.model.BusinessCard;

/**
 * Created by konstantinos on 26/3/2017.
 */

public class BusinessCardAdapter extends ArrayAdapter<BusinessCard> {


    public BusinessCardAdapter(@NonNull Context context, List<BusinessCard> addedBusinessCards) {
        super(context, R.layout.row_business_card, addedBusinessCards);
    }
}
