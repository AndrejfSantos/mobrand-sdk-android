package com.mobrand.appwall.us;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mobrand.appwall.classic.AppWall;
import com.mobrand.appwall.classic.AppwallFactory;
import com.mobrand.appwall.event.MobrandLifecycle;


public class MainActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.layout);

        View viewById = findViewById(R.id.openAppwall);

        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppwallFactory.createAppwall(v.getContext(), "App Wall").setLifecycle(new MobrandLifecycle() {
                    @Override
                    public void onLifecycleEvent(Event event) {
                        System.out.println("OnEventOuter");
                    }
                }).start();
            }
        });

    }


}