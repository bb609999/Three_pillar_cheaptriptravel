package com.example.three_pillar_cheaptriptravel.object;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 1/5/2018.
 */

public class Travel extends DataSupport {

    private String origin;

    private String destination;

    private int distanceInKms;

    private double startTime;

    private double endTime;

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getDistanceInKms() {
        return distanceInKms;
    }

    public void setDistanceInKms(int distanceInKms) {
        this.distanceInKms = distanceInKms;
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

    @Override
    public String toString() {
        return "Travel{" +
                "origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", distanceInKms=" + distanceInKms +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    public double getDuration(){
        return endTime-startTime;
    }


}
