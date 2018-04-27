package com.raed.swe311project.model;

import java.io.Serializable;

public class Property implements Serializable{

    private String mID;
    private int mPrice;
    private String mPropertyType;
    private String mDescription;
    private String mLocation;
    private String mImageURL;
    private String mSellerId;

    private Rating mRating;

    public String getID() {
        return mID;
    }

    public void setID(String ID) {
        mID = ID;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        mPrice = price;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getImageURL() {
        return mImageURL;
    }

    public void setImageURL(String imageURL) {
        mImageURL = imageURL;
    }

    public String getSellerId() {
        return mSellerId;
    }

    public void setSellerId(String sellerId) {
        mSellerId = sellerId;
    }


    public String getPropertyType() {
        return mPropertyType;
    }

    public void setPropertyType(String propertyType) {
        mPropertyType = propertyType;
    }

    public Rating getRating() {
        return mRating;
    }

    public void setRating(Rating rating) {
        mRating = rating;
    }
}
