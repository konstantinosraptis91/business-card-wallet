package gr.bcw.business_card_wallet.model;

/**
 * Created by konstantinos on 18/9/2017.
 */

public class Profession extends DBEntity {

    private String name;

    public Profession() {
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
        return "Profession{"
                + "id=" + id
                + ", name=" + name
                + ", lastUpdated=" + lastUpdated
                + ", createdAt=" + createdAt
                + '}';
    }

}
