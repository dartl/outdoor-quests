package ru.gawk.historygeocachingdemo.models;

/**
 * Created by GAWK on 02.04.2017.
 */

public class Mark {
    private double mLatitude;
    private double mLongitude;

    public Mark(double mLatitude, double mLongitude) {
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }
}
