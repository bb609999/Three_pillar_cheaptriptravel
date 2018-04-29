package com.example.three_pillar_cheaptriptravel.object;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 7/3/2018.
 */

public class Schedule extends DataSupport implements Serializable {

    private int id;

    private String name;

    private int imageId;

    private String date;

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

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", imageId=" + imageId +
                ", date='" + date + '\'' +
                '}';
    }

    public static Schedule getSchedule(int schedule_id){
        Schedule schedule = DataSupport.where("id=?", "" + schedule_id).findFirst(Schedule.class);
        return  schedule;
    }


}

