package com.rishi.dailywagers;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.rishi.dailywagers.model.Wager;

import java.util.List;

/**
 * Adapter to hold the list of wagers
 * Created by rishi on 6/26/16.
 */
public class WagerAdapter extends RecyclerView.Adapter<WagerHolder> {

    private Activity mActivity;
    private FragmentManager mFragmentManager;
    private List<Wager> mWagerList;
    private CalendarDay mCurrentDate;

    /**
     * Constructor with relevant objects
     * @param wagerList
     * @param activity
     * @param fragmentManager
     * @param currentDate
     */
    public WagerAdapter(List<Wager> wagerList, Activity activity, FragmentManager fragmentManager, CalendarDay currentDate){
        mWagerList = wagerList;
        mActivity = activity;
        mFragmentManager = fragmentManager;
        mCurrentDate = currentDate;
    }

    @Override
    public WagerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
        View view = layoutInflater.inflate(R.layout.wager_display, parent, false);
        return new WagerHolder(view, mActivity, mFragmentManager, mCurrentDate);
    }

    @Override
    public void onBindViewHolder(WagerHolder holder, int position) {
        Wager wager = mWagerList.get(position);
        holder.bindWager(wager);
    }

    @Override
    public int getItemCount() {
        if(mWagerList == null){
            return 0;
        }
        return mWagerList.size();
    }
}
