package com.example.three_pillar_cheaptriptravel.object;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 23/4/2018.
 */

public class EventManager {

    public static void arrangeEvent(List<Event> eventList){

        List<Double> DistanceList = new ArrayList<>();

        for(int i=0;i<eventList.size()-1;i++) {
            Place place_1 = eventList.get(i).getPlace();
            Place place_2 = eventList.get(i+1).getPlace();

            double distance = Place.getDistance(place_1.getLat(),place_1.getLng(),place_2.getLat(),place_2.getLng());

            DistanceList.add(distance);
        }

        Double time_pointer = 9.0;

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






}
