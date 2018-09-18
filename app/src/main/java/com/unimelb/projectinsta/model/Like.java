package com.unimelb.projectinsta.model;

import java.util.Date;

public class Like {
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
