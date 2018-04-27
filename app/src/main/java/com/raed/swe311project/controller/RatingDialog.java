package com.raed.swe311project.controller;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import com.raed.swe311project.R;
import com.raed.swe311project.view.RatingView;


/**
 *
 * Client activities must implement {@link RatingDialog.Callback} interface to receive rating results.
 *
 */

public class RatingDialog extends DialogFragment {

    private static final String KEY_DIALOG_TITLE = "key_title";

    private Callback mCallback;
    private RatingView mRatingView;

    /**
     * @param dialogTitle this will appear at the top of the dialog, can be null
     * @return a dialog with the specified title.
     */
    public static RatingDialog newInstance(String dialogTitle){
        Bundle args = new Bundle();
        args.putString(KEY_DIALOG_TITLE, dialogTitle);
        RatingDialog dialog =  new RatingDialog();
        dialog.setArguments(args);
        return dialog;
    }

    interface Callback{
        void onRated(float rating);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (Callback) context;//initialize a reference to the parent activity so we can notify it about the rating.
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mRatingView = (RatingView) LayoutInflater.from(getContext()).inflate(R.layout.dialog_rate, null);
        return new AlertDialog.Builder(getContext())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mCallback.onRated(mRatingView.getRating());
                    }
                })
                .setNegativeButton("CANCEL", null)//no need to do anything if the user press on cancel button
                .setTitle(getArguments().getString(KEY_DIALOG_TITLE, ""))
                .setView(mRatingView)
                .create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;//to prevent memory leaks
    }
}
