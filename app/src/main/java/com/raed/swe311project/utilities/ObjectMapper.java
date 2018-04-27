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
 * This class contains methods to map:
 *      a database document to a java object.
 *      a java object to a map
 * @see VariableNames
 */

public class ObjectMapper {

    /**
     * Map a {@link DocumentSnapshot} to a {@link Property} object.
     * @param document to be mapped to a {@link Property} object
     * @return a {@link Property} object
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
     * Map a {@link DocumentSnapshot} to a {@link Rating} object.
     * @param document to be mapped to a {@link Rating} object
     * @return a {@link Rating} object
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
     * Maps a {@link DocumentSnapshot} to a {@link Comment} object.
     * @param document to be mapped to a {@link Comment} object
     * @return a {@link Comment} instance
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

    /**
     * Maps a {@link Comment} to a variable-value {@link Map}.
     * @param comment to be mapped to a variable-value {@link Map}.
     * @return a variable-value {@link Map}.
     */
    public static Map<String, Object> commentToMap(Comment comment){
        Map<String, Object> commentMap = new HashMap<>();
        commentMap.put(VariableNames.CommentsCollection.COMMENT, comment.getComment());
        commentMap.put(VariableNames.CommentsCollection.DATE, comment.getDate());
        commentMap.put(VariableNames.CommentsCollection.PROPERTY_ID, comment.getPropertyID());
        commentMap.put(VariableNames.CommentsCollection.USER_ID, comment.getUserID());
        return commentMap;
    }

    /**
     * Map a {@link Rating} to a variable-value {@link Map}. Variable names can be found in
     * {@link VariableNames}.
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
