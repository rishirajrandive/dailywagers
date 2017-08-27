package com.rishi.dailywagers;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
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
 * Deals with user/wager details, calendar updates, rate change and shows the amount
 * Created by rishi on 6/24/16.
 */
public class CheckFragment extends Fragment implements OnDateSelectedListener, OnRangeSelectedListener,
        OnMonthChangedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static final String TAG = "CheckFragment";
    public static boolean sDataSaved = false;

    private static final int REQUEST_CONFIRM_OPTION = 0;
    private static final String DIALOG_HELP = "DialogHelp";
    private static final String ARG_WAGER_ID = "wagerId";

    private ProgressDialog mProgressDialog;

    private Wager mWager;
    private CalendarDay mSelectedDay;
    private CalendarDay mToday;
    private int mSelectedMonth;

    private ScrollView mFragmentCheck;
    private List<CalendarDay> mSelectedDateRange;
    private MaterialCalendarView mCalendarView;
    private TextView mAmountLabel;
    private TextView mDueMonthAmount;
    private EditText mRateForTheDay;
    private Button mRateEditStart;
    private Button mRateEditDone;
    private Button mRateEditCancel;
    private CheckBox mDayAttendance;
    private Switch mCustomDates;
    private Button mSaveChanges;

    /**
     * Returns new instance
     * @param wagerId
     * @return
     */
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
        if(mToday == null){
            return;
        }
        mSelectedDay = mToday;
        mSelectedMonth = mToday.getMonth();

        Log.d(TAG, "On create " + mWager.getName());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "On create view");
        View view = inflater.inflate(R.layout.fragment_check, container, false);

        if(((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(mWager.getName());
        }

        getActivity().findViewById(R.id.add_new_wager).setVisibility(View.GONE);
        mFragmentCheck = (ScrollView) view.findViewById(R.id.fragment_check);
        mCalendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        mAmountLabel = (TextView) view.findViewById(R.id.amount_label);
        mDueMonthAmount = (TextView) view.findViewById(R.id.due_amount);
        mRateForTheDay = (EditText) view.findViewById(R.id.edit_rate_of_day);
        mRateEditStart = (Button) view.findViewById(R.id.edit_start);
        mRateEditDone = (Button) view.findViewById(R.id.edit_done);
        mRateEditCancel = (Button) view.findViewById(R.id.edit_cancel);
        mDayAttendance = (CheckBox) view.findViewById(R.id.day_attendance);
        mCustomDates = (Switch) view.findViewById(R.id.date_select_switch);
        mSaveChanges = (Button) view.findViewById(R.id.save_changes);

        mCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        mCalendarView.setOnDateChangedListener(this);
        mCalendarView.setOnRangeSelectedListener(this);
        mCalendarView.setOnMonthChangedListener(this);
        mCalendarView.state().edit()
                .setMinimumDate(DateUtil.getCalendarDay(mWager.getStartDate()))
                .setMaximumDate(mSelectedDay)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        mRateEditStart.setOnClickListener(this);
        mRateEditDone.setOnClickListener(this);
        mRateEditCancel.setOnClickListener(this);
        mSaveChanges.setOnClickListener(this);
        mDayAttendance.setOnClickListener(this);

        mCustomDates.setOnCheckedChangeListener(this);

        mCalendarView.addDecorators(new TodayDecorator(), new WeekendDecorator(),
                new AbsentDecorator(mWager.getAbsentDates()), new ExcludedDayDecorator(mWager.getExcludedDaysOfWeeks()));
        //mCalendarView.invalidateDecorators();

        updateAmount();
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
        getActivity().findViewById(R.id.add_new_wager).setVisibility(View.GONE);
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
//            case R.id.help_button:
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                HelpDialog dialog = HelpDialog.newInstance();
//                dialog.setTargetFragment(CheckFragment.this, REQUEST_CONFIRM_OPTION);
//
//                dialog.show(fragmentManager, DIALOG_HELP);
//                return true;
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
        toggleRateEditStart(mWager.getAbsentDates().indexOf(mSelectedDay) == -1);
    }

    @Override
    public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {
        Log.d(TAG, "Date range selected "+ dates.size());
        //TODO Show amount for the selected dates
        mSelectedDateRange = dates;
        updateAmountForDateRange();
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        Log.d(TAG, "Month changed "+ date.toString() + " " + date.getMonth());
        //TODO Reset the amount shown to current month total
        Calendar cal = date.getCalendar();
        mSelectedMonth = date.getMonth();
        updateAmount();
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
                            updateAmount();
                            toggleSaveButton(false);
                            //Toast.makeText(getActivity(), "Data updated successfully", Toast.LENGTH_SHORT).show();
                            Snackbar.make(mFragmentCheck, "Changes saved successfully", Snackbar.LENGTH_LONG)
                                    .show();
                        }else {
                            //Toast.makeText(getActivity(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                            Snackbar.make(mFragmentCheck, "Oops! something went wrong", Snackbar.LENGTH_LONG)
                                    .show();
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

    /**
     * Checks and updates the amount
     */
    private void updateAmount(){
        if(mCustomDates.isChecked()){
            updateAmountForDateRange();
        }else {
            updateAmountForMonth();
        }
    }
    /**
     * Gets the total amount for the month and updates it
     */
    private void updateAmountForMonth(){
        Log.d(TAG, "Updating amount for month");
        int totalDays;
        CalendarDay profileStartDate = DateUtil.getCalendarDay(mWager.getStartDate());
        if(mToday.getMonth() == mSelectedMonth && profileStartDate != null && profileStartDate.getMonth() == mSelectedMonth){
            totalDays = DateUtil.getDays(profileStartDate, mToday, mWager.getExcludedDaysOfWeeks());
        }else if(mToday.getMonth() == mSelectedMonth){
            totalDays = DateUtil.getDays(DateUtil.getMonthStartDate(mToday), mToday, mWager.getExcludedDaysOfWeeks());
        }else if(profileStartDate != null && mSelectedMonth == profileStartDate.getMonth()){
            totalDays = DateUtil.getDays(profileStartDate, DateUtil.getMonthEndDate(profileStartDate), mWager.getExcludedDaysOfWeeks());
        }else {
            totalDays = DateUtil.getDaysInMonth(mSelectedMonth, mWager.getExcludedDaysOfWeeks());
        }
        Log.d(TAG, "Total days "+ totalDays);
        totalDays = totalDays - (getAbsentDays() + getChangedRateDays());
        double totalAmount = (totalDays * mWager.getRate()) + getChangedRateTotal();
        mDueMonthAmount.setText(totalAmount + "");
    }

    /**
     * Updates amount for selected date range
     */
    private void updateAmountForDateRange(){
        int totalDays = 0;
        double totalAmount = 0.0;

        if(mSelectedDateRange != null && mSelectedDateRange.size() > 0){
            CalendarDay startDate = mSelectedDateRange.get(0);
            CalendarDay endDate = mSelectedDateRange.get(mSelectedDateRange.size() - 1);
            totalDays = DateUtil.getDays(startDate, endDate, mWager.getExcludedDaysOfWeeks());

            totalDays = totalDays - (getAbsentDaysForRange() + getChangedRateDaysForRange());
            totalAmount = (totalDays * mWager.getRate()) + getChangedRateTotalForRange();
        }
        mDueMonthAmount.setText(totalAmount+"");
    }

    /**
     * Updates the absent dates in the calendar.
     */
    private void updateAbsentDates(){
        Log.d(TAG, "Get absent dates");
        if(mCustomDates.isChecked()){
            List<CalendarDay> absentDates = mWager.getAbsentDates();
            if(mSelectedDateRange != null){
                for(CalendarDay date: mSelectedDateRange){
                    if(absentDates.contains(date)){
                        absentDates.remove(absentDates.indexOf(date));
                    }
                }
                absentDates.addAll(mSelectedDateRange);
            }else {
                Toast.makeText(getActivity(), "You need to select dates first!", Toast.LENGTH_SHORT).show();
            }
            mWager.setAbsentDates(absentDates);
        }else {
            List<CalendarDay> absentDates = mWager.getAbsentDates();
            if(mDayAttendance.isChecked() && absentDates.indexOf(mSelectedDay) != -1){
                absentDates.remove(absentDates.indexOf(mSelectedDay));
            }else if(absentDates.indexOf(mSelectedDay) == -1){
                absentDates.add(mSelectedDay);
            }
            mWager.setAbsentDates(absentDates);
        }
    }

    /**
     * Returns the changed rate total
     * @return
     */
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

    /**
     * Returns the changed rate total
     * @return
     */
    private double getChangedRateTotalForRange(){
        Log.d(TAG, "Get changed rate");
        Map<CalendarDay, Double> changedRate = mWager.getChangedRate();
        double changedRateTotal = 0;
        for (Map.Entry<CalendarDay, Double> entry : changedRate.entrySet()) {
            CalendarDay key = entry.getKey();
            if(mSelectedDateRange.contains(key)){
                changedRateTotal = changedRateTotal + entry.getValue();
            }
        }
        return changedRateTotal;
    }

    /**
     * Toggles mode for calendar
     */
    private void toggleMode(){
        if(mCustomDates.isChecked()){
            mAmountLabel.setText(R.string.amount_for_date_range);
            mDueMonthAmount.setText(R.string.dummy_value);
            mCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);

            toggleRateEditStart(false);
            Toast.makeText(getContext(), "Select your date range", Toast.LENGTH_LONG).show();

        }else {
            mAmountLabel.setText(R.string.amount_for_month_label);
            updateAmountForMonth();
            mCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
            mCalendarView.setSelectedDate(mSelectedDay);
            toggleRateEditStart(true);
        }
        toggleSaveButton(false);
    }

    private void toggleRateEditStart(boolean isShow){
        if(isShow){
            mRateEditStart.setVisibility(View.VISIBLE);
        }else {
            mRateEditStart.setVisibility(View.GONE);
        }
        mRateEditDone.setVisibility(View.GONE);
        mRateEditCancel.setVisibility(View.GONE);
    }

    /**
     * Enables the charge for the day edit
     * @param isShow
     */
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

    /**
     * Updates data for day based on calendar selection
     */
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

    /**
     * Toggles save button show/hide
     * @param isShow
     */
    private void toggleSaveButton(boolean isShow) {
        if(isShow){
            mSaveChanges.setVisibility(View.VISIBLE);
        }else {
            mSaveChanges.setVisibility(View.GONE);
        }
    }

    /**
     * Returns the absent days
     * @return
     */
    private int getAbsentDays(){
        int absentDays = 0;
        for(CalendarDay day : mWager.getAbsentDates()){
            if(day.getMonth() == mSelectedMonth){
                absentDays++;
            }
        }
        return absentDays;
    }

    /**
     * Returns the changed rate days
     * @return
     */
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

    /**
     * Returns the absent days
     * @return
     */
    private int getAbsentDaysForRange(){
        int absentDays = 0;
        for(CalendarDay day : mWager.getAbsentDates()){
            if(mSelectedDateRange.contains(day)){
                absentDays++;
            }
        }
        return absentDays;
    }

    /**
     * Returns the changed rate days
     * @return
     */
    private int getChangedRateDaysForRange(){
        int changedRateDays = 0;
        for (Map.Entry<CalendarDay, Double> entry : mWager.getChangedRate().entrySet()) {
            CalendarDay key = entry.getKey();
            if(mSelectedDateRange.contains(key)){
                changedRateDays++;
            }
        }
        return changedRateDays;
    }

    /**
     * Shows the progress dialog
     * @param message
     */
    private void showProgressDialog(String message){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    /**
     * Hides progress dialog
     */
    private void hideProgressDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.hide();
        }
    }
}
