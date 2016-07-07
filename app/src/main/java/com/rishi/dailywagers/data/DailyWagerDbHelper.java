package com.rishi.dailywagers.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.rishi.dailywagers.constants.DaysOfWeek;
import com.rishi.dailywagers.constants.WagerConstants;
import com.rishi.dailywagers.data.DailyWagerContract.DailyWagerEntry;
import com.rishi.dailywagers.model.Wager;
import com.rishi.dailywagers.util.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by rishi on 6/26/16.
 */
public class DailyWagerDbHelper extends SQLiteOpenHelper {

    public final static String TAG = "DailyWagerDbHelper";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DailyWager.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DailyWagerEntry.TABLE_NAME + " (" +
                    DailyWagerEntry._ID + " INTEGER PRIMARY KEY," +
                    DailyWagerEntry.COLUMN_NAME_WAGER_ID + TEXT_TYPE + COMMA_SEP +
                    DailyWagerEntry.COLUMN_NAME_WAGER_DATA + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DailyWagerEntry.TABLE_NAME;

    private static DailyWagerDbHelper sDailyWagerDbHelper;

    private List<Wager> mWagerList;
    /**
     * Returns instance for the class
     * @param context
     * @return
     */
    public static DailyWagerDbHelper getInstance(Context context){
        if(sDailyWagerDbHelper == null){
            sDailyWagerDbHelper = new DailyWagerDbHelper(context);
        }
        return sDailyWagerDbHelper;
    }

