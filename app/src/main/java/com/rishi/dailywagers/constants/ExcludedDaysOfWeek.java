package com.rishi.dailywagers.constants;

/**
 * Created by rishi on 6/28/16.
 */
public enum ExcludedDaysOfWeek {
    SUN("S", 1),
    MON("M", 2),
    TUE("T", 3),
    WED("W", 4),
    THU("T",5),
    FRI("F",6),
    SAT("S",7);

    private int day;
    private String label;

    ExcludedDaysOfWeek(String label, int day) {
        this.label = label;
        this.day = day;
    }

    public int getDay() {
        return day;
    }

    public String getLabel(){
        return label;
    }

    public static ExcludedDaysOfWeek getValue(int day){
        for(ExcludedDaysOfWeek dayOfWeek : values()){
            if(dayOfWeek.getDay() == day){
                return dayOfWeek;
            }
        }
        return null;
    }
}
