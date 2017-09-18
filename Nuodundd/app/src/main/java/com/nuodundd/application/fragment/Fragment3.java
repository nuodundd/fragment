package com.nuodundd.application.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nuodundd.R;
import com.nuodundd.viewstack.Fragment;

/**
 * Created by nuodundd on 2017/9/18.
 */

public class Fragment3 extends Fragment {
    private static final String KEY_INDEX = "KEY_INDEX";
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fragment);
        getView().setBackgroundColor(Color.GREEN);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(KEY_INDEX, index);
                startNewFragment(intent, Fragment3.class);
            }
        });
        index = getIntent().getIntExtra(KEY_INDEX, 0);
        index++;
        button.setText(getClass().getSimpleName() + " - " + index);
    }
}
