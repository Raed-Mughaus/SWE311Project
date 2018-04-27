package com.raed.swe311project.utilities;

import com.raed.swe311project.model.Rating;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Raed on 27/04/2018.
 */

public class RatingMath {


    /**
     * Calculate the new rating. if this user id is already exists in the property's ratings, the
     * old rating will be replaced with the new one.
     * @param propertyID the id of the property associated with the rating
     * @param oldRating the old property rating, can be null.
     * @param userID the id of the user
     * @param ratingVal the new rating value entered by a user
     * @return the new updated rating.
     */
    public static Rating calcNewRating(String propertyID, Rating oldRating, String userID, float ratingVal){
        Rating newRating;
        if (oldRating == null){
            newRating = new Rating(propertyID);
            newRating.setNumOfRating(1);
            newRating.setAvgRating(ratingVal);
            Map<String, Double> userRatingMap = new HashMap<>();
            userRatingMap.put(userID, (double) ratingVal);
            newRating.setUserRatingMap(userRatingMap);
            return newRating;
        }

        float totalRating = oldRating.getAvgRating() * oldRating.getNumOfRating();
        float newAvgRating;
        int newNumOfRating;
        if (oldRating.isRatedBy(userID)){
            float oldUserRating = oldRating.getUserRating(userID);
            newNumOfRating = oldRating.getNumOfRating();
            newAvgRating = (totalRating - oldUserRating + ratingVal) / newNumOfRating;
        }else {
            newNumOfRating = oldRating.getNumOfRating() + 1;
            newAvgRating = (totalRating + ratingVal) / newNumOfRating;
        }

        Map<String, Double> newUserRatingMap = new HashMap<>(oldRating.getUserRatingMap());
        newUserRatingMap.put(userID, (double) ratingVal);//update this user update

        newRating = new Rating(propertyID);
        newRating.setNumOfRating(newNumOfRating);
        newRating.setAvgRating(newAvgRating);
        newRating.setUserRatingMap(newUserRatingMap);

        return newRating;
    }

}
