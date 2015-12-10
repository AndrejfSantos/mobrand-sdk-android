package com.mobrand.appwall.us;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.mobrand.appwall.A.AppWall;
import com.mobrand.sdk.core.DeviceInfo;
import com.mobrand.sdk.core.MobrandCallback;
import com.mobrand.sdk.core.MobrandCore;
import com.mobrand.sdk.core.Utils;
import com.mobrand.sdk.core.model.Config;

public class MainActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppWall.start(this, "App Wall");

    }
}