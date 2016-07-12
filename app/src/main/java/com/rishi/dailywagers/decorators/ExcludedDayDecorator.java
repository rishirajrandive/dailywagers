package com.rishi.dailywagers.decorators;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.rishi.dailywagers.constants.ExcludedDaysOfWeek;

import java.util.Calendar;
import java.util.List;

/**
 * Created by rishi on 6/27/16.
 */
public class ExcludedDayDecorator implements DayViewDecorator {
    private final Drawable mWeekendDrawable;
    private static final int mColor = Color.parseColor("#C8C5E5");
    private List<ExcludedDaysOfWeek> mExcludedDays;

    public ExcludedDayDecorator(List<ExcludedDaysOfWeek> excludedDaysOfWeeks) {
        mExcludedDays = excludedDaysOfWeeks;
        mWeekendDrawable = new ColorDrawable(mColor);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        Calendar calendar = day.getCalendar();
        ExcludedDaysOfWeek excludedDaysOfWeek = ExcludedDaysOfWeek.getValue(calendar.get(Calendar.DAY_OF_WEEK));
        if(mExcludedDays.contains(excludedDaysOfWeek)){
            Log.d("ExcludedDayDecorator",  "Value is true");
            return true;
        }
        return mExcludedDays.contains(day.getDay());
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(mWeekendDrawable);
        view.setDaysDisabled(true);
    }
}
