package com.unimelb.projectinsta.model;

import android.location.Location;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserFeed {
    private UserPojo user;
    private String photo;
    private Location location;
    private String caption;
    private Date date;
    private List<Like> likeList = new ArrayList<>();
    private List<Comment> commentList = new ArrayList<>();
    private int feed_Id;
    private int comments;

    public UserFeed(){

    }

    public UserFeed(UserPojo user,String location,int resid,String description){
        this.user = user;

    }

    public UserFeed(UserPojo user, String photo, Location location, String caption, Date date, Like like, Comment comment, int feed_Id) {
        this.user = user;
        this.photo = photo;
        this.location = location;
        this.caption = caption;
        this.date = date;
        this.likeList.add(like);
        this.commentList.add(comment);
        this.feed_Id = feed_Id;
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

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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

    public void addLikeList(Like like) {
        this.likeList.add(like);
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void addCommentList(Comment comment) {
        this.commentList.add(comment);
    }

    public int getFeed_Id() {
        return feed_Id;
    }

    public void setFeed_Id(int feed_Id) {
        this.feed_Id = feed_Id;
    }

    public int getComments() {
        return commentList.size();
    }


    @Override
    public String toString() {
        return "UserFeed{" +
                "user=" + user +
                ", photo='" + photo + '\'' +
                ", location='" + location + '\'' +
                ", caption='" + caption + '\'' +
                ", date=" + date +
                ", likeList=" + likeList +
                ", commentList=" + commentList +
                ", feed_Id=" + feed_Id +
                ", comments=" + comments +
                '}';
    }
}

