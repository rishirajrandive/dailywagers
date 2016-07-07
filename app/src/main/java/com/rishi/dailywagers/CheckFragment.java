package com.rishi.dailywagers;

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


    private Wager mWager;
    private List<CalendarDay> mAbsentDates;
    private Map<CalendarDay, Double> mChangedRates;
    private CalendarDay mSelectedDay;
    private CalendarDay mToday;
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
        mAbsentDates = mWager.getAbsentDates();
        mChangedRates = mWager.getChangedRate();

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

        mCalendarView.addDecorators(new TodayDecorator(), new WeekendDecorator(), new AbsentDecorator(mAbsentDates));
        //mCalendarView.invalidateDecorators();

        updateAmountForMonth(mToday.getMonth());
        updateDataForDay();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(TAG, "Menu creation");
        inflater.inflate(R.menu.fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_amount:
                Fragment fragment = new UpdateFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if(fragmentManager.findFragmentByTag(UpdateFragment.TAG) == null){
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_layout, fragment, UpdateFragment.TAG).addToBackStack(UpdateFragment.TAG).commit();
                }else {
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_layout, fragment, UpdateFragment.TAG).commit();
                }
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
        Log.d(TAG, "Days in month "+ cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        updateAmountForMonth(date.getMonth());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_done:
                Double newRate = Double.parseDouble(mRateForTheDay.getText().toString());
                mChangedRates.put(mSelectedDay, newRate);
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
                if(mDayAttendance.isChecked() && mAbsentDates.indexOf(mSelectedDay) != -1){
                    mAbsentDates.remove(mAbsentDates.indexOf(mSelectedDay));
                }else if(mAbsentDates.indexOf(mSelectedDay) == -1){
                    mAbsentDates.add(mSelectedDay);
                }
                break;
            case R.id.range_attendance:
                toggleSaveButton(true);
                updateAbsentDates();
                break;
            case R.id.save_changes:
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        return DailyWagerDbHelper.getInstance(getContext()).updateData(mWager);
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        super.onPostExecute(aBoolean);
                        if(aBoolean){
                            sDataSaved = true;
                            mCalendarView.invalidateDecorators();
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

    private void updateAmountForMonth(int currentMonth){
        int totalDays = 0;
        if(mToday.getMonth() == currentMonth){
            totalDays = DateUtil.getDaysUsingEndDate(mToday);
        }else if(currentMonth == DateUtil.getCalendarDay(mWager.getStartDate()).getMonth()){
            totalDays = DateUtil.getDaysUsingStartDate(DateUtil.getCalendarDay(mWager.getStartDate()));
        }else {
            totalDays = mSelectedDay.getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        totalDays = totalDays - (getAbsentDays(currentMonth) + getChangedRateDays(currentMonth));
        double totalAmount = (totalDays * mWager.getRate()) + getChangedRateTotal(currentMonth);
        mDueMonthAmount.setText(totalAmount + "");
    }

    private void updateAbsentDates(){
        if(mSelectedDateRange != null){
            for(CalendarDay date: mSelectedDateRange){
                if(mAbsentDates.contains(date)){
                    mAbsentDates.remove(mAbsentDates.indexOf(date));
                }
            }
            mAbsentDates.addAll(mSelectedDateRange);
        }else {
            Toast.makeText(getActivity(), "Select range of dates", Toast.LENGTH_SHORT).show();
        }

    }

    private double getChangedRateTotal(int month){
        double changedRateTotal = 0;
        for (Map.Entry<CalendarDay, Double> entry : mChangedRates.entrySet()) {
            CalendarDay key = entry.getKey();
            if(month == key.getMonth()){
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
        if(mAbsentDates.indexOf(mSelectedDay) != -1){
            mDayAttendance.setChecked(false);
        }else{
            mDayAttendance.setChecked(true);
        }

        if(mChangedRates.containsKey(mSelectedDay)){
            mRateForTheDay.setText(mChangedRates.get(mSelectedDay).toString());
        }
    }

    private void toggleSaveButton(boolean isShow) {
        if(isShow){
            mSaveChanges.setVisibility(View.VISIBLE);
        }else {
            mSaveChanges.setVisibility(View.GONE);
        }
    }

    private int getAbsentDays(int month){
        int absentDays = 0;
        for(CalendarDay day : mAbsentDates){
            if(day.getMonth() == month){
                absentDays++;
            }
        }
        return absentDays;
    }

    private int getChangedRateDays(int month){
        int changedRateDays = 0;
        for (Map.Entry<CalendarDay, Double> entry : mChangedRates.entrySet()) {
            CalendarDay key = entry.getKey();
            if(month == key.getMonth()){
                changedRateDays++;
            }
        }
        return changedRateDays;
    }
}
