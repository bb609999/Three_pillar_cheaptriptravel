package com.example.three_pillar_cheaptriptravel.object;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 7/3/2018.
 */

public class Event extends DataSupport implements Serializable{

    private int id;

    private String placeName;

    private String type;

    private double startTime;

    private double endTime;

    private String description;

    private int Schedule_id;

    public Event() {
    }

    public Event(String placeName, String type, double startTime, double endTime, String description) {
        this.placeName = placeName;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }

    public String getPlaceName() {
        return placeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSchedule_id() {
        return Schedule_id;
    }

    public void setSchedule_id(int schedule_id) {
        Schedule_id = schedule_id;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
