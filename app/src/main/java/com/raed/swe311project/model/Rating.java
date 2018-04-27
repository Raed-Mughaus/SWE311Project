package com.raed.swe311project.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Raed on 18/04/2018.
 */

public class Rating implements Serializable{

    private final String mPropertyID;
    private int mNumOfRating;
    private float mRating;
    private Map<String, Double> mUserRatingMap = new HashMap<>();


    public Rating(String propertyID){
        mPropertyID = propertyID;
    }

    /**
     * Check to see if a user has rated the property associated with this Rating.
     * @param userID the id of the user.
     * @return true if user has already rated the property associated with this rating, false otherwise.
     */
    public boolean isRatedBy(String userID){
        return mUserRatingMap.containsKey(userID);
    }

    public int getNumOfRating() {
        return mNumOfRating;
    }

    public void setNumOfRating(int numOfRating) {
        mNumOfRating = numOfRating;
    }

    public float getAvgRating() {
        return mRating;
    }

    public void setAvgRating(float rating) {
        mRating = rating;
    }

    public String getPropertyID() {
        return mPropertyID;
    }

    public void setUserRatingMap(Map<String, Double> userRatingMap) {
        mUserRatingMap = userRatingMap;
    }

    public float getUserRating(String userID) {
        return mUserRatingMap.get(userID).floatValue();
    }

    public Map<String, Double> getUserRatingMap() {
        return mUserRatingMap;
    }
}
