package com.raed.swe311project.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.raed.swe311project.R;
import com.raed.swe311project.TaskCallback;
import com.raed.swe311project.model.Comment;
import com.raed.swe311project.DataManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CommentActivity extends AppCompatActivity implements MessageDialog.Callback{

    private static final String KEY_PROPERTY_ID = "property_id_key";

    private RecyclerView mRecyclerView;
    private EditText mCommentEditText;
    private CommentAdapter mCommentAdapter;

    private String mPropertyID;

    public static Intent newIntent(Context context, String propertyID){
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra(KEY_PROPERTY_ID, propertyID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        getSupportActionBar().setTitle("Property Comments");

        mPropertyID = getIntent().getStringExtra(KEY_PROPERTY_ID);

        mCommentEditText = findViewById(R.id.comment_edit_text);

        mRecyclerView = findViewById(R.id.recycler_view);
        mCommentAdapter = new CommentAdapter();
        mRecyclerView.setAdapter(mCommentAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(CommentActivity.this));

        findViewById(R.id.post_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               postComment();
            }
        });

        DataManager
                .getInstance()
                .loadPropertyComments(mPropertyID, new TaskCallback<List<Comment>>() {
                    @Override
                    public void onTaskExecuted(List<Comment> comments) {
                        mCommentAdapter.addComments(comments);
                        mCommentAdapter.notifyDataSetChanged();//update the UI
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(CommentActivity.this, "Error while loading comments", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void postComment(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            MessageDialog messageDialog =
                    MessageDialog.newInstance(
                            "You need to sign in or sign up to be able to post comments",
                            "Let's Go",
                            null);
            messageDialog.show(getSupportFragmentManager(), null);
            return;
        }
        Comment comment = new Comment();
        String commentString = mCommentEditText.getText().toString();
        if (commentString.length() == 0){
            Toast.makeText(CommentActivity.this, "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }
        comment.setComment(commentString);
        comment.setDate(System.currentTimeMillis());
        comment.setUserID(user.getUid());
        comment.setPropertyID(mPropertyID);

        DataManager
                .getInstance()
                .postComment(comment);

        mCommentAdapter.mComments.add(comment);
        int position = mCommentAdapter.mComments.size() - 1;
        mCommentAdapter.notifyItemInserted(position);
        mRecyclerView.smoothScrollToPosition(position);

        mCommentEditText.setText("");
    }

    //this is used for communication with MessageDialog
    @Override
    public void onButtonClicked(boolean positiveButtonClicked) {
        if (positiveButtonClicked)
            startActivity(new Intent(this, LoginActivity.class));
    }

    private class CommentHolder extends RecyclerView.ViewHolder{

        private TextView mCommentTextView;
        private TextView mDateTextView;


        CommentHolder(View itemView) {
            super(itemView);
            mCommentTextView = itemView.findViewById(R.id.comment);
            mDateTextView = itemView.findViewById(R.id.date);
        }

        void bindComment(Comment comment, boolean even) {
            itemView.setBackgroundColor(even ? 0xffffffff : 0x00ffffff);

            mCommentTextView.setText(comment.getComment());
            mDateTextView.setText(new Date(comment.getDate()).toString());
        }
    }

    private class CommentAdapter extends RecyclerView.Adapter<CommentHolder>{

        private List<Comment> mComments = new ArrayList<>();

        @Override
        public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CommentHolder(LayoutInflater.from(CommentActivity.this)
                    .inflate(R.layout.item_comment, parent, false));
        }

        @Override
        public void onBindViewHolder(CommentHolder holder, int position) {
            holder.bindComment(mComments.get(position), position % 2 == 0);
        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }

        private void addComments(List<Comment> comments){
            mComments.addAll(comments);
            Collections.sort(mComments, new Comparator<Comment>() {
                @Override
                public int compare(Comment comment, Comment t1) {
                    return comment.getDate() > t1.getDate() ? 1 : -1;
                }
            });
        }
    }

}
