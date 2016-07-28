package com.rishi.dailywagers.decorators;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Date;

/**
 * Decorator class from library {@link com.prolificinteractive.materialcalendarview.DayViewDecorator}
 * to add color to today.
 * Created by rishi on 6/27/16.
 */
public class TodayDecorator implements DayViewDecorator {

    private CalendarDay mDate;
//    private final Drawable mTodayColor;
//    private static final int mColor = Color.parseColor("#388e3c");

    public TodayDecorator(){
        mDate = CalendarDay.today();
        //mTodayColor = new ColorDrawable(mColor);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return mDate != null && day.equals(mDate);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new RelativeSizeSpan(1.5f));
        //view.setBackgroundDrawable(mTodayColor);
    }

    public void setDate(Date date) {
        mDate = CalendarDay.from(date);
    }
}
