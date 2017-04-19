package gr.bcw.business_card_wallet.storage;

import gr.bcw.business_card_wallet.model.BusinessCard;
import io.realm.Realm;

/**
 * Created by konstantinos on 19/4/2017.
 */

public class BusinessCardStorageHandler {

    private static final String TAG = BusinessCardStorageHandler.class.getSimpleName();

    public void saveBusinessCard(Realm realm, BusinessCard card) {
        realm.beginTransaction();
        realm.copyToRealm(card);
        realm.commitTransaction();
    }

}
