package com.raed.swe311project.utilities;

import com.raed.swe311project.model.Rating;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class RatingMathTest {
    
    String mPropertyID = "1";

    String mUser1ID = "1";
    String mUser2ID = "2";
    String mUser3ID = "3";

    Rating mOldRating0;
    Rating mOldRating1;

    @Before
    public void setUp(){
        mOldRating0 = new Rating(mPropertyID){
            {
                setAvgRating(0);
                setNumOfRating(0);
            }
        };

        mOldRating1 = new Rating(mUser1ID){
            {
                setAvgRating(3);
                setNumOfRating(2);
                Map<String, Double> userRatingMap = new HashMap<>();
                userRatingMap.put(mUser1ID, 5d);
                userRatingMap.put(mUser2ID, 1d);
                setUserRatingMap(userRatingMap);
            }
        };
    }

    //test when there is no any rating
    @Test
    public void testCalcNewRating0(){
        Rating newRating = RatingMath.calcNewRating(mPropertyID, mOldRating0, mUser1ID, 3);
        assertEquals(newRating.getAvgRating(), 3d, 0);
        assertEquals(newRating.getNumOfRating(), 1);

        assertEquals(newRating.getUserRatingMap().size(), 1);

        Map<String, Double> expectedUserRatingMap = new HashMap<>();
        expectedUserRatingMap.put(mUser1ID, 3d);
        assertEquals(newRating.getUserRatingMap(), expectedUserRatingMap);
    }

    //test re-rating
    @Test
    public void testCalcNewRating1(){
        Rating newRating = RatingMath.calcNewRating(mPropertyID, mOldRating1, mUser2ID, 5);
        assertEquals(newRating.getAvgRating(), 5d, 0);
        assertEquals(newRating.getNumOfRating(), 2);

        assertEquals(newRating.getUserRatingMap().size(), 2);

        Map<String, Double> expectedUserRatingMap = new HashMap<>();
        expectedUserRatingMap.put(mUser1ID, 5d);
        expectedUserRatingMap.put(mUser2ID, 5d);
        assertEquals(newRating.getUserRatingMap(), expectedUserRatingMap);

    }

    //test adding a new rating
    @Test
    public void testCalcNewRating2(){
        Rating newRating = RatingMath.calcNewRating(mPropertyID, mOldRating1, mUser3ID, 3);
        assertEquals(newRating.getAvgRating(), 3d, 0);
        assertEquals(newRating.getNumOfRating(), 3);

        assertEquals(newRating.getUserRatingMap().size(), 3);

        Map<String, Double> expectedUserRatingMap = new HashMap<>();
        expectedUserRatingMap.put(mUser1ID, 5d);
        expectedUserRatingMap.put(mUser2ID, 1d);
        expectedUserRatingMap.put(mUser3ID, 3d);
        assertEquals(newRating.getUserRatingMap(), expectedUserRatingMap);
    }
}