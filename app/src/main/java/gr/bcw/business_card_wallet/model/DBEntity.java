package gr.bcw.business_card_wallet.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by konstantinos on 18/9/2017.
 */

public abstract class DBEntity implements Serializable {

    protected long id;
    protected Date lastUpdated;
    protected Date createdAt;

    public DBEntity() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
