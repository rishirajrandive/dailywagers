package com.rishi.dailywagers;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

public class HomeActivity extends AbstractFragmentActivity implements View.OnClickListener{

    private final static String TAG = "HomeActivity";
    private MaterialCalendarView mCalendarView;
    private FloatingActionButton mAddNew;

    @Override
    protected Fragment createFragment() {
        return new HomeFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //mAddNew = (FloatingActionButton) findViewById(R.id.add_new_wager);

        //mAddNew.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(CheckFragment.TAG);
            if(fragment != null && fragment.isVisible() && !CheckFragment.sDataSaved){
                Log.d(TAG, "Need to save the data");
            }
            getSupportFragmentManager().popBackStack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "Home clicked");
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "Floating action button pressed");
    }
}
