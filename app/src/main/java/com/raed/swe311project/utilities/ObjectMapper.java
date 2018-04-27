package com.raed.swe311project.utilities;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.raed.swe311project.model.Comment;
import com.raed.swe311project.model.VariableNames;
import com.raed.swe311project.model.VariableNames.PropertiesCollection;
import com.raed.swe311project.model.VariableNames.RatingCollection;
import com.raed.swe311project.model.Property;
import com.raed.swe311project.model.Rating;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains methods to map database documents to java objects.
 * @see VariableNames
 */

public class ObjectMapper {

    /**
     * Map a DocumentSnapshot to a property object.
     * @param document to be mapped to a property object
     * @return a property object
     **/
    public static Property documentToProperty(DocumentSnapshot document) {
        Property property = new Property();
        property.setID(document.getId());
        property.setImageURL(document.getString(PropertiesCollection.IMAGE_URL));
        property.setDescription(document.getString(PropertiesCollection.DESCRIPTION));
        property.setLocation(document.getString(PropertiesCollection.LOCATION));
        property.setSellerId(document.getString(PropertiesCollection.SELLER_ID));
        property.setPrice(document.getDouble(PropertiesCollection.PRICE).intValue());
        property.setPropertyType(document.getString(PropertiesCollection.TYPE));
        return property;
    }

    /**
     * Map a DocumentSnapshot to a Rating object.
     * @param document to be mapped to a Rating object
     * @return a rating object
     **/
    public static Rating documentToRating(DocumentSnapshot document) {
        Rating rating = new Rating(document.getId());
        rating.setNumOfRating(document.getDouble(RatingCollection.NUM_OF_RATING).intValue());
        rating.setAvgRating(document.getDouble(RatingCollection.RATING).floatValue());
        Map<String, Double> userRatingMap = (Map<String, Double>) document.get(RatingCollection.USER_RATING_MAP);
        rating.setUserRatingMap(userRatingMap);
        return rating;
    }

    /**
     * Map a DocumentSnapshot to a comment object.
     * @param document to be mapped to a Comment object
     * @return a comment object
     **/
    public static Comment documentToComment(QueryDocumentSnapshot document) {
        Comment comment = new Comment();
        comment.setID(document.getId());
        comment.setDate(document.getLong(VariableNames.CommentsCollection.DATE));
        comment.setPropertyID(document.getString(VariableNames.CommentsCollection.PROPERTY_ID));
        comment.setUserID(document.getString(VariableNames.CommentsCollection.USER_ID));
        comment.setComment(document.getString(VariableNames.CommentsCollection.COMMENT));
        return comment;
    }


    public static Map<String, Object> commentToMap(Comment comment){
        Map<String, Object> commentMap = new HashMap<>();
        commentMap.put(VariableNames.CommentsCollection.COMMENT, comment.getComment());
        commentMap.put(VariableNames.CommentsCollection.DATE, comment.getDate());
        commentMap.put(VariableNames.CommentsCollection.PROPERTY_ID, comment.getPropertyID());
        commentMap.put(VariableNames.CommentsCollection.USER_ID, comment.getUserID());
        return commentMap;
    }

    /**
     * Map a {@link Rating} to a variable-value {@link Map}. Variable names
     * can be found in {@link VariableNames}.
     * @param rating to be mapped
     * @return a variable-value {@link Map}.
     */
    public static Map<String, Object> ratingToMap(Rating rating){
        Map<String, Object> ratingMap = new HashMap<>();
        ratingMap.put(RatingCollection.RATING, rating.getAvgRating());
        ratingMap.put(RatingCollection.NUM_OF_RATING, rating.getNumOfRating());
        ratingMap.put(RatingCollection.USER_RATING_MAP, rating.getUserRatingMap());
        return ratingMap;
    }
}
