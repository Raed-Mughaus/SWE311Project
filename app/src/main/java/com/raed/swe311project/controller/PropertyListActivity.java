package com.raed.swe311project.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PropertyListActivity extends AppCompatActivity {

    private static final String EXTRA_KEY_SKIP_LOGIN = "skip_login";

    private RecyclerView mRecyclerView;

    private List<Property> mProperties;
    private List<Rating> mRatings;

    public static Intent newIntent(Context context, boolean skipLogin){
        Intent intent = new Intent(context, PropertyListActivity.class);
        intent.putExtra(EXTRA_KEY_SKIP_LOGIN, skipLogin);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_list);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null && !getIntent().getBooleanExtra(EXTRA_KEY_SKIP_LOGIN, false)){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mProperties = null;//so we update them next time
            mRatings = null;//so update them next time
            startActivity(intent);
        }

        mRecyclerView = findViewById(R.id.recycler_view);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sign_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        DataManager.getInstance().loadProperties(
                new TaskCallback<List<Property>>() {
                    @Override
                    public void onTaskExecuted(List<Property> properties) {
                        mProperties = properties;
                        updateUI();
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(PropertyListActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        DataManager.getInstance().loadRatings(
                new TaskCallback<List<Rating>>() {
                    @Override
                    public void onTaskExecuted(List<Rating> ratings) {
                        mRatings = ratings;
                        updateUI();
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(PropertyListActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }

    private void updateUI() {
        if (mProperties != null && mRatings != null){
            for (Property property: mProperties) {
                Rating rating = null;
                for (Rating r : mRatings) {
                    if (r.getPropertyID().equals(property.getID())){
                        rating = r;
                        break;
                    }
                }
                property.setRating(rating);
            }
            mRecyclerView.setAdapter(new PropertyAdapter(mProperties));
            mRecyclerView.setLayoutManager(new LinearLayoutManager(PropertyListActivity.this));
        }
    }

    private class PropertyHolder extends RecyclerView.ViewHolder{

        private Property mProperty;

        private ImageView mPropertyImageView;
        private TextView mPriceTextView;
        private TextView mPropertyInfoTextView;
        private RatingView mPropertyRatingView;

        public PropertyHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(PropertyActivity.getIntent(PropertyListActivity.this, mProperty));
                }
            });

            mPropertyImageView = itemView.findViewById(R.id.property_image);
            mPriceTextView = itemView.findViewById(R.id.property_price);
            mPropertyInfoTextView = itemView.findViewById(R.id.property_info);
            mPropertyRatingView = itemView.findViewById(R.id.rating_bar);
        }

        void bindProperty(Property property){
            mProperty = property;


            int price = mProperty.getPrice();
            if (price >= 1000000)
                mPriceTextView.setText((mProperty.getPrice()/1000000) + "M $");
            else if (price >= 1000)
                mPriceTextView.setText((mProperty.getPrice()/1000) + "K $");

            Rating rating = mProperty.getRating();
            if (rating == null)
                mPropertyRatingView.setVisibility(View.INVISIBLE);
            else {
                mPropertyRatingView.setRating(mProperty.getRating().getAvgRating());
                mPropertyRatingView.setVisibility(View.VISIBLE);
            }

            mPropertyInfoTextView.setText(mProperty.getPropertyType() + " in " + mProperty.getLocation());

            Picasso
                    .get()
                    .load(mProperty.getImageURL())
                    .resize(500, 400)
                    .into(mPropertyImageView);
        }
    }

    private class PropertyAdapter extends RecyclerView.Adapter<PropertyHolder>{

        private List<Property> mProperties;

        PropertyAdapter(List<Property> properties){
            mProperties = properties;
            Collections.sort(mProperties, new Comparator<Property>() {
                @Override
                public int compare(Property property, Property t1) {
                    return property.getID().compareTo(t1.getID());
                }
            });
        }

        @Override
        public PropertyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PropertyHolder(LayoutInflater.from(PropertyListActivity.this)
                    .inflate(R.layout.item_property, parent, false));
        }

        @Override
        public void onBindViewHolder(PropertyHolder holder, int position) {
            holder.bindProperty(mProperties.get(position));
        }

        @Override
        public int getItemCount() {
            return mProperties.size();
        }
    }

}
