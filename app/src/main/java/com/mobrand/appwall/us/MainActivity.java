package com.mobrand.appwall.us;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mobrand.appwall.classic.AppWall;


public class MainActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.layout);

        View viewById = findViewById(R.id.openAppwall);


        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppWall.start(v.getContext(), "App Wall");
            }
        });

    }


}