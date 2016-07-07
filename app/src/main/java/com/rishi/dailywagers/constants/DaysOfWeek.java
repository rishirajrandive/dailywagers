package com.rishi.dailywagers.constants;

/**
 * Created by rishi on 6/28/16.
 */
public enum DaysOfWeek {
    SUN("sun"),
    MON("mon"),
    TUE("tue"),
    WED("wed"),
    THU("thu"),
    FRI("fri"),
    SAT("sat");

    private String day;

    DaysOfWeek(String day){
        this.day = day;
    }

    public String getDay(){
        return day;
    }
}
