package gr.bcw.business_card_wallet.model.sender;

import gr.bcw.business_card_wallet.model.BusinessCard;

/**
 * Created by konstantinos on 19/9/2017.
 */

public class BusinessCardRequest {

    private String professionName;
    private String companyName;
    private BusinessCard businessCard;

    public BusinessCardRequest() {

    }

    public void setProfessionName(String professionName) {
        this.professionName = professionName;
    }

    public String getProfessionName() {
        return professionName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setBusinessCard(BusinessCard businessCard) {
        this.businessCard = businessCard;
    }

    public BusinessCard getBusinessCard() {
        return businessCard;
    }

    @Override
    public String toString() {
        return "BusinessCardRequest{" +
                "professionName='" + professionName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", businessCard=" + businessCard +
                '}';
    }
}
