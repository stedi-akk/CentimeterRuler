package com.stedi.centimeterruler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class SimpleDialog extends DialogFragment {
    private static final String KEY_TITLE = "KEY_TITLE";
    private static final String KEY_MESSAGE = "KEY_MESSAGE";
    private static final String KEY_OK_TEXT = "KEY_OK_TEXT";
    private static final String KEY_CANCEL_TEXT = "KEY_CANCEL_TEXT";

    public static class OnResult {
        public final boolean okClicked;
        public final String tag;

        public OnResult(boolean okClicked, String tag) {
            this.okClicked = okClicked;
            this.tag = tag;
        }
    }

    public static SimpleDialog newInstance(String title, String message, String okText, String cancelText) {
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_MESSAGE, message);
        args.putString(KEY_OK_TEXT, okText);
        args.putString(KEY_CANCEL_TEXT, cancelText);
        SimpleDialog dlg = new SimpleDialog();
        dlg.setArguments(args);
        return dlg;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() == null)
            throw new IllegalArgumentException("arguments not found");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String title = getArguments().getString(KEY_TITLE, null);
        String message = getArguments().getString(KEY_MESSAGE, null);
        String okText = getArguments().getString(KEY_OK_TEXT, null);
        String cancelText = getArguments().getString(KEY_CANCEL_TEXT, null);
        if (title != null) {
            builder.setTitle(title);
        }
        if (message != null) {
            builder.setMessage(message);
        }
        if (okText != null) {
            builder.setPositiveButton(okText, (dialog, which) -> {
                App.getBus().post(new OnResult(true, getTag()));
            });
        }
        if (cancelText != null) {
            builder.setNegativeButton(cancelText, (dialog, which) -> {
                App.getBus().post(new OnResult(false, getTag()));
            });
        }
        return builder.create();
    }
}
