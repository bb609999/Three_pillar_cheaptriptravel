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

    private String date;

    private int Schedule_id;

    private int Place_id;

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

    public int getPlace_id() {
        return Place_id;
    }

    public void setPlace_id(int place_id) {
        Place_id = place_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", placeName='" + placeName + '\'' +
                ", type='" + type + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", Schedule_id=" + Schedule_id +
                ", Place_id=" + Place_id +
                '}';
    }
}
