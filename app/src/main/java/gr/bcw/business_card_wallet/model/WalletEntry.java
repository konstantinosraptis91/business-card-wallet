package gr.bcw.business_card_wallet.model;

/**
 * Created by konstantinos on 5/5/2017.
 */

public class WalletEntry {

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
