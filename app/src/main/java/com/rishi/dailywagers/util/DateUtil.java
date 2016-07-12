package com.rishi.dailywagers.util;

import android.util.Log;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.rishi.dailywagers.constants.ExcludedDaysOfWeek;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    public static int getDays(CalendarDay start, CalendarDay end, List<ExcludedDaysOfWeek> excludedDaysOfWeek){
        Log.d(TAG, "Start date "+start.toString() + " end date is "+ end.toString());
        Date startDate = start.getDate();
        Date endDate = end.getDate();
        long diff = endDate.getTime() - startDate.getTime();
        long deno = 24*60*60*1000;
        Log.d(TAG, "Final days "+ diff/deno);
        int totalDays = (int)(diff/deno) + 1;
        int excludedDays = 0;

        Calendar calStart = Calendar.getInstance();
        calStart.setTime(startDate);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(endDate);

        while(!calStart.after(calEnd)) {
            for(ExcludedDaysOfWeek day : excludedDaysOfWeek){
                if(calStart.get(Calendar.DAY_OF_WEEK) == day.getDay()) {
                    excludedDays++;
                }
            }
            calStart.add(Calendar.DATE,1);
        }
        Log.d(TAG, "Days to be excluded are "+ excludedDays);
        totalDays = totalDays - excludedDays;
        return totalDays;
    }

    public static int getDaysUsingEndDate(CalendarDay endDate, List<ExcludedDaysOfWeek> excludedDaysOfWeek){
        CalendarDay startDate = getCalendarDay(getDisplayDate(endDate.getYear(), endDate.getMonth(), 1));
        return getDays(startDate, endDate, excludedDaysOfWeek);
    }

    public static int getDaysUsingStartDate(CalendarDay startDate, List<ExcludedDaysOfWeek> excludedDaysOfWeek){
        CalendarDay endDate = getCalendarDay(getDisplayDate(startDate.getYear(), startDate.getMonth(), startDate.getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH)));
        return getDays(startDate, endDate, excludedDaysOfWeek);
    }

    public static int getDaysInMonth(int currentMonth, List<ExcludedDaysOfWeek> excludedDaysOfWeek){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, currentMonth);
        int totalDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int excludedDays = 0;
        for(ExcludedDaysOfWeek day : excludedDaysOfWeek){
            calendar.set(Calendar.DAY_OF_WEEK, day.getDay());
            excludedDays = excludedDays + calendar.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH);
        }
        totalDays = totalDays - excludedDays;
        Log.d(TAG, "After excluding days "+ totalDays);
        return totalDays;
    }

}
