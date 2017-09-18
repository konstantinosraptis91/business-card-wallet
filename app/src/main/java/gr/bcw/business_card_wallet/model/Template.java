package gr.bcw.business_card_wallet.model;

/**
 * Created by konstantinos on 18/9/2017.
 */

public class Template extends DBEntity {

    private String name;
    private String primaryColor;
    private String secondaryColor;

    public Template() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(String secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    @Override
    public String toString() {
        return "Template{"
                + "id=" + id
                + ", name=" + name
                + ", primaryColor=" + primaryColor
                + ", secondaryColor=" + secondaryColor
                + ", lastUpdated=" + lastUpdated
                + ", createdAt=" + createdAt
                + '}';
    }

}
