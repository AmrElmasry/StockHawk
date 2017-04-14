package com.udacity.stockhawk.models;

/**
 * Created by Amr on 14/04/17.
 */

public class StockHistory {
    private long timeStampInMillis;
    private float value;

    public StockHistory(long timeStampInMillis, float value) {
        this.timeStampInMillis = timeStampInMillis;
        this.value = value;
    }


    public long getTimeStampInMillis() {
        return timeStampInMillis;
    }

    public float getValue() {
        return value;
    }
}