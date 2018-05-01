package com.example.three_pillar_cheaptriptravel.object;

import android.util.Log;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 7/3/2018.
 */

public class Schedule extends DataSupport implements Serializable {

    private int id;

    private String name;

    private int imageId;

    private String date;

    private int hotel_id;

    private double getUpTime;

    private double backTime;

    public Schedule() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Schedule(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(int hotel_id) {
        this.hotel_id = hotel_id;
    }

    public double getGetUpTime() {
        return getUpTime;
    }

    public void setGetUpTime(double getUpTime) {
        this.getUpTime = getUpTime;
    }

    public double getBackTime() {
        return backTime;
    }

    public void setBackTime(double backTime) {
        this.backTime = backTime;
    }

    public Schedule(int id, String name, int imageId, String date, int hotel_id, double getUpTime, double backTime) {
        this.id = id;
        this.name = name;
        this.imageId = imageId;
        this.date = date;
        this.hotel_id = hotel_id;
        this.getUpTime = getUpTime;
        this.backTime = backTime;
    }

    public static Schedule getSchedule(int schedule_id) {
        Schedule schedule = DataSupport.where("id=?", "" + schedule_id).findFirst(Schedule.class);
        return schedule;
    }

    public int getMaxDays() {
        List<Event> eventList = Event.getEvents(id);

        Log.d("daylist", "getMaxDays: " + eventList.size());

        List<Integer> dayList = new ArrayList<>();
        dayList.add(0);

        for (Event event : eventList) {
            dayList.add(event.getDays());
        }


        return Collections.max(dayList);

    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", imageId=" + imageId +
                ", date='" + date + '\'' +
                ", hotel_id=" + hotel_id +
                ", getUpTime=" + getUpTime +
                ", backTime=" + backTime +
                '}';
    }
}




