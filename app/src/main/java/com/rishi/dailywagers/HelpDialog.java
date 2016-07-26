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
public class HelpDialog extends DialogFragment {
    public static final String EXTRA_OPTION =
            "com.rishi.dailywagers.helpdialog";

    private static final int CLOSE = 1;

    public static HelpDialog newInstance() {
        HelpDialog fragment = new HelpDialog();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_help, null);

        ((Button)v.findViewById(R.id.close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                sendResult(Activity.RESULT_OK, CLOSE);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.help_title)
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
