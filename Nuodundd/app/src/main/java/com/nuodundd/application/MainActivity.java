package com.nuodundd.application;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.nuodundd.R;
import com.nuodundd.application.fragment.Fragment1;
import com.nuodundd.viewstack.FragmentManager;

/**
 * Created by nuodundd on 2017/9/18.
 */

public class MainActivity extends Activity {
    private FragmentManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = new FragmentManager(this, R.id.content, savedInstanceState);
        manager.startNewFragment(Fragment1.class);
    }

    @Override
    public void onBackPressed() {
        if (!manager.onBackKey()) {
            moveTaskToBack(true);
        }
    }
}
