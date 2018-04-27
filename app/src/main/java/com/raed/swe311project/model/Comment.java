package com.raed.swe311project.model;

/**
 * Created by Raed on 18/04/2018.
 */

public class Comment {

    private String mID;
    private long mDate;
    private String mPropertyID;
    private String mUserID;
    private String mComment;

    public String getID() {
        return mID;
    }

    public void setID(String ID) {
        mID = ID;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
        mDate = date;
    }

    public String getPropertyID() {
        return mPropertyID;
    }

    public void setPropertyID(String propertyID) {
        mPropertyID = propertyID;
    }

    public String getUserID() {
        return mUserID;
    }

    public void setUserID(String userID) {
        mUserID = userID;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }
}
