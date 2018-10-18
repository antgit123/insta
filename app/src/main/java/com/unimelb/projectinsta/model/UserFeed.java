package com.unimelb.projectinsta.model;

import android.location.Location;

import java.util.Date;
import java.util.List;

public class UserFeed {
    private UserPojo user;
    private String photo;
    private Location location;
    private String hashtag;
    private Date date;
    private List<Like> likeList;
    private List<Comment> commentList;
    private int feed_Id;
    private int comments;


    private String m_UserName;
    private String m_Location;
    private int m_Img;
    private String m_Description;

    public UserFeed(){

    }

    public UserFeed(String userName, String location, int image_resource, String description){
        this.m_UserName = userName;
        this.m_Location = location;
        this.m_Img = image_resource;
        this.m_Description = description;
    }

    public UserFeed(UserPojo user, String photo, Location location, String hashtag, Date date, List<Like> likeList, List<Comment> commentList, String m_Description) {
        this.user = user;
        this.photo = photo;
        this.location = location;
        this.hashtag = hashtag;
        this.date = date;
        this.likeList = likeList;
        this.commentList = commentList;
        this.m_Description = m_Description;
    }

    public String getM_UserName() {
        return m_UserName;
    }

    public void setM_UserName(String m_UserName) {
        this.m_UserName = m_UserName;
    }

    public int getM_Img() {
        return m_Img;
    }

    public void setM_Img(int m_Img) {
        this.m_Img = m_Img;
    }

    public String getM_Description() {
        return m_Description;
    }

    public void setM_Description(String m_Description) {
        this.m_Description = m_Description;
    }

    public String getM_Location() {
        return m_Location;
    }

    public void setM_Location(String m_Location) {
        this.m_Location = m_Location;
    }

    public UserPojo getUser() {
        return user;
    }

    public void setUser(UserPojo user) {
        this.user = user;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Like> getLikeList() {
        return likeList;
    }

    public void setLikeList(List<Like> likeList) {
        this.likeList = likeList;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }
}

