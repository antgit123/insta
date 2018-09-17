package com.unimelb.projectinsta.model;

public class UserFeed {

    //will be replaced with User model class
    private String m_UserName;
    private String m_Location;
    private int m_Img;
    private String m_Description;

    public UserFeed(String userName, String location, int image_resource, String description){
        this.m_UserName = userName;
        this.m_Location = location;
        this.m_Img = image_resource;
        this.m_Description = description;
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
}

