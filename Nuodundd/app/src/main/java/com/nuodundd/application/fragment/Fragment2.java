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

public class Fragment2 extends Fragment{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fragment);
        getView().setBackgroundColor(Color.YELLOW);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startNewFragment(intent,Fragment3.class);
            }
        });
        button.setText(getClass().getSimpleName());
    }
}
