package gr.bcw.business_card_wallet.model;

import java.util.Date;

import io.realm.annotations.PrimaryKey;

/**
 * Created by konstantinos on 26/3/2017.
 */

public class BusinessCard {

    @PrimaryKey
    private long id;
    private long professionId;
    private long templateId;
    private long userId;
    private String title;
    private String description;
    private String phoneNumber1;
    private String phoneNumber2;
    private String linkedIn;
    private String website;
    private String email;
    private Date createdAt;
    private Date lastUpdated;

    public BusinessCard() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProfessionId() {
        return professionId;
    }

    public void setProfessionId(long professionId) {
        this.professionId = professionId;
    }

    public long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(long templateId) {
        this.templateId = templateId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoneNumber1() {
        return phoneNumber1;
    }

    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }

    public String getPhoneNumber2() {
        return phoneNumber2;
    }

    public void setPhoneNumber2(String phoneNumber2) {
        this.phoneNumber2 = phoneNumber2;
    }

    public String getLinkedIn() {
        return linkedIn;
    }

    public void setLinkedIn(String linkedIn) {
        this.linkedIn = linkedIn;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "BusinessCard{" +
                "id=" + id +
                ", professionId=" + professionId +
                ", templateId=" + templateId +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", phoneNumber1='" + phoneNumber1 + '\'' +
                ", phoneNumber2='" + phoneNumber2 + '\'' +
                ", linkedIn='" + linkedIn + '\'' +
                ", website='" + website + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
