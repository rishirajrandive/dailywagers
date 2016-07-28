package com.rishi.dailywagers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.rishi.dailywagers.introduction.AppIntroduction;

/**
 * Home page for the app
 */
public class HomeActivity extends AbstractFragmentActivity implements View.OnClickListener{

    private final static String TAG = "HomeActivity";
    public final static String SLIDE_SHOW = "slide_show";
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
        mAddNew = (FloatingActionButton) findViewById(R.id.add_new_wager);
        mAddNew.setVisibility(View.VISIBLE);
        mAddNew.setOnClickListener(this);

        if(!isIntroDone()){
            startIntroduction();
        }
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
        mAddNew.setVisibility(View.VISIBLE);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_home_menu, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "Home clicked");
                onBackPressed();
                return true;
            case R.id.slide_show:
                Log.d(TAG, "Slide show requested");
                startIntroduction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "Floating action button pressed");
        mAddNew.setVisibility(View.GONE);
        AbstractFragmentActivity.updateFragment(new ProfileFragment(), ProfileFragment.TAG, getSupportFragmentManager());
    }

    /**
     * Starts the introduction screens
     */
    private void startIntroduction(){
        Intent intent = new Intent(this, AppIntroduction.class);
        startActivity(intent);
        finish();
    }

    /**
     * Checks if introduction done
     * @return
     */
    private boolean isIntroDone(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return preferences.getBoolean(SLIDE_SHOW, false);
    }
}
