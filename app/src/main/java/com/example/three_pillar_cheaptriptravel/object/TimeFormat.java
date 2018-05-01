package com.example.three_pillar_cheaptriptravel.object;

/**
 * Created by Administrator on 1/5/2018.
 */

public class TimeFormat {

    public static int getHourOfTime(double time){
        return (int)time;
    }

    public static int getMinuteOfTime(double time){
        return (int)(time - getHourOfTime(time)) *60;
    }

    public static double getTimeFromHM(int hours,int minutes){
        return hours+(minutes/60.0);
    }

    public static String formatTime(int hour,int minute){
            String hour_formatted = hour < 10 ? "0" + hour : "" + hour;
            String minute_formatted = minute < 10 ? "0" + minute : "" + minute;

            return hour_formatted + ":" + minute_formatted;
    }

}
