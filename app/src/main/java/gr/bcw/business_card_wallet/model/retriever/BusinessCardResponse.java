package gr.bcw.business_card_wallet.model.retriever;

import gr.bcw.business_card_wallet.model.BusinessCard;
import gr.bcw.business_card_wallet.model.Company;
import gr.bcw.business_card_wallet.model.Profession;
import gr.bcw.business_card_wallet.model.Template;

/**
 * Created by konstantinos on 18/9/2017.
 */

public class BusinessCardResponse {

    private String userFullName;
    private Profession profession;
    private Company company;
    private Template template;
    private BusinessCard businessCard;

    public BusinessCardResponse() {

    }

    public void setUserFullName(String firstName, String lastName) {
        this.userFullName = firstName + " " + lastName;
    }

    public String getUserFullName() {
        return userFullName;
    }


    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public BusinessCard getBusinessCard() {
        return businessCard;
    }

    public void setBusinessCard(BusinessCard businessCard) {
        this.businessCard = businessCard;
    }

    @Override
    public String toString() {
        return "BusinessCardResponse{" +
                "userFullName='" + userFullName + '\'' +
                ", profession=" + profession +
                ", company=" + company +
                ", template=" + template +
                ", businessCard=" + businessCard +
                '}';
    }
}
