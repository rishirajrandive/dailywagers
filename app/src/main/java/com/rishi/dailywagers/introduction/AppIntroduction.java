package com.rishi.dailywagers.introduction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.rishi.dailywagers.HomeActivity;
import com.rishi.dailywagers.R;

/**
 * App introduction slides added using library {@link com.github.paolorotolo.appintro.AppIntro}
 * Created by rishi on 7/25/16.
 */
public class AppIntroduction extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(IntroSlide.newInstance(R.layout.zero_slide));
        addSlide(IntroSlide.newInstance(R.layout.first_slide));
        addSlide(IntroSlide.newInstance(R.layout.second_slide));
        addSlide(IntroSlide.newInstance(R.layout.third_slide));
        addSlide(IntroSlide.newInstance(R.layout.fourth_slide));
    }

    private void loadMainActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        loadMainActivity();
        Toast.makeText(getApplicationContext(), "See it later from home page", Toast.LENGTH_SHORT).show();
    }

    public void getStarted(View v){
        loadMainActivity();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        loadMainActivity();
        updateIntroDone();
    }

    /**
     * Updates value in SharedPreference to True when introduction is done
     */
    private void updateIntroDone() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preferences.edit().putBoolean(HomeActivity.SLIDE_SHOW, true).apply();
    }
}
