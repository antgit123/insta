package com.unimelb.projectinsta.model;

import java.util.Date;

public class MyNotificationsPojo {
    private String type;
    private String feedDescription;
    private UserPojo user;
    private Date notificationTimestamp;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFeedDescription() {
        return feedDescription;
    }

    public void setFeedDescription(String feedDescription) {
        this.feedDescription = feedDescription;
    }

    public UserPojo getUser() {
        return user;
    }

    public void setUser(UserPojo user) {
        this.user = user;
    }

    public Date getNotificationTimestamp() {
        return notificationTimestamp;
    }

    public void setNotificationTimestamp(Date notificationTimestamp) {
        this.notificationTimestamp = notificationTimestamp;
    }

    public  MyNotificationsPojo(String type, String feedDescription,UserPojo user, Date notificationTimestamp){
        this.type = type;
        this.feedDescription = feedDescription;
        this.user = user;
        this.notificationTimestamp = notificationTimestamp;

    }
}
