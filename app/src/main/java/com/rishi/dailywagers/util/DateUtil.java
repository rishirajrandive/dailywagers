package com.rishi.dailywagers.util;

import android.util.Log;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by rishi on 6/30/16.
 */
public class DateUtil {

    private static final String TAG = "DateUtil";
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd MMM yyy");

    public static String getCurrentDisplayDate(){
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();

        return FORMATTER.format(date);
    }

    public static String getDisplayDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, month, day);
        Date date = cal.getTime();

        return FORMATTER.format(date);
    }

    public static String getDisplayDate(CalendarDay calendarDay) {
        Date date = calendarDay.getDate();
        return FORMATTER.format(date);
    }

    public static CalendarDay getCalendarDay(String strDate){
        try {
            Date date = FORMATTER.parse(strDate);
            return CalendarDay.from(date);
        }catch (ParseException ex){
            Log.e(TAG, "Error parsing date "+ ex.getStackTrace());
        }
        return null;
    }

    public static int getMonth(String strDate){
        try {
            Date date = FORMATTER.parse(strDate);
            return CalendarDay.from(date).getMonth();
        }catch (ParseException ex){
            Log.e(TAG, "Error parsing date "+ ex.getStackTrace());
        }
        return 0;
    }

    public static int getDays(CalendarDay start, CalendarDay end){
        Log.d(TAG, "Start date "+start.toString() + " end date is "+ end.toString());
        Date startDate = start.getDate();
        Date endDate = end.getDate();
        long diff = endDate.getTime() - startDate.getTime();
        long deno = 24*60*60*1000;
        Log.d(TAG, "Final days "+ diff/deno);
        return (int)(diff/deno);
    }

    public static int getDaysUsingEndDate(CalendarDay endDate){
        CalendarDay startDate = getCalendarDay(getDisplayDate(endDate.getYear(), endDate.getMonth(), 1));
        return getDays(startDate, endDate);
    }

    public static int getDaysUsingStartDate(CalendarDay startDate){
        CalendarDay endDate = getCalendarDay(getDisplayDate(startDate.getYear(), startDate.getMonth(), startDate.getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH)));
        return getDays(startDate, endDate);
    }

}
