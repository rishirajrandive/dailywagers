package com.rishi.dailywagers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by rishi on 6/24/16.
 */
public abstract class AbstractFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_layout);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_layout, fragment, HomeFragment.TAG)
                    .commit();
        }
    }

    public static void updateFragment(Fragment fragment, String fragmentTag, FragmentManager supportFragmentManager){
        if (fragment != null) {
            FragmentManager fragmentManager = supportFragmentManager;
            if(fragmentManager.findFragmentByTag(fragmentTag) == null){
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_layout, fragment, fragmentTag).addToBackStack(fragmentTag).commit();
            }else {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_layout, fragment, fragmentTag).commit();
            }
        }
    }


}
