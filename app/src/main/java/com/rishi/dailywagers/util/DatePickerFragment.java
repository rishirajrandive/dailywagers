package com.rishi.dailywagers.util;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by rishi on 6/29/16.
 */
@SuppressLint("ValidFragment")
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    private static Context sContext;
    private static IDatePickerDialogListener sListener;

    public static DatePickerFragment newInstance(Context context, int titleResource){
        DatePickerFragment dialog  = new DatePickerFragment();
        sContext = context;

        Bundle args = new Bundle();
        args.putInt("title", titleResource);
        dialog.setArguments(args);
        return dialog;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // NO implementation required here
        sListener.setDate(year, monthOfYear, dayOfMonth);
    }

    public void setDatePickerDialogListener(IDatePickerDialogListener listener){
        sListener = listener;
    }
}
