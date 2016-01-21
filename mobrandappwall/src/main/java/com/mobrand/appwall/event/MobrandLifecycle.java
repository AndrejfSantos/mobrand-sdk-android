package com.mobrand.appwall.event;

import de.greenrobot.event.EventBus;

/**
 * Created by rmateus on 21/01/16.
 */
public abstract class MobrandLifecycle {

    public enum EnumLifecycle{
        DESTROY, CREATE, ADSRECEIVED
    }

    final public void onEvent(Event event){
        onLifecycleEvent(event);
        switch (event.getLifecycle()){
            case DESTROY:
                EventBus.getDefault().unregister(this);
                break;
        }

    }

    public abstract void onLifecycleEvent(Event event);

    public static class Event{

        private EnumLifecycle lifecycle;

        public Event(EnumLifecycle lifecycle) {
            this.lifecycle = lifecycle;
        }

        public EnumLifecycle getLifecycle() {
            return lifecycle;
        }
    }


}
