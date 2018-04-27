package com.example.three_pillar_cheaptriptravel.object;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 23/4/2018.
 */

public class EventManager {

    public static void arrangeEvent(List<Event> eventList,int schedule_id){

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
                time_pointer+=DistanceList.get(i)/10;
            }

            eventList.get(i).setStartTime(new_startTime);
            eventList.get(i).setEndTime(new_EndTime);
            eventList.get(i).save();
        }

    }






}
