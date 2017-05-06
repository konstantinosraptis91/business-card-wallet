package gr.bcw.business_card_wallet.model;

import io.realm.RealmObject;

/**
 * Created by konstantinos on 5/5/2017.
 */

public class WalletEntry extends RealmObject {

    private long businessCardId;
    private long userId;

    public WalletEntry() {

    }

    public long getBusinessCardId() {
        return businessCardId;
    }

    public void setBusinessCardId(long businessCardId) {
        this.businessCardId = businessCardId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "WalletEntry{" +
                "businessCardId=" + businessCardId +
                ", userId=" + userId +
                '}';
    }
}
