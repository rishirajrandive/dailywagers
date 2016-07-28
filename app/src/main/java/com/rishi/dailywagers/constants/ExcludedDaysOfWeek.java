package com.rishi.dailywagers.constants;

/**
 * Enum to maintaing the days of week.
 * Created by rishi on 6/28/16.
 */
public enum ExcludedDaysOfWeek {
    SUN("S", 1),
    MON("M", 2),
    TUE("T", 3),
    WED("W", 4),
    THU("T", 5),
    FRI("F", 6),
    SAT("S", 7);

    private int day;
    private String label;

    /**
     * Constructor to initialize Enum
     * @param label
     * @param day
     */
    ExcludedDaysOfWeek(String label, int day) {
        this.label = label;
        this.day = day;
    }

    /**
     * Function to get day
     * @return
     */
    public int getDay() {
        return day;
    }

    /**
     * To get the label, not used yet.
     * @return
     */
    public String getLabel(){
        return label;
    }

    /**
     * Returns the enum value for day int value.
     * @param day
     * @return
     */
    public static ExcludedDaysOfWeek getValue(int day){
        for(ExcludedDaysOfWeek dayOfWeek : values()){
            if(dayOfWeek.getDay() == day){
                return dayOfWeek;
            }
        }
        return null;
    }
}
