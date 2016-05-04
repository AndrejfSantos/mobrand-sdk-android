package com.mobrand.appwall.us;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


import com.mobrand.appwall.classic.AppWall;
import com.mobrand.appwall.classic.AppwallFactory;
import com.mobrand.sdk.core.event.MobrandLifecycle;
import com.mobrand.simplyred.MobrandSimplyRedInterstitial;


public class MainActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout);

        findViewById(R.id.openAppwall)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppWall.start(v.getContext(), "App Wall");
                    }
                });

        findViewById(R.id.openInterstitial)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobrandSimplyRedInterstitial.start(v.getContext(), "App Wall");
                    }
                });


    }


}