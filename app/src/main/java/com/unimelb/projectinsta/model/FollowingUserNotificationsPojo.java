package com.unimelb.projectinsta.model;

import java.util.Date;

public class FollowingUserNotificationsPojo {
    private String type;
    private String feedDescription;
    private UserPojo user1;
    private UserPojo user2;
    private Date notificationTimestamp;
    private String userId;

    public FollowingUserNotificationsPojo() {

    }

    public FollowingUserNotificationsPojo(UserPojo user1, UserPojo user2, String type, String feedDescription, Date notificationTimestamp, String userId){
        this.type = type;
        this.user1 = user1;
        this.user2 = user2;
        this.feedDescription = feedDescription;
        this.notificationTimestamp = notificationTimestamp;
        this.userId = userId;
    }

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

    public UserPojo getUser1() {
        return user1;
    }

    public void setUser1(UserPojo user1) {
        this.user1 = user1;
    }

    public UserPojo getUser2() {
        return user2;
    }

    public void setUser2(UserPojo user2) {
        this.user2 = user2;
    }

    public Date getNotificationTimestamp() {
        return notificationTimestamp;
    }

    public void setNotificationTimestamp(Date notificationTimestamp) {
        this.notificationTimestamp = notificationTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
