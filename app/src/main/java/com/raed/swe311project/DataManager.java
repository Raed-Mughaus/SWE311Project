package com.raed.swe311project;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.raed.swe311project.model.Comment;
import com.raed.swe311project.model.Property;
import com.raed.swe311project.model.Rating;
import com.raed.swe311project.model.VariableNames.CommentsCollection;
import com.raed.swe311project.model.VariableNames.PropertiesCollection;
import com.raed.swe311project.model.VariableNames.RatingCollection;
import com.raed.swe311project.utilities.ObjectMapper;
import com.raed.swe311project.utilities.RatingMath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class store/fetch data from the database.
 **/

//todo find a way to prevent memory leaks from happening, the task callback may have a reference to an activity
//todo move the rating logic to the server side to make it more secure and efficient.

public class DataManager {

    private static final String TAG = "DataManager";

    private static final DataManager sInstance = new DataManager();

    private FirebaseFirestore mFirebaseFirestore;

    public static DataManager getInstance() {
        return sInstance;
    }

    private DataManager() {
        mFirebaseFirestore = FirebaseFirestore.getInstance();
    }

    /**
     * Load all properties from the database.
     * @param callback pass an instance of this class to receive the fetched properties and to be
     *                 informed weather the task completed successful or failed.
     **/
    public void loadProperties(final TaskCallback<List<Property>> callback){
        mFirebaseFirestore
                .collection(PropertiesCollection.COLLECTION_NAME)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<Property> properties = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Property property = ObjectMapper.documentToProperty(document);
                                properties.add(property);
                            }
                            callback.onTaskExecuted(properties);
                        } else {
                            Log.e(TAG, "Error while loading properties: ", task.getException());
                            callback.onError(task.getException());
                        }
                    }
                });
    }

    /**
     * Load all ratings from the database.
     * @param callback pass an instance of this class to receive the ratings and to be informed
     *                weather the task completed successful or failed.
     */
    public void loadRatings(final TaskCallback<List<Rating>> callback){
        mFirebaseFirestore
                .collection(RatingCollection.COLLECTION_NAME)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<Rating> ratings = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Rating rating = ObjectMapper.documentToRating(document);
                                ratings.add(rating);
                            }
                            callback.onTaskExecuted(ratings);
                        } else {
                            Log.e(TAG, "Error loading ratings",task.getException() );
                            callback.onError(task.getException());
                        }
                    }
                });
    }

    /**
     * Load all comments from the database for a specific property.
     * @param propertyID to load comments associated with this id
     * @param callback pass an instance of this class to receive the comments and to be informed
     *                weather the task is successfully completed or not.
     */
    public void loadPropertyComments(final String propertyID, final TaskCallback<List<Comment>> callback){
        mFirebaseFirestore
                .collection(CommentsCollection.COLLECTION_NAME)
                .whereEqualTo(CommentsCollection.PROPERTY_ID, propertyID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()){
                            Log.e(TAG, "Error while loading comments for property " + propertyID, task.getException());
                            callback.onError(task.getException());
                            return;
                        }
                        List<Comment> comments = new ArrayList<>();
                        for (QueryDocumentSnapshot docSnapshot : task.getResult()){
                            Comment comment = ObjectMapper.documentToComment(docSnapshot);
                            comments.add(comment);
                        }
                        callback.onTaskExecuted(comments);
                    }
                });
    }

    /**
     * Add a new comment to the database
     * @param comment to be posted
     */
    public void postComment(Comment comment){
        Map<String, Object> commentMap = ObjectMapper.commentToMap(comment);
        mFirebaseFirestore
                .collection(CommentsCollection.COLLECTION_NAME)
                .document()
                .set(commentMap);
    }

    /**
     * If the user has already rated this property, the old rating will be replaced with new one.
     * Otherwise a new rating will be added.
     * @param property to be rated.
     * @param rating can be a value from 0 to 5.
     * @param callback pass an instance of this class to receive the updated property rating and to
     *                be informed weather the task is successful completed or not.
     * @throws IllegalArgumentException if the rating is outside the range(0 to 5)
     */
    public void rateProperty(final Property property, final float rating, final TaskCallback<Rating> callback) {
        Log.d(TAG, "Rate property " + property.getID() + " by " + rating);
        if (rating < 0 || rating > 5)
            throw new IllegalArgumentException("Rating must be between 0 and 5");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
            throw new SecurityException("Rating a property requires an authorized user");

        mFirebaseFirestore
                .runTransaction(new Transaction.Function<Rating>() {
                    @Override
                    public Rating apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        return executeRatePropertyTransaction(property.getID(), rating, userID, transaction);
                    }
                }).addOnCompleteListener(new OnCompleteListener<Rating>() {
                    @Override
                    public void onComplete(@NonNull Task<Rating> task) {
                        if (task.isSuccessful()){
                            Rating r = task.getResult();
                            callback.onTaskExecuted(r);
                        }else {
                            Exception exception = task.getException();
                            Log.e(TAG, exception.getMessage(), exception);
                            callback.onError(exception);
                        }
                    }
                });
    }

    //we need a transaction to make sure the rating data is consistent.
    private Rating executeRatePropertyTransaction(String propertyID, float newUserRating, String userID, Transaction transaction) throws FirebaseFirestoreException {
        DocumentReference ratingDocRef = mFirebaseFirestore
                .document(RatingCollection.COLLECTION_NAME + "/" + propertyID);

        Rating oldRating = null;
        DocumentSnapshot documentSnapshot = transaction.get(ratingDocRef);
        if (documentSnapshot.exists()) {
            //fetch the old rating if any
            oldRating = ObjectMapper.documentToRating(documentSnapshot);
        }

        //Calculate the new rating
        Rating newRating = RatingMath.calcNewRating(propertyID, oldRating, userID, newUserRating);

        //update or store the new rating
        Map<String, Object> newRatingMap = ObjectMapper.ratingToMap(newRating);
        if (documentSnapshot.exists()) {
            transaction.update(ratingDocRef, newRatingMap);
        }else {
            transaction.set(ratingDocRef, newRatingMap);
        }

        return newRating;
    }

}