    /**
     * Constructor for class
     * @param context
     */
    private DailyWagerDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e(TAG, "Deleting and creating table");
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean saveData(Wager wager){
        try{
            JSONObject jsonObject = createJSON(wager);
            Log.d(TAG, "Inserting values");
            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DailyWagerEntry.COLUMN_NAME_WAGER_ID, wager.getId().toString());
            values.put(DailyWagerEntry.COLUMN_NAME_WAGER_DATA, jsonObject.toString());

            long newRowId;
            newRowId = db.insert(
                    DailyWagerEntry.TABLE_NAME,
                    null,
                    values);

            Log.d(TAG, "New row created "+ newRowId);
            db.close();
        }catch (JSONException ex){
            Log.e(TAG, "Error creating JSON");
            return false;
        }catch (Exception ex){
            Log.e(TAG, "Error saving data");
            return false;
        }
        return true;
    }

    public boolean updateData(Wager wager){
        try{
            JSONObject jsonObject = createJSON(wager);
            Log.d(TAG, "Updating values");
            SQLiteDatabase db = getReadableDatabase();

            // New value for one column
            ContentValues values = new ContentValues();
            values.put(DailyWagerEntry.COLUMN_NAME_WAGER_DATA, jsonObject.toString());

            // Which row to update, based on the ID
            String selection = DailyWagerEntry.COLUMN_NAME_WAGER_ID + " LIKE ?";
            String[] selectionArgs = { String.valueOf(wager.getId()) };

            int count = db.update(
                    DailyWagerEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
            Log.d(TAG, "Row updated "+ count);
            db.close();
        }catch (JSONException ex){
            Log.e(TAG, "Error creating JSON");
            return false;
        }catch (Exception ex){
            Log.e(TAG, "Error updating DB");
            return false;
        }
        return true;
    }

    public List<Wager> fetchWagers(){
        Log.d(TAG, "Fetching all the favorites");
        mWagerList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ DailyWagerEntry.TABLE_NAME, null);
        Log.d(TAG, "Total favorite entries " + cursor.getCount());
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            try{
                JSONObject jsonObject = new JSONObject(cursor.getString(cursor.getColumnIndex(DailyWagerEntry.COLUMN_NAME_WAGER_DATA)));
                Wager wager = new Wager();
                Log.d(TAG, "Wager id "+ jsonObject.getString(WagerConstants.ID));
                wager.setId(UUID.fromString(jsonObject.getString(WagerConstants.ID)));
                wager.setName(jsonObject.getString(WagerConstants.NAME));
                wager.setStartDate(jsonObject.getString(WagerConstants.START_DATE));
                wager.setRate(Double.parseDouble(jsonObject.getString(WagerConstants.ORIGINAL_RATE)));

                JSONArray daysOfWeekArr = jsonObject.getJSONArray(WagerConstants.DAYS_OF_WEEK);
                if(daysOfWeekArr.length() > 0){
                    List<DaysOfWeek> daysOfWeek = new ArrayList<>();
                    for(int j=0; j<daysOfWeekArr.length(); j++){
                        daysOfWeek.add(DaysOfWeek.valueOf((daysOfWeekArr.getString(j)).toUpperCase()));
                    }
                    wager.setDaysOfWeek(daysOfWeek);
                }

                JSONArray absentDatesArr = jsonObject.getJSONArray(WagerConstants.ABSENT_DATES);
                if(absentDatesArr.length() > 0){
                    List<CalendarDay> absentDates = new ArrayList<>();
                    for(int j=0; j<absentDatesArr.length(); j++){
                        absentDates.add(DateUtil.getCalendarDay(absentDatesArr.getString(j)));
                    }
                    wager.setAbsentDates(absentDates);
                }

                Iterator<?> keys = jsonObject.getJSONObject(WagerConstants.CHANGED_RATE).keys();
                JSONObject changedRateJSON = jsonObject.getJSONObject(WagerConstants.CHANGED_RATE);

                Map<CalendarDay, Double> changedRates = new HashMap<>();
                while(keys.hasNext()){
                    String key = (String) keys.next();
                    CalendarDay date = DateUtil.getCalendarDay(key);
                    if(date != null){
                        try {
                            changedRates.put(date, changedRateJSON.getDouble(key));
                        } catch (JSONException e) {
                            Log.d(TAG, "Error getting value of rate " + e.getStackTrace());
                        }
                    }
                }

                wager.setChangedRate(changedRates);

                mWagerList.add(wager);
            }catch (JSONException ex){
                Log.e(TAG, "JSONException for getting data from DB");
            }

        }
        cursor.close();
        return mWagerList;
    }

    public Wager getCurrentWager(UUID wagerId){
        for(Wager wager: mWagerList){
            if(wager.getId().equals(wagerId)){
                return wager;
            }
        }
        return null;
    }

    private JSONObject createJSON(Wager wager) throws JSONException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(WagerConstants.ID, wager.getId());
        jsonObject.put(WagerConstants.NAME, wager.getName());
        jsonObject.put(WagerConstants.ORIGINAL_RATE, wager.getRate());
        jsonObject.put(WagerConstants.START_DATE, wager.getStartDate());
        jsonObject.put(WagerConstants.CURRENCY, wager.getCurrency());
        JSONArray daysOfWeek = new JSONArray();
        for(DaysOfWeek day : wager.getDaysOfWeek()){
            daysOfWeek.put(day.getDay());
        }
        jsonObject.put(WagerConstants.DAYS_OF_WEEK, daysOfWeek);

        JSONObject changedRateJSON = new JSONObject();
        for (Map.Entry<CalendarDay, Double> entry : wager.getChangedRate().entrySet()) {
            changedRateJSON.put(DateUtil.getDisplayDate(entry.getKey()), entry.getValue());
        }
        jsonObject.put(WagerConstants.CHANGED_RATE, changedRateJSON);

        JSONArray absentDates = new JSONArray();
        for(int i=0; i<wager.getAbsentDates().size(); i++){
            absentDates.put(DateUtil.getDisplayDate(wager.getAbsentDates().get(i)));
        }
        jsonObject.put(WagerConstants.ABSENT_DATES, absentDates);

        Log.d(TAG, "Final JSONObject "+ jsonObject.toString());

        return jsonObject;
    }
}
