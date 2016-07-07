package com.rishi.dailywagers.decorators;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.List;

/**
 * Created by rishi on 6/27/16.
 */
public class AbsentDecorator implements DayViewDecorator {

    private final Drawable mAbsentDrawable;
    private static final int mColor = Color.parseColor("#ff3300");
    private List<CalendarDay> mDates;

    public AbsentDecorator(List<CalendarDay> dates){
        mAbsentDrawable = new ColorDrawable(mColor);
        mDates = dates;
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return mDates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(mAbsentDrawable);
    }
}
