package gr.bcw.business_card_wallet.model;

import java.util.Date;

/**
 * Created by konstantinos on 20/3/2017.
 */

public class AuthResponse {

    private long id;
    private String token;
    private Date lastUpdated;
    private Date createdAt;

    public AuthResponse() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
