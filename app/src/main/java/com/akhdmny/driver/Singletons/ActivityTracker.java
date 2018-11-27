package com.android.akhdmny.Singletons;

public class ActivityTracker {

    private static ActivityTracker shared;

    private ActivityTracker(){}  //private constructor.

    public static ActivityTracker getInstance(){
        if (shared == null){ //if there is no instance available... create new one
            shared = new ActivityTracker();
        }

        return shared;
    }

    

}
