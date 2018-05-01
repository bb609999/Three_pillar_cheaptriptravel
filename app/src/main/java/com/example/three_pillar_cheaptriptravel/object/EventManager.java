package com.example.three_pillar_cheaptriptravel.object;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 23/4/2018.
 */

public class EventManager {

    public static void arrangeEvent(List<Event> eventList, int schedule_id){

        List<Double> DistanceList = new ArrayList<>();
        Schedule schedule = Schedule.getSchedule(schedule_id);

        for(int i=0;i<eventList.size()-1;i++) {
            Place place_1 = eventList.get(i).getPlace();
            Place place_2 = eventList.get(i+1).getPlace();

            double distance = Place.getDistance(place_1.getLat(),place_1.getLng(),place_2.getLat(),place_2.getLng());

            DistanceList.add(distance);
        }

        Double time_pointer = schedule.getGetUpTime();

        for(int i=0;i<eventList.size();i++){
            double new_startTime =  time_pointer ;
            double new_EndTime = new_startTime+2;
            time_pointer = new_EndTime;

            if(i<eventList.size()-1){
                //500m = 0.5km = 0.5hour = 30min / 10km/hour =

                if(DistanceList.get(i)<1.25){
                    time_pointer+=DistanceList.get(i)/(3.0);
                }else {
                    time_pointer+=DistanceList.get(i)/(10.0);
                }


            }

            eventList.get(i).setStartTime(new_startTime);
            eventList.get(i).setEndTime(new_EndTime);
            eventList.get(i).save();
        }

    }

    public static void arrangeEventByCluster(Schedule schedule,List<Event> events,int RadiusInMeters){
        ArrayList<ArrayList<Event>> clusterList = EventLocationCluster.clusterEvent(events,RadiusInMeters);


        for(int i=0;i<clusterList.size();i++){
            for(Event event:clusterList.get(i)){
               event.setDate(schedule.getDate());


               event.addDate(i);


               event.save();
                Log.d("belongtoCluster", ""+i);
                Log.d("event", ""+event.getPlaceName());
            }
        }
    }

    public static void arrangeTimeAllDay(int schedule_id){
        //loop 10

        Schedule schedule = Schedule.getSchedule(schedule_id);

        for(int i=0;i<10;i++) {
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

            try {

                date = format.parse(schedule.getDate());
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.DATE, i);
                date = c.getTime();
            }catch (Exception e){
                e.printStackTrace();
            }

            String dateString = format.format(date);

            List<Event> eventListWithSameDay =  Event.getEventsByDate(schedule_id,dateString);



            EventManager.arrangeEvent(eventListWithSameDay,schedule_id);

        }

    }

    public static void arrangeTimeOneDay(int schedule_id,int day) {
        //loop 10

        Schedule schedule = Schedule.getSchedule(schedule_id);


        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        try {

            date = format.parse(schedule.getDate());
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE, day - 1);
            date = c.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String dateString = format.format(date);

        List<Event> eventListWithSameDay = Event.getEventsByDate(schedule_id, dateString);


        EventManager.arrangeEvent(eventListWithSameDay,schedule_id);
    }










}
