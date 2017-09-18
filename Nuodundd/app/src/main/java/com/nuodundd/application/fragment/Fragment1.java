package com.nuodundd.application.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nuodundd.R;
import com.nuodundd.viewstack.Fragment;

/**
 * Created by nuodundd on 2017/9/18.
 */

public class Fragment1 extends Fragment {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fragment);
        getView().setBackgroundColor(Color.RED);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewFragment(Fragment2.class);
            }
        });
        button.setText(getClass().getSimpleName());
    }


}
