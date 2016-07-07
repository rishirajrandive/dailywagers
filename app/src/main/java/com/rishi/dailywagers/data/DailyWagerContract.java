package com.rishi.dailywagers.data;

import android.provider.BaseColumns;

/**
 * Created by rishi on 6/26/16.
 */
public class DailyWagerContract {

    public DailyWagerContract(){}

    public static abstract class DailyWagerEntry implements BaseColumns{
        public static final String TABLE_NAME = "dailywager";
        public static final String COLUMN_NAME_WAGER_ID = "wager_id";
        public static final String COLUMN_NAME_WAGER_DATA = "wager_data";
    }
}
