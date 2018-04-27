package com.raed.swe311project.controller;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.raed.swe311project.R;

/**
 *
 * A simple dialog that show a message with 2 buttons, and set the result based on the clicked
 * button.
 */

public class MessageDialog extends DialogFragment {


    private static final String KEY_MESSAGE = "message_key";
    private static final String KEY_POSITIVE_BUTTON_TEXT = "message_positive_button_text";
    private static final String KEY_NEGATIVE_BUTTON_TEXT = "message_negative_button_text";

    private Callback mCallback;

    /**
     * @param message to be displayed in a dialog, if null the result is undefined.
     * @param positiveButton if null a default text will be used.
     * @param negativeButton if null a default text will be used.
     */
    public static MessageDialog newInstance(String message, String positiveButton, String negativeButton){
        MessageDialog dialog = new MessageDialog();
        Bundle arguments = new Bundle();
        arguments.putString(KEY_MESSAGE, message);
        arguments.putString(KEY_POSITIVE_BUTTON_TEXT, positiveButton);
        arguments.putString(KEY_NEGATIVE_BUTTON_TEXT, negativeButton);
        dialog.setArguments(arguments);
        return dialog;
    }

    interface Callback{
        void onButtonClicked(boolean positiveButtonClicked);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (Callback) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.dialog_message, null);
        textView.setText(getArguments().getString(KEY_MESSAGE));

        String positiveButtonString = getArguments().getString(KEY_POSITIVE_BUTTON_TEXT);
        if (positiveButtonString == null)
            positiveButtonString = "OK";
        String negativeButtonString = getArguments().getString(KEY_NEGATIVE_BUTTON_TEXT);
        if (negativeButtonString == null)
            negativeButtonString = "CANCEL";
        return new AlertDialog.Builder(getContext())
                .setView(textView)
                .setPositiveButton( positiveButtonString , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendResult(true);
                    }
                })
                .setNegativeButton(negativeButtonString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendResult(false);
                    }
                })
                .create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;//to prevent memory leaks
    }

    private void sendResult(boolean result){
        mCallback.onButtonClicked(result);
    }
}
