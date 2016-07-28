package com.rishi.dailywagers;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.CorrectionInfo;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.rishi.dailywagers.data.DailyWagerDbHelper;
import com.rishi.dailywagers.model.Wager;
import com.rishi.dailywagers.util.DateUtil;

import java.util.List;

/**
 * Holder for the wager
 * Created by rishi on 6/26/16.
 */
public class WagerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Activity mActivity;
    private FragmentManager mFragmentManager;
    private TextView mWagerName;
    private TextView mChargeForDay;
    private TextView mStartDate;
    private CheckBox mCurrentStatus;
    private CalendarDay mCurrentDate;

    private Wager mWager;

    public WagerHolder(View itemView, Activity activity, FragmentManager fragmentManager, CalendarDay currentDate) {
        super(itemView);

        itemView.setOnClickListener(this);

        mActivity = activity;
        mFragmentManager = fragmentManager;
        mWagerName = (TextView) itemView.findViewById(R.id.wager_name);
        mChargeForDay = (TextView) itemView.findViewById(R.id.charge_for_day);
        mStartDate = (TextView) itemView.findViewById(R.id.display_start_date);
        mCurrentStatus = (CheckBox) itemView.findViewById(R.id.current_status);
        mCurrentDate = currentDate;

        mCurrentStatus.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.current_status){
            List<CalendarDay> absentDate = mWager.getAbsentDates();
            if(mCurrentStatus.isChecked() && absentDate.indexOf(mCurrentDate) != -1){
                absentDate.remove(absentDate.indexOf(mCurrentDate));
            }else if(absentDate.indexOf(mCurrentDate) == -1){
                absentDate.add(mCurrentDate);
            }
            mWager.setAbsentDates(absentDate);
            new UpdateAttendance().execute();
        }else {
            Fragment fragment =  CheckFragment.newInstance(mWager.getId());
            if (fragment != null) {
                if(mFragmentManager.findFragmentByTag(CheckFragment.TAG) == null){
                    mFragmentManager.beginTransaction()
                            .replace(R.id.fragment_layout, fragment, CheckFragment.TAG).addToBackStack(CheckFragment.TAG).commit();
                }else {
                    mFragmentManager.beginTransaction()
                            .replace(R.id.fragment_layout, fragment, CheckFragment.TAG).commit();
                }
            }
        }
    }

    public void bindWager(Wager wager){
        mWager = wager;
        mWagerName.setText(wager.getName());
        mStartDate.setText(wager.getDisplayStartDate());
        mChargeForDay.setText(wager.getChargeForDay());
        List<CalendarDay> absentDates = wager.getAbsentDates();
        if(absentDates.contains(mCurrentDate)){
            mCurrentStatus.setChecked(false);
        }else {
            mCurrentStatus.setChecked(true);
        }
    }

    private class UpdateAttendance extends AsyncTask<Void, Void, Boolean>{
        @Override
        protected Boolean doInBackground(Void... params) {
            return DailyWagerDbHelper.getInstance(mActivity).saveWager(mWager);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) mActivity.findViewById(R.id.coordinatelayout);
            if(aBoolean){
                Snackbar.make(coordinatorLayout, "Attendance updated", Snackbar.LENGTH_LONG)
                        .show();
            }else {
                Snackbar.make(coordinatorLayout, "Oops! something went wrong", Snackbar.LENGTH_LONG)
                        .show();
            }
        }
    }
}
