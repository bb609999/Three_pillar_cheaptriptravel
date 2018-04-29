package com.example.three_pillar_cheaptriptravel.object;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    private int order;

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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
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
                ", order=" + order +
                '}';
    }

    public Calendar getDateInCalendar(){
        Calendar event_date = Calendar.getInstance();

        String[] dateList = date.split("/");
        event_date.set(Calendar.DAY_OF_MONTH,Integer.valueOf(dateList[0]));
        event_date.set(Calendar.MONTH,Integer.valueOf(dateList[1])-1);
        event_date.set(Calendar.YEAR,Integer.valueOf(dateList[2]));

        return event_date;
    }

    public Place getPlace(){
        Place place = DataSupport.where("id=?",""+getPlace_id()).findFirst(Place.class);
        return place;

    }

    public boolean exceedOpeningHour(String OpeningHour){

        Place place = getPlace();


        int start_hour = (int)startTime;
        int start_minute = (int) Math.round((startTime-start_hour)*60.0);
        int end_hour = (int)endTime;
        int end_minute = (int) Math.round((endTime-end_hour)*60.0);

        if(OpeningHour!="No Information") {
            if (start_hour < Integer.valueOf(OpeningHour.substring(0, 2)) || end_hour > Integer.valueOf(OpeningHour.substring(5, 7))) {
                return true;
            }
            if (start_hour == Integer.valueOf(OpeningHour.substring(0, 2)) || end_hour == Integer.valueOf(OpeningHour.substring(5, 7))) {
                if (start_minute < Integer.valueOf(OpeningHour.substring(2, 4)) || end_minute > Integer.valueOf(OpeningHour.substring(7, 9))) {
                    return true;

                }
            }
        }

        return false;
    }

    public void addDate(int numOfDate){

        Date date1 = new Date();

        try {
            date1 = new SimpleDateFormat("dd/MM/yyyy").parse(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date1);
        c.add(Calendar.DATE, numOfDate);  // number of days to add
        date = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
        this.save();
    }

    public static List<Event> getEvents(int schedule_id){

        List<Event> eventList = DataSupport.where("Schedule_id=?", "" + schedule_id).find(Event.class);

        return eventList;
    }

    public static List<Event> getEventsByDate(int schedule_id,String dateString){
        List<Event> eventList = DataSupport.where("Schedule_id=? AND date=?", "" + schedule_id,dateString).find(Event.class);
        return eventList;
    }




}
