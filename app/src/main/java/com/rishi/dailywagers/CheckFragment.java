package com.rishi.dailywagers;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;
import com.rishi.dailywagers.data.DailyWagerDbHelper;
import com.rishi.dailywagers.decorators.AbsentDecorator;
import com.rishi.dailywagers.decorators.ExcludedDayDecorator;
import com.rishi.dailywagers.decorators.TodayDecorator;
import com.rishi.dailywagers.decorators.WeekendDecorator;
import com.rishi.dailywagers.model.Wager;
import com.rishi.dailywagers.util.DateUtil;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by rishi on 6/24/16.
 */
public class CheckFragment extends Fragment implements OnDateSelectedListener, OnRangeSelectedListener,
        OnMonthChangedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static final String TAG = "CheckFragment";
    public static boolean sDataSaved = false;

    private static final String ARG_WAGER_ID = "wagerId";

    private ProgressDialog mProgressDialog;

    private Wager mWager;
    private CalendarDay mSelectedDay;
    private CalendarDay mToday;
    private int mSelectedMonth;

    private List<CalendarDay> mSelectedDateRange;
    private MaterialCalendarView mCalendarView;
    private TextView mDueMonthAmount;
    private EditText mRateForTheDay;
    private Button mRateEditStart;
    private Button mRateEditDone;
    private Button mRateEditCancel;
    private CheckBox mDayAttendance;
    private CheckBox mDateRangeAttendance;
    private Switch mCustomDates;
    private Button mSaveChanges;


    public static CheckFragment newInstance(UUID wagerId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_WAGER_ID, wagerId);

        CheckFragment fragment = new CheckFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        UUID wagerId = (UUID) getArguments().getSerializable(ARG_WAGER_ID);
        mWager = DailyWagerDbHelper.getInstance(getContext()).getCurrentWager(wagerId);
        mToday = DateUtil.getCalendarDay(DateUtil.getCurrentDisplayDate());
        mSelectedDay = mToday;
        mSelectedMonth = mToday.getMonth();

        Log.d(TAG, "On create " + mWager.getName());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "On create view");
        View view = inflater.inflate(R.layout.fragment_check, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(mWager.getName());

        getActivity().findViewById(R.id.add_new_wager).setVisibility(View.GONE);
        mCalendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        mDueMonthAmount = (TextView) view.findViewById(R.id.due_amount);
        mRateForTheDay = (EditText) view.findViewById(R.id.edit_rate_of_day);
        mRateEditStart = (Button) view.findViewById(R.id.edit_start);
        mRateEditDone = (Button) view.findViewById(R.id.edit_done);
        mRateEditCancel = (Button) view.findViewById(R.id.edit_cancel);
        mDayAttendance = (CheckBox) view.findViewById(R.id.day_attendance);
        mDateRangeAttendance = (CheckBox) view.findViewById(R.id.range_attendance);
        mCustomDates = (Switch) view.findViewById(R.id.date_select_switch);
        mSaveChanges = (Button) view.findViewById(R.id.save_changes);

        mCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        mCalendarView.setOnDateChangedListener(this);
        mCalendarView.setOnRangeSelectedListener(this);
        mCalendarView.setOnMonthChangedListener(this);
        mCalendarView.state().edit().setMinimumDate(DateUtil.getCalendarDay(mWager.getStartDate())).setMaximumDate(mSelectedDay).commit();

        mRateEditStart.setOnClickListener(this);
        mRateEditDone.setOnClickListener(this);
        mRateEditCancel.setOnClickListener(this);
        mSaveChanges.setOnClickListener(this);
        mDayAttendance.setOnClickListener(this);
        mDateRangeAttendance.setOnClickListener(this);

        mCustomDates.setOnCheckedChangeListener(this);

        mCalendarView.addDecorators(new TodayDecorator(), new WeekendDecorator(),
                new AbsentDecorator(mWager.getAbsentDates()), new ExcludedDayDecorator(mWager.getExcludedDaysOfWeeks()));
        //mCalendarView.invalidateDecorators();

        updateAmountForMonth();
        updateDataForDay();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "ON start for the app");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "On resume for the app");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(TAG, "Menu creation");
        inflater.inflate(R.menu.fragment_check_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_profile:
                AbstractFragmentActivity.updateFragment(ProfileFragment.getFragment(mWager.getId()), ProfileFragment.TAG, getActivity().getSupportFragmentManager());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        Log.d(TAG, "Date selected "+ date.toString() + "  " + selected);
        //TODO Show amount for the selected dates
        mSelectedDay = date;
        updateDataForDay();
    }

    @Override
    public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {
        Log.d(TAG, "Date range selected "+ dates.size());
        //TODO Show amount for the selected dates
        mSelectedDateRange = dates;
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        Log.d(TAG, "Month changed "+ date.toString() + " " + date.getMonth());
        //TODO Reset the amount shown to current month total
        Calendar cal = date.getCalendar();
        mSelectedMonth = date.getMonth();
        updateAmountForMonth();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_done:
                Map<CalendarDay, Double> changedRate = mWager.getChangedRate();
                Double newRate = Double.parseDouble(mRateForTheDay.getText().toString());
                changedRate.put(mSelectedDay, newRate);
                mWager.setChangedRate(changedRate);
                enableRateEdit(false);
                break;
            case R.id.edit_cancel:
                enableRateEdit(false);
                break;
            case R.id.edit_start:
                enableRateEdit(true);
                break;
            case R.id.day_attendance:
                toggleSaveButton(true);
                List<CalendarDay> absentDates = mWager.getAbsentDates();
                if(mDayAttendance.isChecked() && absentDates.indexOf(mSelectedDay) != -1){
                    absentDates.remove(absentDates.indexOf(mSelectedDay));
                }else if(absentDates.indexOf(mSelectedDay) == -1){
                    absentDates.add(mSelectedDay);
                }
                mWager.setAbsentDates(absentDates);
                break;
            case R.id.range_attendance:
                toggleSaveButton(true);
                updateAbsentDates();
                break;
            case R.id.save_changes:
                showProgressDialog("Saving data...");
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        return DailyWagerDbHelper.getInstance(getContext()).saveWager(mWager);
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        super.onPostExecute(aBoolean);
                        hideProgressDialog();
                        if(aBoolean){
                            sDataSaved = true;
                            mCalendarView.invalidateDecorators();
                            updateAmountForMonth();
                            toggleSaveButton(false);
                            Toast.makeText(getActivity(), "Data updated successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getActivity(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.date_select_switch:
                toggleMode();
                break;
        }
    }

    private void updateAmountForMonth(){
        Log.d(TAG, "Updating amount for month");
        int totalDays;
        if(mToday.getMonth() == mSelectedMonth){
            totalDays = DateUtil.getDaysUsingEndDate(mToday, mWager.getExcludedDaysOfWeeks());
        }else if(mSelectedMonth == DateUtil.getCalendarDay(mWager.getStartDate()).getMonth()){
            totalDays = DateUtil.getDaysUsingStartDate(DateUtil.getCalendarDay(mWager.getStartDate()), mWager.getExcludedDaysOfWeeks());
        }else {
            totalDays = DateUtil.getDaysInMonth(mSelectedMonth, mWager.getExcludedDaysOfWeeks());
        }

        totalDays = totalDays - (getAbsentDays() + getChangedRateDays());
        double totalAmount = (totalDays * mWager.getRate()) + getChangedRateTotal();
        mDueMonthAmount.setText(totalAmount + "");
    }

    private void updateAbsentDates(){
        Log.d(TAG, "Get absent dates");
        List<CalendarDay> absentDates = mWager.getAbsentDates();
        if(mSelectedDateRange != null){
            for(CalendarDay date: mSelectedDateRange){
                if(absentDates.contains(date)){
                    absentDates.remove(absentDates.indexOf(date));
                }
            }
            absentDates.addAll(mSelectedDateRange);
        }else {
            Toast.makeText(getActivity(), "Select range of dates", Toast.LENGTH_SHORT).show();
        }
        mWager.setAbsentDates(absentDates);
    }

    private double getChangedRateTotal(){
        Log.d(TAG, "Get changed rate");
        Map<CalendarDay, Double> changedRate = mWager.getChangedRate();
        double changedRateTotal = 0;
        for (Map.Entry<CalendarDay, Double> entry : changedRate.entrySet()) {
            CalendarDay key = entry.getKey();
            if(mSelectedMonth == key.getMonth()){
                changedRateTotal = changedRateTotal + entry.getValue();
            }
        }
        return changedRateTotal;
    }

    private void toggleMode(){
        if(mCustomDates.isChecked()){
            mCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);

            mDayAttendance.setVisibility(View.GONE);
            mRateEditStart.setVisibility(View.GONE);

            mDateRangeAttendance.setVisibility(View.VISIBLE);

        }else {
            mCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);

            mDayAttendance.setVisibility(View.VISIBLE);
            mRateEditStart.setVisibility(View.VISIBLE);

            mDateRangeAttendance.setVisibility(View.GONE);
        }
        mRateEditDone.setVisibility(View.GONE);
        mRateEditCancel.setVisibility(View.GONE);
        toggleSaveButton(true);
    }

    private void enableRateEdit(boolean isShow){
        if(isShow){
            toggleSaveButton(true);

            mRateEditStart.setVisibility(View.GONE);

            mRateForTheDay.setEnabled(true);
            mRateForTheDay.requestFocus();

            mRateEditDone.setVisibility(View.VISIBLE);
            mRateEditCancel.setVisibility(View.VISIBLE);
        }else {
            mRateEditStart.setVisibility(View.VISIBLE);

            mRateForTheDay.setEnabled(false);
            mRateForTheDay.clearFocus();

            mRateEditDone.setVisibility(View.GONE);
            mRateEditCancel.setVisibility(View.GONE);
        }
    }

    private void updateDataForDay(){
        Log.d(TAG, "Updating for the day");
        if(mWager.getAbsentDates().indexOf(mSelectedDay) != -1){
            mDayAttendance.setChecked(false);
        }else{
            mDayAttendance.setChecked(true);
        }

        if(mWager.getChangedRate().containsKey(mSelectedDay)){
            mRateForTheDay.setText(mWager.getChangedRate().get(mSelectedDay).toString());
        }else {
            mRateForTheDay.setText(mWager.getRate()+"");
        }
    }

    private void toggleSaveButton(boolean isShow) {
        if(isShow){
            mSaveChanges.setVisibility(View.VISIBLE);
        }else {
            mSaveChanges.setVisibility(View.GONE);
        }
    }

    private int getAbsentDays(){
        int absentDays = 0;
        for(CalendarDay day : mWager.getAbsentDates()){
            if(day.getMonth() == mSelectedMonth){
                absentDays++;
            }
        }
        return absentDays;
    }

    private int getChangedRateDays(){
        int changedRateDays = 0;
        for (Map.Entry<CalendarDay, Double> entry : mWager.getChangedRate().entrySet()) {
            CalendarDay key = entry.getKey();
            if(mSelectedMonth == key.getMonth()){
                changedRateDays++;
            }
        }
        return changedRateDays;
    }

    private void showProgressDialog(String message){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.hide();
        }
    }
}
