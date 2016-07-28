package com.rishi.dailywagers.decorators;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

/**
 * Decorator class from library {@link com.prolificinteractive.materialcalendarview.DayViewDecorator}
 * to add color to weekends.
 * Created by rishi on 6/27/16.
 */
public class WeekendDecorator implements DayViewDecorator {

    private final Calendar mCalendar = Calendar.getInstance();
    private final Drawable mWeekendDrawable;
    private static final int mColor = Color.parseColor("#228BC34A");

    public WeekendDecorator() {
        mWeekendDrawable = new ColorDrawable(mColor);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        day.copyTo(mCalendar);
        int weekDay = mCalendar.get(Calendar.DAY_OF_WEEK);
        return weekDay == Calendar.SATURDAY || weekDay == Calendar.SUNDAY;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(mWeekendDrawable);
    }
}
