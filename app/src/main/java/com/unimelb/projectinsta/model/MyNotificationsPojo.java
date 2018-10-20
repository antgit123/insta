package com.unimelb.projectinsta.model;

import java.util.Date;
import java.util.Objects;

public class MyNotificationsPojo implements Comparable<MyNotificationsPojo> {
    private String type;
    private String feedDescription;
    private UserPojo user;
    private Date notificationTimestamp;
    private String userId;


    public MyNotificationsPojo() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public  MyNotificationsPojo(String userId, String type, String feedDescription,UserPojo user, Date notificationTimestamp){
        this.type = type;
        this.feedDescription = feedDescription;
        this.user = user;
        this.notificationTimestamp = notificationTimestamp;
        this.userId = userId;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyNotificationsPojo that = (MyNotificationsPojo) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(feedDescription, that.feedDescription) &&
                Objects.equals(user, that.user) &&
                Objects.equals(notificationTimestamp, that.notificationTimestamp) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(type, feedDescription, user, notificationTimestamp, userId);
    }

    @Override
    public int compareTo(MyNotificationsPojo o) {
        if(getNotificationTimestamp() == null || o.getNotificationTimestamp() == null){
            return 0;
        }else{
           return o.getNotificationTimestamp().compareTo(getNotificationTimestamp());
        }

    }
}
