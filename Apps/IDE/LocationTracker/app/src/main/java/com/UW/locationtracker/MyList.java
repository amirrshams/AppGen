package com.UW.locationtracker;
import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class MyList extends Application{

    private static MyList singleton;

    private List<Location> myLocations;

    public List<Location> getMyLocations() {
        return myLocations;
    }

    public void setMyLocations(List<Location> myLocations) {
        this.myLocations = myLocations;
    }

    public MyList getInstance(){
        return singleton;
    }

    public void onCreate() {
        super.onCreate();
        singleton = this;
        myLocations = new ArrayList<>();

    }
}
