package gr.bcw.business_card_wallet.model;

/**
 * Created by konstantinos on 18/9/2017.
 */

public class Company extends DBEntity {

    private String name;

    public Company() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Company{"
                + "id=" + id
                + ", name=" + name
                + ", lastUpdated=" + lastUpdated
                + ", createdAt=" + createdAt
                + '}';
    }

}
