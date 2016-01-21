package com.mobrand.appwall.classic;

import android.content.Context;

import com.mobrand.appwall.event.MobrandLifecycle;

/**
 * Created by rmateus on 21/01/16.
 */
public class AppwallFactory {


    public static AppwallBuilder createAppwall(Context context, String placement){

        return new AppwallBuilder(placement, context);

    }

    public static class AppwallBuilder{

        private String placement;
        private Context context;
        private MobrandLifecycle lifecycle;

        public AppwallBuilder(String placement, Context context) {
            this.placement = placement;
            this.context = context;
        }

        public AppwallBuilder setPlacement(String placement) {
            this.placement = placement;
            return this;
        }

        public AppwallBuilder setContext(Context context) {
            this.context = context;
            return this;
        }

        public AppwallBuilder setLifecycle(MobrandLifecycle lifecycle) {
            this.lifecycle = lifecycle;
            return this;
        }

        public void start(){
            AppWall.start(context, placement, lifecycle);
        }
    }


}
