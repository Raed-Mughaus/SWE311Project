package com.raed.swe311project.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.raed.swe311project.R;
import com.raed.swe311project.DataManager;
import com.raed.swe311project.TaskCallback;
import com.raed.swe311project.model.Property;
import com.raed.swe311project.model.Rating;
import com.raed.swe311project.view.RatingView;
import com.squareup.picasso.Picasso;

public class PropertyActivity extends AppCompatActivity implements RatingDialog.Callback, MessageDialog.Callback{

    private static final String EXTRA_KEY_PROPERTY = "property_key";

    private Property mProperty;

    private View mRatingViewRoot;
    private RatingView mRatingView;
    private TextView mNumOfRating;
    private TextView mRatingTextView;

    public static Intent getIntent(Context context, Property property){
        Intent intent =  new Intent(context, PropertyActivity.class);
        intent.putExtra(EXTRA_KEY_PROPERTY, property);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property);

        mProperty = (Property) getIntent().getSerializableExtra(EXTRA_KEY_PROPERTY);

        TextView shortDescriptionTextView = findViewById(R.id.info);
        shortDescriptionTextView.setText(
                mProperty.getPropertyType() + " in " + mProperty.getLocation()
                        + "\nPrice: " + mProperty.getPrice() + " $");

        TextView longDescriptionTextView = findViewById(R.id.description);
        longDescriptionTextView.setText(mProperty.getDescription());

        findViewById(R.id.view_comments_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(CommentActivity.newIntent(PropertyActivity.this, mProperty.getID()));
            }
        });

        findViewById(R.id.rate_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null){
                    MessageDialog messageDialog = MessageDialog.newInstance("To rate a property you need to login", "Let's go", "Cancel");
                    messageDialog.show(getSupportFragmentManager(), null);
                    return;
                }
                RatingDialog dialog = RatingDialog.newInstance("Rate A Property");
                dialog.show(getSupportFragmentManager(), null);
            }
        });

        ImageView propertyImageView = findViewById(R.id.image_view);

        Picasso
                .get()
                .load(mProperty.getImageURL())
                .into(propertyImageView);

        mRatingViewRoot = findViewById(R.id.rating_root_view);
        mNumOfRating = findViewById(R.id.num_of_ratings);
        mRatingView = findViewById(R.id.rating_bar);
        mRatingTextView = findViewById(R.id.rating);
        updateRating();

    }

    //This is used for communication with RatingDialog
    @Override
    public void onRated(final float rating) {
        DataManager
                .getInstance()
                .rateProperty(
                        mProperty,
                        rating,
                        new TaskCallback<Rating>() {
                            @Override
                            public void onTaskExecuted(Rating rating) {
                                //if the rating task completed successfully1 update mProperty and then update the displayed rating information

                                mProperty.setRating(rating);
                                updateRating();
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(PropertyActivity.this, "an error happen while rating the property", Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    private void updateRating(){
        Rating rating = mProperty.getRating();
        if (rating == null){
            mRatingViewRoot.setVisibility(View.GONE);
            return;
        }
        mRatingViewRoot.setVisibility(View.VISIBLE);
        mNumOfRating.setText((rating.getNumOfRating() + ""));
        mRatingView.setRating(rating.getAvgRating());
        float propertyRating = rating.getAvgRating();
        String propertyRatingStr = (propertyRating + "").substring(0, 3);//show only 2 digits
        mRatingTextView.setText((propertyRatingStr  + "/" + 5));
    }

    //This is for MessageDialog
    @Override
    public void onButtonClicked(boolean positiveButtonClicked) {
        if (positiveButtonClicked)
            startActivity(new Intent(this, LoginActivity.class));
    }
}
