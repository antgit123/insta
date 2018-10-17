package com.unimelb.projectinsta.model;

import android.util.AndroidRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserPojo {
    private String userId;
    private String userName;
    private String userRealName;
    private String profilePhoto;
    private String email;
    private String password;
    private List<UserPojo> followerList;
    private List<UserPojo> followingList;

    public UserPojo(){

    }

    public UserPojo(String userId, String userName, String userRealName, String email, String password) {
        this.userId = userId;
        this.userName = userName;
        this.userRealName = userRealName;
        this.email = email;
        this.password = password;
        this.followerList = new ArrayList<UserPojo>();
        this.followingList = new ArrayList<UserPojo>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<UserPojo> getFollowerList() {
        return followerList;
    }

    public void setFollowerList(List<UserPojo> followerList) {
        this.followerList = followerList;
    }

    public List<UserPojo> getFollowingList() {
        return followingList;
    }

    public void setFollowingList(List<UserPojo> followingList) {
        this.followingList = followingList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPojo userPojo = (UserPojo) o;
        return userId == userPojo.userId &&
                Objects.equals(userName, userPojo.userName) &&
                Objects.equals(userRealName, userPojo.userRealName) &&
                Objects.equals(profilePhoto, userPojo.profilePhoto) &&
                Objects.equals(email, userPojo.email) &&
                Objects.equals(password, userPojo.password) &&
                Objects.equals(followerList, userPojo.followerList) &&
                Objects.equals(followingList, userPojo.followingList);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userId, userName, userRealName, profilePhoto, email, password, followerList, followingList);
    }
}
