package com.holding.counter;

import com.google.gson.annotations.SerializedName;

public class CounterModel {

    @SerializedName("TIME")
    public long time;

    @SerializedName("T1")
    public String t1;

    @SerializedName("T2")
    public String t2;

    @SerializedName("T3")
    public String t3;

    @SerializedName("T4")
    public String t4;

    @SerializedName("T5")
    public String t5;

    @SerializedName("ADC")
    public String adc;

    @SerializedName("Ch1")
    public String ch1;

    @SerializedName("Ch2")
    public String ch2;

    @SerializedName("TCh1")
    public String tch1;

    @SerializedName("TCh2")
    public String tch2;

    @SerializedName("OUT")
    public int OUT;

    public String getTimeString() {
        return new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(new java.util.Date (time*1000));
    }
}
