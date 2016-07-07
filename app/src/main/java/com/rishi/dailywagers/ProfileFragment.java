package com.rishi.dailywagers;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.rishi.dailywagers.constants.DaysOfWeek;
import com.rishi.dailywagers.constants.WagerConstants;
import com.rishi.dailywagers.data.DailyWagerDbHelper;
import com.rishi.dailywagers.model.Wager;
import com.rishi.dailywagers.util.DatePickerFragment;
import com.rishi.dailywagers.util.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rishi on 6/24/16.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "ProfileFragment";

    private EditText mWagerName;
    private EditText mWagerRate;
    private EditText mStartDate;
    private Button mDatePicker;
    private ToggleButton mSunday;
    private ToggleButton mMonday;
    private ToggleButton mTuesday;
    private ToggleButton mWednesday;
    private ToggleButton mThursday;
    private ToggleButton mFriday;
    private ToggleButton mSaturday;
    private Button mSave;

    private Wager mWager;
    private List<DaysOfWeek> mDaysOfWeek;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        Log.d(TAG, "On create");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "On create view");
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Add new wager");

        mWagerName = (EditText) view.findViewById(R.id.wager_name);
        mWagerRate = (EditText) view.findViewById(R.id.wager_rate);
        mStartDate = (EditText) view.findViewById(R.id.profile_start_date);
        mDatePicker = (Button) view.findViewById(R.id.profile_start_date_btn);

        mSunday = (ToggleButton) view.findViewById(R.id.sunday);
        mMonday = (ToggleButton) view.findViewById(R.id.monday);
        mTuesday = (ToggleButton) view.findViewById(R.id.tuesday);
        mWednesday = (ToggleButton) view.findViewById(R.id.wednesday);
        mThursday = (ToggleButton) view.findViewById(R.id.thursday);
        mFriday = (ToggleButton) view.findViewById(R.id.friday);
        mSaturday = (ToggleButton) view.findViewById(R.id.saturday);

        mSave = (Button) view.findViewById(R.id.save_profile);

        mSave.setOnClickListener(this);
        mDatePicker.setOnClickListener(this);

        mDaysOfWeek = new ArrayList<>();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save_profile:
                Log.d(TAG, "Save bttn clicked");
                if(isValid()){
                    mWager = new Wager();
                    mWager.setName(mWagerName.getText().toString());
                    mWager.setRate(Double.parseDouble(mWagerRate.getText().toString()));
                    mWager.setStartDate(mStartDate.getText().toString());
                    mWager.setDaysOfWeek(mDaysOfWeek);
                    new SaveProfile().execute();
                }
                break;
            case R.id.profile_start_date_btn:
                Log.d(TAG, "Select date clicked");
                DialogFragment newFragment = new DatePickerFragment(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        super.onDateSet(view, year, monthOfYear, dayOfMonth);
                        mStartDate.setText(DateUtil.getDisplayDate(year, monthOfYear, dayOfMonth));
                    }
                };
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                break;
        }
    }

    private boolean isValid(){
        if(TextUtils.isEmpty(mWagerName.getText())){
            mWagerName.setError(getString(R.string.error_message));
            return false;
        }
        if(TextUtils.isEmpty(mWagerRate.getText())){
            mWagerRate.setError(getString(R.string.error_message));
            return false;
        }
        if(TextUtils.isEmpty(mStartDate.getText())){
            mStartDate.setError(getString(R.string.error_message));
            return false;
        }
        if(!isValidDaysOfWeek()){
            Toast.makeText(getActivity(), "Select at least one day of week", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean isValidDaysOfWeek(){
        boolean isValid = false;
        if(mSunday.isChecked()){
            isValid = true;
            mDaysOfWeek.add(DaysOfWeek.SUN);
        }
        if(mMonday.isChecked()){
            isValid = true;
            mDaysOfWeek.add(DaysOfWeek.MON);
        }
        if(mTuesday.isChecked()){
            isValid = true;
            mDaysOfWeek.add(DaysOfWeek.TUE);
        }
        if(mWednesday.isChecked()){
            isValid = true;
            mDaysOfWeek.add(DaysOfWeek.WED);
        }
        if(mThursday.isChecked()){
            isValid = true;
            mDaysOfWeek.add(DaysOfWeek.THU);
        }
        if(mFriday.isChecked()){
            isValid = true;
            mDaysOfWeek.add(DaysOfWeek.FRI);
        }
        if(mSaturday.isChecked()){
            isValid = true;
            mDaysOfWeek.add(DaysOfWeek.SAT);
        }
        return isValid;
    }

    private class SaveProfile extends AsyncTask<Void, Void, Boolean>{
        @Override
        protected Boolean doInBackground(Void... params) {
            return DailyWagerDbHelper.getInstance(getContext()).saveData(mWager);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                getActivity().getSupportFragmentManager().popBackStack();
            }else{
                Toast.makeText(getActivity(), "Something went wrong...", Toast.LENGTH_LONG).show();
            }
        }
    }
}
