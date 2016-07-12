package com.rishi.dailywagers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * Created by rishi on 7/11/16.
 */
public class ConfirmDialog extends DialogFragment {
    public static final String EXTRA_OPTION =
            "com.android.find.restaurant.filter_option";

    private static final int CANCEL = 0;
    private static final int CONFIRM = 1;

    public static ConfirmDialog newInstance() {
        ConfirmDialog fragment = new ConfirmDialog();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_confirmation, null);

        ((Button)v.findViewById(R.id.confirm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                sendResult(Activity.RESULT_OK, CONFIRM);
            }
        });

        ((Button)v.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                sendResult(Activity.RESULT_CANCELED, CANCEL);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.dialog_title)
                .create();
    }

    /**
     * Send result to the required activity
     * @param resultCode
     * @param optionId
     */
    private void sendResult(int resultCode, int optionId) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_OPTION, optionId);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
