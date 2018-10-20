package com.unimelb.projectinsta.model;

import java.util.Date;

public class Like {

    public Like(){

    }

    public Like(UserPojo user, Date date){
        this.user = user;
        this.date = date;
    }

    private UserPojo user;
    private Date date;

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
}
