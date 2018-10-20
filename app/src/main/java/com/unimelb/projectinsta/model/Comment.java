package com.unimelb.projectinsta.model;

import java.util.Comparator;
import java.util.Date;

public class Comment implements Comparable<Comment>{
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

    @Override
    public int compareTo(Comment o) {
        if(getDate() == null || o.getDate() == null){
            return 0;
        }else{
            return o.getDate().compareTo(getDate());
        }

    }
}
