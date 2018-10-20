package com.unimelb.projectinsta.model;

import java.util.Date;

public class Comment {
    private UserPojo user;
    private Date date;
    private String description;

    public Comment(){

    }

    public Comment(UserPojo user, Date date,String description){
        this.user = user;
        this.date = date;
        this.description = description;
    }

    public UserPojo getUser() {
        return user;
    }

    public void setUser(UserPojo user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
