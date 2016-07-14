package com.rishi.dailywagers;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.rishi.dailywagers.constants.ExcludedDaysOfWeek;
import com.rishi.dailywagers.data.DailyWagerDbHelper;
import com.rishi.dailywagers.model.Wager;
import com.rishi.dailywagers.util.DatePickerFragment;
import com.rishi.dailywagers.util.DateUtil;
import com.rishi.dailywagers.util.IDatePickerDialogListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by rishi on 6/24/16.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "ProfileFragment";
    private static final String WAGER_ID_ARG = "wager_id";
    private static final int REQUEST_CONFIRM_OPTION = 0;
    private static final String DIALOG_CONFIRM = "DialogConfirm";

    private boolean mIsNew = true;
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
    private List<ExcludedDaysOfWeek> mExcludedDaysOfWeeks;

    public static ProfileFragment getFragment(UUID wagerId){
        ProfileFragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString(WAGER_ID_ARG, wagerId.toString());
        fragment.setArguments(bundle);

        return fragment;
    }
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

        Bundle args = getArguments();
        if(args != null && args.getString(WAGER_ID_ARG) != null){
            UUID wagerId = UUID.fromString(getArguments().getString(WAGER_ID_ARG));
            mWager = DailyWagerDbHelper.getInstance(getContext()).getCurrentWager(wagerId);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Update "+ mWager.getName());
            mIsNew = false;
        }else {
            mWager = new Wager();
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Add new wager");
        }

        mWagerName = (EditText) view.findViewById(R.id.profile_wager_name);
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

        if(!mIsNew){
            populateWagerProfile();
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save_profile:
                Log.d(TAG, "Save bttn clicked");
                if(isValid()){
                    populateWagerData();
                    new SaveProfile().execute();
                }
                break;
            case R.id.profile_start_date_btn:
                Log.d(TAG, "Select date clicked");
                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(getContext(), R.string.date_picker_title);
                datePickerFragment.setDatePickerDialogListener(new IDatePickerDialogListener() {
                    @Override
                    public void setDate(int year, int month, int day) {
                        mStartDate.setText(DateUtil.getDisplayDate(year, month, day));
                    }
                });
                datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(TAG, "Menu creation");
        if(!mIsNew){
            inflater.inflate(R.menu.fragment_profile_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_profile:
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                ConfirmDialog dialog = ConfirmDialog.newInstance();
                dialog.setTargetFragment(ProfileFragment.this, REQUEST_CONFIRM_OPTION);

                dialog.show(fragmentManager, DIALOG_CONFIRM);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CONFIRM_OPTION){
            if(resultCode == Activity.RESULT_OK){
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        return DailyWagerDbHelper.getInstance(getContext()).deleteWager(mWager.getId().toString());
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        super.onPostExecute(aBoolean);
                        if(aBoolean){
                            Toast.makeText(getActivity(), "Data deleted successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), HomeActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }else {
                            Toast.makeText(getActivity(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
            }
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
        mExcludedDaysOfWeeks = new ArrayList<>();
        if(!mSunday.isChecked()){
            mExcludedDaysOfWeeks.add(ExcludedDaysOfWeek.SUN);
        }
        if(!mMonday.isChecked()){
            mExcludedDaysOfWeeks.add(ExcludedDaysOfWeek.MON);
        }
        if(!mTuesday.isChecked()){
            mExcludedDaysOfWeeks.add(ExcludedDaysOfWeek.TUE);
        }
        if(!mWednesday.isChecked()){
            mExcludedDaysOfWeeks.add(ExcludedDaysOfWeek.WED);
        }
        if(!mThursday.isChecked()){
            mExcludedDaysOfWeeks.add(ExcludedDaysOfWeek.THU);
        }
        if(!mFriday.isChecked()){
            mExcludedDaysOfWeeks.add(ExcludedDaysOfWeek.FRI);
        }
        if(!mSaturday.isChecked()){
            mExcludedDaysOfWeeks.add(ExcludedDaysOfWeek.SAT);
        }
        if(mExcludedDaysOfWeeks.size() == 7){
            return false;
        }
        return true;
    }

    private void populateWagerProfile(){
        mWagerName.setText(mWager.getName());
        mWagerRate.setText(mWager.getRate()+"");
        mStartDate.setText(mWager.getStartDate());

        for(ExcludedDaysOfWeek day : mWager.getExcludedDaysOfWeeks()){
            switch (day){
                case SUN:
                    mSunday.setChecked(false);
                    break;
                case MON:
                    mMonday.setChecked(false);
                    break;
                case TUE:
                    mTuesday.setChecked(false);
                    break;
                case WED:
                    mWednesday.setChecked(false);
                    break;
                case THU:
                    mThursday.setChecked(false);
                    break;
                case FRI:
                    mFriday.setChecked(false);
                    break;
                case SAT:
                    mSaturday.setChecked(false);
                    break;
            }
        }
    }

    private void populateWagerData(){
        mWager.setName(mWagerName.getText().toString());
        mWager.setRate(Double.parseDouble(mWagerRate.getText().toString()));
        mWager.setStartDate(mStartDate.getText().toString());
        mWager.setExcludedDaysOfWeeks(mExcludedDaysOfWeeks);
    }

    private class SaveProfile extends AsyncTask<Void, Void, Boolean>{
        @Override
        protected Boolean doInBackground(Void... params) {
            return DailyWagerDbHelper.getInstance(getContext()).saveWager(mWager);
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
