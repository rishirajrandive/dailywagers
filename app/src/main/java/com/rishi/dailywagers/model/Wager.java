package com.rishi.dailywagers.model;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.rishi.dailywagers.constants.ExcludedDaysOfWeek;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by rishi on 6/24/16.
 */
public class Wager {
    private UUID id;
    private String name;
    private double dueAmount;
    private double rate;
    private String currency;
    private String startDate;
    private List<ExcludedDaysOfWeek> excludedDaysOfWeeks;
    private Map<CalendarDay, Double> changedRate;
    private List<CalendarDay> absentDates;

    public Wager(){
        id = UUID.randomUUID();
        currency = "Rs";
        excludedDaysOfWeeks = new ArrayList<>();
        changedRate = new HashMap<>();
        absentDates = new ArrayList<>();
        dueAmount = 0;
        rate = 0;
    }

    public String getDisplayStartDate(){
        return "Start date: " + startDate;
    }

    public String getChargeForDay(){
        return "Charge for the day: "+ rate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(double dueAmount) {
        this.dueAmount = dueAmount;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public List<ExcludedDaysOfWeek> getExcludedDaysOfWeeks() {
        return excludedDaysOfWeeks;
    }

    public void setExcludedDaysOfWeeks(List<ExcludedDaysOfWeek> excludedDaysOfWeeks) {
        this.excludedDaysOfWeeks = excludedDaysOfWeeks;
    }

    public Map<CalendarDay, Double> getChangedRate() {
        return changedRate;
    }

    public void setChangedRate(Map<CalendarDay, Double> changedRate) {
        this.changedRate = changedRate;
    }

    public List<CalendarDay> getAbsentDates() {
        return absentDates;
    }

    public void setAbsentDates(List<CalendarDay> absentDates) {
        this.absentDates = absentDates;
    }
}
