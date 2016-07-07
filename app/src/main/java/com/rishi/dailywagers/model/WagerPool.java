package com.rishi.dailywagers.model;

import android.content.Context;

import com.rishi.dailywagers.data.DailyWagerDbHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rishi on 6/28/16.
 */
public class WagerPool {

    private static final String TAG = "WagerPool";
    private static WagerPool mWagerPool;
    private List<Wager> mWagerList;


    public static WagerPool getInstance(Context context){
        if(mWagerPool ==  null){
            mWagerPool = new WagerPool(context);
        }
        return mWagerPool;
    }

    private WagerPool(Context context){

    }

}
