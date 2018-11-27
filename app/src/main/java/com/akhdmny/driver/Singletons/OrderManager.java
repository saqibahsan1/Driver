package com.android.akhdmny.Singletons;

import com.google.firebase.database.ValueEventListener;

public class OrderManager {

    private static OrderManager shared;

    private OrderManager(){}  //private constructor.

    public ValueEventListener observer;


    public static OrderManager getInstance(){
        if (shared == null){ //if there is no instance available... create new one
            shared = new OrderManager();
        }

        return shared;
    }

    public Boolean isObserverRunning(){
        if (this.observer == null){
            return false;
        }else{
            return true;
        }
    }

}
