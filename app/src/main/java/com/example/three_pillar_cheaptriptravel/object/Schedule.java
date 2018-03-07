package com.example.three_pillar_cheaptriptravel.object;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 7/3/2018.
 */

public class Schedule extends DataSupport implements Serializable {

    private String name;

    private int imageId;


    private List<Event> eventList = new ArrayList<>();

    public List<Event> getEventList() {
        return eventList;
    }

    public Schedule(String name, int imageId, List<Event> eventList) {
        this.name = name;
        this.imageId = imageId;
        this.eventList = eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
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
}

