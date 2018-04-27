package com.raed.swe311project.model;

/**
 * Created by Raed on 12/04/2018.
 */

public class VariableNames {

    public static class PropertiesCollection {

        public static final String COLLECTION_NAME = "properties";

        public static final String SELLER_ID = "seller_id";
        public static final String IMAGE_URL = "image_url";
        public static final String PRICE = "price";
        public static final String LOCATION = "location";
        public static final String DESCRIPTION = "description";
        public static final String TYPE = "type";
    }

    public static class CommentsCollection{

        public static final String COLLECTION_NAME = "comments";

        public static final String COMMENT = "comment";
        public static final String PROPERTY_ID = "property_id";
        public static final String USER_ID = "user_id";
        public static final String DATE = "date";
    }

    public static class RatingCollection{

        public static final String COLLECTION_NAME = "ratings";

        public static final String NUM_OF_RATING = "num_of_ratings";
        public static final String RATING = "rating";
        public static final String USER_RATING_MAP = "user_rating_map";

    }

}
