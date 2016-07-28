package com.rishi.dailywagers.model;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.rishi.dailywagers.constants.ExcludedDaysOfWeek;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Holds all the details for wager object, with relevant methods.
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

    /**
     * Constructor to define new Wager object
     */
    public Wager(){
        id = UUID.randomUUID();
        currency = "Rs";
        excludedDaysOfWeeks = new ArrayList<>();
        changedRate = new HashMap<>();
        absentDates = new ArrayList<>();
        dueAmount = 0;
        rate = 0;
    }

    /**
     * Returns start date for display purpose
     * @return
     */
    public String getDisplayStartDate(){
        return "Start date: " + startDate;
    }

    /**
     * Returns charge for the day for display purpose
     * @return
     */
    public String getChargeForDay(){
        return "Charge for the day: "+ rate;
    }

    /**
     * Returns Id for the wager
     * @return
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the Id for wager
     * @param id
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Returns name for the Wager
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name for the wager
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns amount due for the month/range of days
     * @return
     */
    public double getDueAmount() {
        return dueAmount;
    }

    /**
     * Sets amount due for the month/range of days
     * @param dueAmount
     */
    public void setDueAmount(double dueAmount) {
        this.dueAmount = dueAmount;
    }

    /**
     * Returns the charge for the day
     * @return
     */
    public double getRate() {
        return rate;
    }

    /**
     * Sets charge for the day
     * @param rate
     */
    public void setRate(double rate) {
        this.rate = rate;
    }

    /**
     * Returns the currency used
     * @return
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency used
     * @param currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Returns the start date for wager
     * @return
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date for wager
     * @param startDate
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * Returns the excluded days of week
     * @return
     */
    public List<ExcludedDaysOfWeek> getExcludedDaysOfWeeks() {
        return excludedDaysOfWeeks;
    }

    /**
     * Sets the excluded days of week
     * @param excludedDaysOfWeeks
     */
    public void setExcludedDaysOfWeeks(List<ExcludedDaysOfWeek> excludedDaysOfWeeks) {
        this.excludedDaysOfWeeks = excludedDaysOfWeeks;
    }

    /**
     * Returns the changed rate
     * @return
     */
    public Map<CalendarDay, Double> getChangedRate() {
        return changedRate;
    }

    /**
     * Sets the changed rates
     * @param changedRate
     */
    public void setChangedRate(Map<CalendarDay, Double> changedRate) {
        this.changedRate = changedRate;
    }

    /**
     * Returns absent dates
     * @return
     */
    public List<CalendarDay> getAbsentDates() {
        return absentDates;
    }

    /**
     * Sets the absent dates
     * @param absentDates
     */
    public void setAbsentDates(List<CalendarDay> absentDates) {
        this.absentDates = absentDates;
    }
}
